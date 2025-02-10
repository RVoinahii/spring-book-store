package mate.academy.intro.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.user.UserRegistrationRequestDto;
import mate.academy.intro.dto.user.UserResponseDto;
import mate.academy.intro.exceptions.RegistrationException;
import mate.academy.intro.mapper.UserMapper;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email:" + requestDto.getEmail()
                    + " is already exist");
        }
        User user = userMapper.toModel(requestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
