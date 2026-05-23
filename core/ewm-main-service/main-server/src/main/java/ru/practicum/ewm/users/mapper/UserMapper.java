package ru.practicum.ewm.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(NewUserDto newUserDto);

    UserDto toUserDto(User userEntity);
}
