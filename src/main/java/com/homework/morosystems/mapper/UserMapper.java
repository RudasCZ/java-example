package com.homework.morosystems.mapper;

import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import com.homework.morosystems.repository.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {

    UserGetDto toDto(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UserCreateUpdateDto dto, @MappingTarget UserEntity entity);
}
