package com.homework.morosystems.service;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.UserMapper;
import com.homework.morosystems.model.UserDto;
import com.homework.morosystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ApplicationException("User with id %s not found".formatted(id), HttpStatus.NOT_FOUND));
    }
}
