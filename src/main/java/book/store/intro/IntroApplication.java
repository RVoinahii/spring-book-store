package book.store.intro;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Spring Book Store",
                version = "1.0.0",
                description = "A modern, feature-rich online bookstore built with Spring Boot."
                        + " This project showcases the power of RESTful APIs,"
                        + " authentication, and CRUD operations. Perfect for learning about web"
                        + " development and microservices architecture.",
                contact = @Contact(
                        name = "Roman Voynahiy"
                )
        )
)
@SecurityScheme(
        name = "BearerAuthentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class IntroApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }
}
