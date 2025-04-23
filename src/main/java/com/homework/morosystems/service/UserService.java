package com.homework.morosystems.service;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.UserMapper;
import com.homework.morosystems.model.UserDto;
import com.homework.morosystems.model.UserPageResponseDto;
import com.homework.morosystems.repository.UserEntity;
import com.homework.morosystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return null;
    }

    @Transactional(readOnly = true)
    public UserPageResponseDto getUsersPageable(Integer page, Integer size) {
        if (page < 0 || size <= 0) {
            throw new ApplicationException("Page and size must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserEntity> pageResult = userRepository.findAll(pageable);
        List<UserDto> resultDtos = pageResult.get().map(userMapper::toDto).toList();

        return new UserPageResponseDto()
                .content(resultDtos)
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements());
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ERR_MSG_USER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

        userMapper.updateEntityFromDto(userDto, userEntity);
        userEntity = userRepository.save(userEntity);

        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
//        if (userRepository.existsByEmail(userDto.getEmail())) {
//            throw new ApplicationException("User with email %s already exists".formatted(userDto.getEmail()), HttpStatus.BAD_REQUEST);
//        }

        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity = userRepository.save(userEntity);

        return userMapper.toDto(userEntity);
    }
}
