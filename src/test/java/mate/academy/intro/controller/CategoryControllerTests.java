package mate.academy.intro.controller;

import static mate.academy.intro.util.TestDataUtil.PAGE_SIZE;
import static mate.academy.intro.util.TestDataUtil.createCategoryRequestDtoSample;
import static mate.academy.intro.util.TestDataUtil.createDefaultBookWithoutCategoriesDtoSample;
import static mate.academy.intro.util.TestDataUtil.createDefaultCategoryDtoSample;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import mate.academy.intro.model.PageResponse;
import mate.academy.intro.service.category.CategoryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            getAllCategories():
             Verifying retrieval of all categories with correct pagination parameters
            """)
    @Sql(scripts = "classpath:database/categories/insert_one_category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllCategories_ValidPageable_Success() throws Exception {
        //Given
        CategoryDto expectedCategoryDto = createDefaultCategoryDtoSample();
        expectedCategoryDto.setId(1L);

        //When
        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<CategoryDto> actualPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualPage.getSize());
        EqualsBuilder.reflectionEquals(
                actualPage.getContent().getFirst(), expectedCategoryDto, "description");
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            getCategoryById():
             Verifying retrieval of a category by its ID
            """)
    @Sql(scripts = "classpath:database/categories/insert_one_category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_ValidId_Success() throws Exception {
        //Given
        CategoryDto expected = createDefaultCategoryDtoSample();
        expected.setId(1L);
        //When
        MvcResult result = mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "description");
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            getBooksByCategoryId():
             Verifying retrieval of valid books with correct
              pagination parameters and valid category ID
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_three_categories.sql",
            "classpath:database/books/insert_three_books.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_three_books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_WithValidParametersAndCategoryId_Success() throws Exception {
        //Given
        Long expectedBookId = 1L;

        BookWithoutCategoriesDto expectedBookDto = createDefaultBookWithoutCategoriesDtoSample();
        expectedBookDto.setId(expectedBookId);

        //When
        MvcResult result = mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<BookWithoutCategoriesDto> actualPage = objectMapper
                .readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {});

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualPage.getSize());
        EqualsBuilder.reflectionEquals(actualPage.getContent().getFirst(), expectedBookDto);
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            createCategory():
             Confirming successful creation of a category with valid request
            """)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Success() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        CategoryDto expected = createDefaultCategoryDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "description");
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            updateCategoryById():
             Verifying updating category data by ID with valid request
            """)
    @Sql(scripts = "classpath:database/categories/insert_one_category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategoryById_ValidRequestDtoAndId_Success() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        CategoryDto expected = createDefaultCategoryDtoSample();
        expected.setId(1L);
        expected.setName(requestDto.name());
        expected.setDescription(requestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        put("/categories/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            deleteCategory():
             Verifying successful category removal by its ID
            """)
    @Sql(scripts = "classpath:database/categories/insert_one_category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_ValidId_Success() throws Exception {
        //When
        MvcResult result = mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        mockMvc.perform(get("/categories/1")).andExpect(status().isNotFound());
    }
}
