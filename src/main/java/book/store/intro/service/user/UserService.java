package book.store.intro.service.user;

import book.store.intro.dto.user.UserRegistrationRequestDto;
import book.store.intro.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto registrationRequestDto);
}
