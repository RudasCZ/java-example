package com.homework.morosystems.mapper;

import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import com.homework.morosystems.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_shouldMapEntityToDto() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setName("Alice");

        UserGetDto dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Alice");
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        UserCreateUpdateDto dto = new UserCreateUpdateDto();
        dto.setName("Bob");

        UserEntity entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Bob");
    }

    @Test
    void updateEntityFromDto_shouldUpdateEntityFromDto() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setName("Old");

        UserCreateUpdateDto dto = new UserCreateUpdateDto();
        dto.setName("New");

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("New");
    }
}