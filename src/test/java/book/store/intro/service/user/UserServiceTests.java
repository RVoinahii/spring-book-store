package book.store.intro.service.user;

import static book.store.intro.util.TestUserDataUtil.USER_HASHED_PASSWORD;
import static book.store.intro.util.TestUserDataUtil.createRegistrationRequestDtoSample;
import static book.store.intro.util.TestUserDataUtil.createUserResponseDtoSampleFromEntity;
import static book.store.intro.util.TestUserDataUtil.createUserSampleFromRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.intro.dto.user.UserRegistrationRequestDto;
import book.store.intro.dto.user.UserResponseDto;
import book.store.intro.exceptions.EntityNotFoundException;
import book.store.intro.exceptions.RegistrationException;
import book.store.intro.mapper.UserMapper;
import book.store.intro.model.Role;
import book.store.intro.model.Role.RoleName;
import book.store.intro.model.User;
import book.store.intro.repository.role.RoleRepository;
import book.store.intro.repository.user.UserRepository;
import book.store.intro.service.shopping.cart.ShoppingCartServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegistrationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = createRegistrationRequestDtoSample();
    }

    @Test
    @DisplayName("""
            register():
             Should return UserResponseDto when valid registration request is provided
            """)
    void register_ValidRegistrationRequest_ReturnResponseDto() {
        //Given
        Long expectedUserId = 1L;

        User user = createUserSampleFromRequest(requestDto);
        user.setId(expectedUserId);

        Role userRole = new Role();
        userRole.setRole(RoleName.USER);

        UserResponseDto expectedResponseDto = createUserResponseDtoSampleFromEntity(user);

        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(USER_HASHED_PASSWORD);
        when(roleRepository.findByRole(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(shoppingCartService).createShoppingCartForUser(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponseDto);

        //When
        UserResponseDto actualResponseDto = userService.register(requestDto);

        //Then
        assertThat(actualResponseDto).isEqualTo(expectedResponseDto);
        verify(userMapper).toEntity(requestDto);
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(passwordEncoder).encode(requestDto.getPassword());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("""
            register():
             Should throw RegistrationException when email is already registered
            """)
    void register_EmailAlreadyExists_ShouldThrowException() {
        //Given
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        //When
        Exception exception = assertThrows(
                RegistrationException.class, () -> userService.register(requestDto)
        );

        //Then
        String expected = "User with email:" + requestDto.getEmail()
                + " is already exist";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("""
            register():
             Should throw EntityNotFoundException when role is not found
            """)
    void register_RoleNotFound_ShouldThrowException() {
        //Given
        User user = createUserSampleFromRequest(requestDto);

        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(USER_HASHED_PASSWORD);
        when(roleRepository.findByRole(RoleName.USER)).thenReturn(Optional.empty());

        //Then
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> userService.register(requestDto)
        );

        //Then
        String expected = "Role with name '" + RoleName.USER + "' not found";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).toEntity(requestDto);
        verify(passwordEncoder).encode(requestDto.getPassword());
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
