package mate.academy.intro;

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
                title = "SpringBook Store",
                version = "0.7.5",
                description = "This project in only for learning!",
                contact = @Contact(
                        name = "Roman Voynahiy",
                        email = "romanvoynahiy@gmail.com"
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
