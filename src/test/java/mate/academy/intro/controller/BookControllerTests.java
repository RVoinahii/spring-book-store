package mate.academy.intro.controller;

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
import java.math.BigDecimal;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
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

    private static final String BOOK_TITLE = "BookOne";
    private static final String BOOK_AUTHOR = "AuthorOne";
    private static final String BOOK_ISBN = "978-3-16-148410-0";
    private static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(9.99);

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TestRestTemplate restTemplate;

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
        BookDto expectedBookDto = createBookDtoSample();
        expectedBookDto.setId(1L);

        //When
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        PageResponse<BookDto> actualPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualPage.getSize());
        EqualsBuilder.reflectionEquals(actualPage.getContent().getFirst(), expectedBookDto);
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
        BookDto expected = createBookDtoSample();
        expected.setId(1L);
        //When
        MvcResult result = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    @Test
    @DisplayName("""
            search():
             Testing search functionality for books with valid query parameters
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_three_books.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_three_books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void search_WithValidParameters_Success() throws Exception {
        //Given
        String expectedIsbn = "978-3-16-148410-2";
        Long expectedId = 3L;

        BookDto expectedBookDto = createBookDtoSample();
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
        PageResponse<BookDto> actualPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualPage.getSize());
        EqualsBuilder.reflectionEquals(actualPage.getContent().getFirst(), expectedBookDto);
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
        BookDto expected = createBookDtoSample();

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
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
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

        BookDto expected = createBookDtoSample();
        expected.setId(1L);
        expected.setTitle("NewTitle");
        expected.setAuthor("NewAuthor");

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
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual);
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

        //When
        MvcResult result = mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        mockMvc.perform(get("/books/1")).andExpect(status().isNotFound());
    }

    private CreateBookRequestDto createBookRequestDtoSample() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR);
        requestDto.setIsbn(BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setCategories(List.of(1L));
        return requestDto;
    }

    private BookDto createBookDtoSample() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setIsbn(BOOK_ISBN);
        bookDto.setPrice(BOOK_PRICE);
        bookDto.setCategoryIds(List.of(1L));
        return bookDto;
    }
}
