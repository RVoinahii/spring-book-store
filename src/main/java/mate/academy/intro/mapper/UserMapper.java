package mate.academy.intro.mapper;

import java.util.List;
import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.user.UserRegistrationRequestDto;
import mate.academy.intro.dto.user.UserResponseDto;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    UserResponseDto toDto(User user);

    @AfterMapping
    default void setRoles(@MappingTarget UserResponseDto responseDto, User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .toList();
        responseDto.setRoles(roles);
    }

    User toModel(UserRegistrationRequestDto registrationRequestDto);

}
