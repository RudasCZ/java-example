package com.homework.morosystems.service;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.UserMapper;
import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import com.homework.morosystems.model.UserPageResponseDto;
import com.homework.morosystems.repository.UserEntity;
import com.homework.morosystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    public static final String ERR_MSG_USER_NOT_FOUND = "User with id %s not found";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public UserGetDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

        if (!securityUtil.isCurrentAuthenticatedUsername(userEntity.getUsername())) {
            throw new ApplicationException("Cannot to delete other user", HttpStatus.FORBIDDEN);
        }

        userRepository.deleteById(id);
        log.debug("User with id {} deleted", id);
    }

    @Transactional(readOnly = true)
    public UserPageResponseDto getUsersPageable(Integer page, Integer size) {
        if (page < 0 || size <= 0) {
            throw new ApplicationException("Page and size must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserEntity> pageResult = userRepository.findAll(pageable);
        List<UserGetDto> resultDtos = pageResult.get().map(userMapper::toDto).toList();

        return new UserPageResponseDto()
                .content(resultDtos)
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements());
    }

    @Transactional
    public UserGetDto updateUser(Long id, UserCreateUpdateDto userDto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

        if (!securityUtil.isCurrentAuthenticatedUsername(userEntity.getUsername())) {
            throw new ApplicationException("You cannot edit other users", HttpStatus.FORBIDDEN);
        }

        if (!userEntity.getUsername().equals(userDto.getUsername())
                && userRepository.existsByUsername(userDto.getUsername())) {

            throw new ApplicationException("User with username %s already exists".formatted(userDto.getUsername()), HttpStatus.CONFLICT);
        }

        userMapper.updateEntityFromDto(userDto, userEntity);
        if (StringUtils.isNotBlank(userDto.getPassword())) {
            userEntity.setPassword(securityUtil.encodePassword(userDto.getPassword()));
            log.debug("Password updated for user with id {}", id);
        }
        userEntity = userRepository.save(userEntity);

        log.debug("User with id {} updated", id);
        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserGetDto createUser(UserCreateUpdateDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ApplicationException("User with username %s already exists".formatted(userDto.getUsername()), HttpStatus.CONFLICT);
        }

        if (StringUtils.isBlank(userDto.getPassword())) {
            throw new ApplicationException("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(securityUtil.encodePassword(userDto.getPassword()));
        userEntity = userRepository.save(userEntity);

        log.debug("Created new user with id {}", userEntity.getId());
        return userMapper.toDto(userEntity);
    }
}
