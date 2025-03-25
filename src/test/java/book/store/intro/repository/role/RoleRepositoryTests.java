package book.store.intro.repository.role;

import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.intro.model.Role;
import book.store.intro.model.Role.RoleName;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("""
            findByRole():
             Should return role 'ADMIN' when a valid role name is provided
            """)
    void findByRole_Admin_ShouldReturnRole() {
        //Given
        RoleName admin = RoleName.ADMIN;
        Role expectedRole = new Role();
        expectedRole.setRole(admin);

        //When
        Optional<Role> actualRole = roleRepository.findByRole(admin);

        //Given
        assertTrue(actualRole.isPresent(), "Role 'ADMIN' should be present");
        assertTrue(EqualsBuilder.reflectionEquals(
                actualRole.get(), expectedRole, "id"));
    }

    @Test
    @DisplayName("""
            findByRole():
             Should return role 'USER' when a valid role name is provided
            """)
    void findByRole_User_ShouldReturnRole() {
        //Given
        RoleName user = RoleName.USER;
        Role expectedRole = new Role();
        expectedRole.setRole(user);

        //When
        Optional<Role> actualRole = roleRepository.findByRole(user);

        //Given
        assertTrue(actualRole.isPresent(), "Role 'USER' should be present");
        assertTrue(EqualsBuilder.reflectionEquals(
                actualRole.get(), expectedRole, "id"));
    }
}
