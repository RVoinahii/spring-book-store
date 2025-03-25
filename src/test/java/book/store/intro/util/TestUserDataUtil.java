package book.store.intro.util;

import static book.store.intro.util.TestBookDataUtil.DEFAULT_ID_SAMPLE;

import book.store.intro.dto.user.UserRegistrationRequestDto;
import book.store.intro.dto.user.UserResponseDto;
import book.store.intro.model.Role;
import book.store.intro.model.Role.RoleName;
import book.store.intro.model.User;
import java.util.Set;

public class TestUserDataUtil {
    public static final String USER_EMAIL = "mail.example@gmail.com";
    public static final String USER_PASSWORD = "password";
    public static final String USER_HASHED_PASSWORD =
            "$2a$10$40VcEX9yypgQXTJ0CL/oteEJuB03CJ0lGzxmhB1ZlsDDLL5LnHbga";
    public static final String USER_FIRST_NAME = "FirstName";
    public static final String USER_LAST_NAME = "LastName";
    public static final String USER_AUTHORITY = "USER";
    public static final String ADMIN_AUTHORITY = "ADMIN";

    public static UserRegistrationRequestDto createRegistrationRequestDtoSample() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(USER_EMAIL);
        requestDto.setPassword(USER_PASSWORD);
        requestDto.setRepeatPassword(USER_PASSWORD);
        requestDto.setFirstName(USER_FIRST_NAME);
        requestDto.setLastName(USER_LAST_NAME);
        return requestDto;
    }

    public static User createUserSampleFromRequest(UserRegistrationRequestDto requestDto) {
        User user = new User();
        user.setId(DEFAULT_ID_SAMPLE);
        user.setEmail(requestDto.getEmail());
        user.setPassword(USER_HASHED_PASSWORD);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());

        Role role = new Role();
        role.setId(DEFAULT_ID_SAMPLE);
        role.setRole(RoleName.USER);

        user.setRoles(Set.of(role));
        return user;
    }

    public static User createDefaultUserSample() {
        User user = new User();
        user.setId(DEFAULT_ID_SAMPLE);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_HASHED_PASSWORD);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);

        Role role = new Role();
        role.setId(DEFAULT_ID_SAMPLE);
        role.setRole(RoleName.USER);

        user.setRoles(Set.of(role));
        return user;
    }

    public static UserResponseDto createUserResponseDtoSampleFromEntity(
            User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        return responseDto;
    }

    public static UserResponseDto createDefaultUserResponseDtoSample() {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(DEFAULT_ID_SAMPLE);
        responseDto.setEmail(USER_EMAIL);
        responseDto.setFirstName(USER_FIRST_NAME);
        responseDto.setLastName(USER_LAST_NAME);
        return responseDto;
    }
}
