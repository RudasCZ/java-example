package com.homework.morosystems.service;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.UserMapper;
import com.homework.morosystems.mapper.UserMapperImpl;
import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import com.homework.morosystems.model.UserPageResponseDto;
import com.homework.morosystems.repository.UserEntity;
import com.homework.morosystems.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    private static final String TEST_NAME = "Alice";
    private static final long TEST_USER_ID = 99L;
    private static final String TEST_USERNAME = "username";
    private static final String TEST_PASSWORD = "password";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    private UserEntity userEntity;
    private UserCreateUpdateDto createUpdateDto;

    @BeforeEach
    void setUp() {
        userEntity = mockUserEntity();
        createUpdateDto = mockUserCreateUpdate();
    }

    // READ

    @Test
    void getUserById_returnsDto() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));

        UserGetDto result = userService.getUserById(TEST_USER_ID);

        assertThat(result)
                .extracting(UserGetDto::getId, UserGetDto::getName, UserGetDto::getUsername)
                .containsExactly(TEST_USER_ID, TEST_NAME, TEST_USERNAME);

        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_throwsWhenMissing() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(TEST_USER_ID))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    // DELETE

    @Test
    void deleteUser_deletesOwnUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(true);

        userService.deleteUser(TEST_USER_ID);

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_throwsWhenMissing() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(TEST_USER_ID))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_throwsWhenNotOwnUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(TEST_USER_ID))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    // PAGING

    @Test
    void getUsersPageable_returnsPage() {
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity), PageRequest.of(0, 1), 1);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        UserPageResponseDto result = userService.getUsersPageable(0, 1);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isZero();

        verify(userRepository).findAll(any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUsersPageable_throwsOnInvalidParams() {
        assertThatThrownBy(() -> userService.getUsersPageable(-1, 10))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThatThrownBy(() -> userService.getUsersPageable(0, 0))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(userRepository);
    }


    // UPDATE

    @Test
    void updateUser_updatesWithoutPasswordChange() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        createUpdateDto.setPassword(null);

        UserGetDto result = userService.updateUser(TEST_USER_ID, createUpdateDto);

        assertThat(result.getName()).isEqualTo(TEST_NAME);
        verify(securityUtil, never()).encodePassword(anyString());
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_updatesAndHashesPassword() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(securityUtil.encodePassword("newPass")).thenReturn("HASHED");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        createUpdateDto.setPassword("newPass");

        userService.updateUser(TEST_USER_ID, createUpdateDto);

        verify(securityUtil).encodePassword("newPass");
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_throwsWhenMissing() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(TEST_USER_ID, createUpdateDto))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateUser_throwsWhenNotOwnUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(false);

        assertThatThrownBy(() -> userService.updateUser(TEST_USER_ID, createUpdateDto))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateUser_throwsWhenUsernameExists() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(userEntity));
        when(securityUtil.isCurrentAuthenticatedUsername(TEST_USERNAME)).thenReturn(true);
        createUpdateDto.setUsername("takenUsername");
        when(userRepository.existsByUsername("takenUsername")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(TEST_USER_ID, createUpdateDto))
                .isInstanceOf(ApplicationException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).existsByUsername("takenUsername");
        verifyNoMoreInteractions(userRepository);
    }


    private UserEntity mockUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(TEST_USER_ID);
        entity.setName(TEST_NAME);
        entity.setUsername(TEST_USERNAME);
        entity.setPassword(TEST_PASSWORD);
        return entity;
    }

    private UserCreateUpdateDto mockUserCreateUpdate() {
        UserCreateUpdateDto dto = new UserCreateUpdateDto();
        dto.setName(TEST_NAME);
        dto.setUsername(TEST_USERNAME);
        dto.setPassword(TEST_PASSWORD);
        return dto;
    }
}