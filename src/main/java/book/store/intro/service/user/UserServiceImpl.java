package book.store.intro.service.user;

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
import book.store.intro.service.shopping.cart.ShoppingCartService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
