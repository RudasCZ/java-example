package com.homework.morosystems.mapper;

import com.homework.morosystems.model.UserDto;
import com.homework.morosystems.repository.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    UserDto toDto(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntityPartially(UserDto userDto);
}
