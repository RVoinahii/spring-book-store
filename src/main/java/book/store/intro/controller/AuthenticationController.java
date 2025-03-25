package book.store.intro.controller;

import book.store.intro.dto.user.UserLoginRequestDto;
import book.store.intro.dto.user.UserLoginResponseDto;
import book.store.intro.dto.user.UserRegistrationRequestDto;
import book.store.intro.dto.user.UserResponseDto;
import book.store.intro.security.AuthenticationService;
import book.store.intro.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for managing authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with the provided parameters"
    )
    public UserResponseDto registerUser(
            @RequestBody @Valid UserRegistrationRequestDto userRequestDto) {
        return userService.register(userRequestDto);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate existing user",
            description = "Authenticate existing user with the provided parameters"
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
