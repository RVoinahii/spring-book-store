package mate.academy.intro;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.service.BookService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class IntroApplication {
    private final BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }
}
