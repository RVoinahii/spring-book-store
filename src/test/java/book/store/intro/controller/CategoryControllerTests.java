package book.store.intro.controller;

import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static book.store.intro.util.TestBookDataUtil.createDefaultBookWithoutCategoriesDtoSample;
import static book.store.intro.util.TestCategoryDataUtil.createCategoryRequestDtoSample;
import static book.store.intro.util.TestCategoryDataUtil.createDefaultCategoryDtoSample;
import static book.store.intro.util.TestUserDataUtil.ADMIN_AUTHORITY;
import static book.store.intro.util.TestUserDataUtil.USER_AUTHORITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import book.store.intro.model.PageResponse;
import book.store.intro.service.category.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
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
        Long expectedCategoryId = 1L;

        CategoryDto expectedCategoryDto = createDefaultCategoryDtoSample();
        expectedCategoryDto.setId(expectedCategoryId);

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
        assertTrue(EqualsBuilder.reflectionEquals(
                actualPage.getContent().getFirst(), expectedCategoryDto));

    }

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
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
        Long expectedCategoryId = 1L;

        CategoryDto expected = createDefaultCategoryDtoSample();
        expected.setId(expectedCategoryId);
        //When
        MvcResult result = mockMvc.perform(get("/categories/{categoryId}", expectedCategoryId))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
    @Test
    @DisplayName("""
        getCategoryById():
         Should return 404 NOT FOUND when given invalid ID
            """)
    void getCategoryById_InvalidId_NotFound() throws Exception {
        //Given
        Long invalidId = 99L;

        // When & Then
        mockMvc.perform(get("/categories/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
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
        Long expectedCategoryId = 1L;

        BookWithoutCategoriesDto expectedBookDto = createDefaultBookWithoutCategoriesDtoSample();
        expectedBookDto.setId(expectedBookId);

        //When
        MvcResult result = mockMvc.perform(get("/categories/{categoryId}/books",
                        expectedCategoryId))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<BookWithoutCategoriesDto> actualPage = objectMapper
                .readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {});

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualPage.getSize());
        assertTrue(EqualsBuilder.reflectionEquals(
                actualPage.getContent().getFirst(), expectedBookDto));
    }

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
    @Test
    @DisplayName("""
        getBooksByCategoryId():
         Should return 404 NOT FOUND when given invalid ID
            """)
    void getBooksByCategoryId_InvalidBookId_NotFound() throws Exception {
        //Given
        Long invalidId = 99L;

        // When & Then
        mockMvc.perform(get("/categories/{categoryId}/books", invalidId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = ADMIN_AUTHORITY)
    @Test
    @DisplayName("""
            createCategory():
             Confirming successful creation of a category with valid request
            """)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Created() throws Exception {
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
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", authorities = ADMIN_AUTHORITY)
    @Test
    @DisplayName("""
            createCategory():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    void createCategory_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(null, null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user", authorities = USER_AUTHORITY)
    @Test
    @DisplayName("""
         createCategory():
         Should return 403 FORBIDDEN when user doesn't have authority 'ADMIN'
            """)
    void createCategory_UserWithoutRequiredAuthority_Forbidden() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = ADMIN_AUTHORITY)
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
        Long expectedCategoryId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        CategoryDto expected = createDefaultCategoryDtoSample();
        expected.setId(expectedCategoryId);
        expected.setName(requestDto.name());
        expected.setDescription(requestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        put("/categories/{categoryId}", expectedCategoryId)
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
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "admin", authorities = {USER_AUTHORITY, ADMIN_AUTHORITY})
    @Test
    @DisplayName("""
        updateCategoryById():
         Should return 404 NOT FOUND when given invalid ID
            """)
    void updateCategoryByById_InvalidId_NotFound() throws Exception {
        //Given
        Long invalidId = 99L;

        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When & Then
        MvcResult result = mockMvc.perform(
                        put("/categories/{categoryId}", invalidId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = ADMIN_AUTHORITY)
    @Test
    @DisplayName("""
            updateCategoryById():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    void updateCategoryById_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        Long categoryId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(null, null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        put("/categories/{categoryId}", categoryId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user", authorities = USER_AUTHORITY)
    @Test
    @DisplayName("""
         updateCategoryById():
         Should return 403 FORBIDDEN when user doesn't have authority 'ADMIN'
            """)
    void updateCategoryById_UserWithoutRequiredAuthority_Forbidden() throws Exception {
        //Given
        Long categoryId = 1L;

        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        put("/categories/{categoryId}", categoryId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = ADMIN_AUTHORITY)
    @Test
    @DisplayName("""
            deleteCategory():
             Verifying successful category removal by its ID
            """)
    @Sql(scripts = "classpath:database/categories/insert_one_category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_ValidId_NoContent() throws Exception {
        //Given
        Long categoryId = 1L;

        //When
        MvcResult result = mockMvc.perform(delete("/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        mockMvc.perform(get(
                "/categories/{categoryId}", categoryId)).andExpect(status().isNotFound()
        );
    }

    @WithMockUser(username = "user", authorities = USER_AUTHORITY)
    @Test
    @DisplayName("""
         deleteCategory():
         Should return 403 FORBIDDEN when user doesn't have authority 'ADMIN'
            """)
    void deleteCategory_UserWithoutRequiredAuthority_Forbidden() throws Exception {
        //Given
        Long categoryId = 1L;

        //When & Then
        MvcResult result = mockMvc.perform(delete("/categories/{categoryId}", categoryId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
