package mate.academy.intro.controller;

import static mate.academy.intro.util.TestDataUtil.PAGE_SIZE;
import static mate.academy.intro.util.TestDataUtil.createBookRequestDtoSample;
import static mate.academy.intro.util.TestDataUtil.createDefaultBookDtoSample;
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
import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import mate.academy.intro.model.PageResponse;
import mate.academy.intro.service.book.BookService;
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
public class BookControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private BookService bookService;

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
            getAllBooks():
             Verifying retrieval of all books with correct pagination parameters
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllBooks_ValidPageable_Success() throws Exception {
        //Given
        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(1L);

        //When
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<BookDto> actualBookDtosPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualBookDtosPage);
        assertEquals(1, actualBookDtosPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualBookDtosPage.getSize());
        EqualsBuilder.reflectionEquals(actualBookDtosPage.getContent().getFirst(), expectedBookDto);
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            getById():
             Verifying retrieval of a book by its ID
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_ValidId_Success() throws Exception {
        //Given
        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(1L);
        //When
        MvcResult result = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto);
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            search():
             Testing search functionality for books with valid query parameters
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_three_categories.sql",
            "classpath:database/books/insert_three_books.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_three_books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void search_WithValidParameters_Success() throws Exception {
        //Given
        String expectedIsbn = "978-3-16-148410-2";
        Long expectedId = 3L;

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(expectedId);
        expectedBookDto.setIsbn(expectedIsbn);

        //When
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("title", "")
                        .param("author", "")
                        .param("isbn", expectedIsbn)
                        .param("bottomPrice", "")
                        .param("upperPrice", "")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<BookDto> actualBookDtosPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualBookDtosPage);
        assertEquals(1, actualBookDtosPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualBookDtosPage.getSize());
        EqualsBuilder.reflectionEquals(actualBookDtosPage.getContent().getFirst(), expectedBookDto);
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            createBook():
             Confirming successful creation of a book with valid request
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_Success() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createBookRequestDtoSample();
        BookDto expectedBookDto = createDefaultBookDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        assertNotNull(actualBookDto.getId());
        EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto, "id");
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            updateBookById():
             Verifying updating book data by ID with valid request
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookById_ValidRequestDtoAndId_Success() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createBookRequestDtoSample();
        requestDto.setTitle("NewTitle");
        requestDto.setAuthor("NewAuthor");

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(1L);
        expectedBookDto.setTitle("NewTitle");
        expectedBookDto.setAuthor("NewAuthor");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        put("/books/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        assertNotNull(actualBookDto.getId());
        EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto);
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            deleteBook():
             Verifying successful book removal by its ID
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Success() throws Exception {
        //When
        MvcResult result = mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        mockMvc.perform(get("/books/1")).andExpect(status().isNotFound());
    }
}
