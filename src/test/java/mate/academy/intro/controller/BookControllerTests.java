package mate.academy.intro.controller;

import static mate.academy.intro.util.TestBookDataUtil.PAGE_SIZE;
import static mate.academy.intro.util.TestBookDataUtil.createBookRequestDtoSample;
import static mate.academy.intro.util.TestBookDataUtil.createDefaultBookDtoSample;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

    @Test
    @DisplayName("""
        get/post/put/deleteMethods():
         Should return 401 UNAUTHORIZED when user is not authenticated
            """)
    void getPostPutDeleteMethods_UnauthorizedUser_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        Long bookId = 1L;

        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/books/search"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/books"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/books/{bookId}", bookId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/books/{bookId}", bookId))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", authorities = "USER")
    @Test
    @DisplayName("""
        post/put/deleteMethods():
         Should return 500 ACCESS DENIED when user doesn't have authority 'ADMIN'
            """)
    void postPutDeleteMethods_UserWithoutRequiredAuthority_ShouldReturnAccessDenied()
            throws Exception {
        //Given
        Long bookId = 1L;

        CreateBookRequestDto requestDto = createBookRequestDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult postResult = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andReturn();

        MvcResult putResult = mockMvc.perform(
                        put("/books/{bookId}", bookId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andReturn();

        MvcResult deleteResult = mockMvc.perform(delete("/books/{bookId}", bookId))
                .andExpect(status().isInternalServerError())
                .andReturn();
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
        Long expectedBookId = 1L;

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(expectedBookId);

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
        assertTrue(EqualsBuilder.reflectionEquals(
                actualBookDtosPage.getContent().getFirst(), expectedBookDto));
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            getBookById():
             Verifying retrieval of a book by its ID
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_ValidId_Success() throws Exception {
        //Given
        Long expectedBookId = 1L;

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(expectedBookId);
        //When
        MvcResult result = mockMvc.perform(get("/books/{bookId}", expectedBookId))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        assertTrue(EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto));
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
        getBookById():
         Should return 404 NOT FOUND when given invalid ID
            """)
    void getBookById_InvalidId_NotFound() throws Exception {
        //Given
        Long invalidId = 99L;

        // When & Then
        mockMvc.perform(get("/books/{bookId}", invalidId))
                .andExpect(status().isNotFound());
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
        Long expectedId = 3L;
        String expectedTitle = "BookThree";
        String expectedAuthor = "AuthorThree";
        String expectedIsbn = "978-3-16-148410-2";
        Long expectedCategoryId = 3L;

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(expectedId);
        expectedBookDto.setTitle(expectedTitle);
        expectedBookDto.setAuthor(expectedAuthor);
        expectedBookDto.setIsbn(expectedIsbn);
        expectedBookDto.setCategoryIds(List.of(expectedCategoryId));

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
        assertTrue(EqualsBuilder.reflectionEquals(
                actualBookDtosPage.getContent().getFirst(), expectedBookDto));
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
        assertTrue(EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto, "id"));
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            createBook():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    void createBook_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
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

        Long expectedBookId = 1L;

        BookDto expectedBookDto = createDefaultBookDtoSample();
        expectedBookDto.setId(expectedBookId);
        expectedBookDto.setTitle("NewTitle");
        expectedBookDto.setAuthor("NewAuthor");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        put("/books/{bookId}", expectedBookId)
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
        assertTrue(EqualsBuilder.reflectionEquals(actualBookDto, expectedBookDto));
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
        updateBookById():
         Should return 404 NOT FOUND when given invalid ID
            """)
    void updateBookByById_InvalidId_NotFound() throws Exception {
        //Given
        Long invalidId = 99L;

        CreateBookRequestDto requestDto = createBookRequestDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When & Then
        MvcResult result = mockMvc.perform(
                        put("/books/{bookId}", invalidId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = "ADMIN")
    @Test
    @DisplayName("""
            updateBookById():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    void updateBookById_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        Long bookId = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        put("/books/{bookId}", bookId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
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
        //Given
        Long bookId = 1L;

        //When
        MvcResult result = mockMvc.perform(delete("/books/{bookId}", bookId))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        mockMvc.perform(get("/books/{bookId}", bookId)).andExpect(status().isNotFound());
    }
}
