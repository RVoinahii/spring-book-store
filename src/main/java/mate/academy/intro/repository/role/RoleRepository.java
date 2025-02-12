package mate.academy.intro.repository.role;

import java.util.Optional;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleName role);
}
