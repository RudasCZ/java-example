package com.homework.morosystems.service;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.UserMapper;
import com.homework.morosystems.mapper.UserMapperImpl;
import com.homework.morosystems.model.UserDto;
import com.homework.morosystems.model.UserPageResponseDto;
import com.homework.morosystems.repository.UserEntity;
import com.homework.morosystems.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    private static final String TEST_NAME = "Alice";
    private static final long TEST_USER_ID = 99L;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void getUserById_shouldReturnUserDto() {
        UserEntity entity = mockEntity();

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getUserById(1L);

        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(TEST_USER_ID))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    void deleteUser_shouldDeleteIfExists() {
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);

        userService.deleteUser(TEST_USER_ID);

        verify(userRepository).existsById(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
    }

    @Test
    void deleteUser_shouldThrowIfNotExists() {
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(TEST_USER_ID))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).existsById(99L);
    }

    @Test
    void getUsersPageable_shouldReturnPage() {
        UserEntity entity = mockEntity();
        Page<UserEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        UserPageResponseDto result = userService.getUsersPageable(0, 1);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isZero();

        verify(userRepository).findAll(any(Pageable.class));
        verify(userMapper).toDto(entity);
    }

    @Test
    void updateUser_shouldMapAndSave() {
        UserDto dto = new UserDto().id(null).name("Updated");
        UserEntity entity = new UserEntity();
        entity.setName("Old");
        entity.setId(TEST_USER_ID);
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenReturn(entity);

        UserDto result = userService.updateUser(1L, dto);

        assertThat(result.getName()).isEqualTo("Updated");
        verify(userRepository).findById(1L);
        verify(userMapper).updateEntityFromDto(dto, entity);
        verify(userRepository).save(entity);
        verify(userMapper).toDto(entity);
    }

    @Test
    void createUser_shouldMapAndSave() {
        UserDto dto = new UserDto().id(null).name("New");
        UserEntity entity = new UserEntity();
        entity.setName("New");
        entity.setId(TEST_USER_ID);
        when(userRepository.save(any())).thenReturn(entity);

        UserDto result = userService.createUser(dto);

        assertThat(result.getName()).isEqualTo("New");
        verify(userMapper).toEntity(dto);
        verify(userRepository).save(any(UserEntity.class));
        verify(userMapper).toDto(entity);
    }

    private UserEntity mockEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(TEST_USER_ID);
        entity.setName(TEST_NAME);
        return entity;
    }
}