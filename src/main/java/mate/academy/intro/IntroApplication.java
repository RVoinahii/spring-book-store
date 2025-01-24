package mate.academy.intro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.model.Book;
import mate.academy.intro.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@SpringBootApplication
public class IntroApplication {
    private final BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book bookOne = new Book();
            bookOne.setTitle("Harry Potter");
            bookOne.setAuthor("J.K Rowling");
            bookOne.setIsbn("978-545-0102");
            bookOne.setPrice(new BigDecimal(40));

            Book bookTwo = new Book();
            bookTwo.setTitle("The Great Gatsby");
            bookTwo.setAuthor("F. Scott Fitzgerald");
            bookTwo.setIsbn("978-545-0103");
            bookTwo.setPrice(new BigDecimal(15));

            Book bookThree = new Book();
            bookThree.setTitle("1984");
            bookThree.setAuthor("George Orwell");
            bookThree.setIsbn("978-545-0104");
            bookThree.setPrice(new BigDecimal(25));

            List<Book> bookList = new ArrayList<>();
            bookList.add(bookOne);
            bookList.add(bookTwo);
            bookList.add(bookThree);

            for (Book book : bookList) {
                bookService.save(book);
            }

            System.out.println(bookService.findAll());
        };
    }
}
