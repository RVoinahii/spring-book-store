package mate.academy.intro.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.user.UserRegistrationRequestDto;
import mate.academy.intro.dto.user.UserResponseDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.exceptions.RegistrationException;
import mate.academy.intro.mapper.UserMapper;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.Role.RoleName;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.role.RoleRepository;
import mate.academy.intro.repository.user.UserRepository;
import mate.academy.intro.service.shopping.cart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email:" + requestDto.getEmail()
                    + " is already exist");
        }
        User user = createUser(requestDto);
        shoppingCartService.createShoppingCartForUser(user);
        return userMapper.toDto(user);
    }

    private User createUser(UserRegistrationRequestDto requestDto) {
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByRole(RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role with name '" + RoleName.USER + "' not found")
                );
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }
}
