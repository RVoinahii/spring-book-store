package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.user.UserRegistrationRequestDto;
import book.store.intro.dto.user.UserResponseDto;
import book.store.intro.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto registrationRequestDto);
}
