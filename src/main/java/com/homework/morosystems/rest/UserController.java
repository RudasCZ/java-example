package com.homework.morosystems.rest;

import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import com.homework.morosystems.model.UserPageResponseDto;
import com.homework.morosystems.rest.api.UsersApi;
import com.homework.morosystems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserGetDto> createUser(UserCreateUpdateDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserPageResponseDto> getAllUsers(Integer page, Integer size) {
        return ResponseEntity.ok(userService.getUsersPageable(page, size));
    }

    @Override
    public ResponseEntity<UserGetDto> getUserById(Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<UserGetDto> updateUser(Long id, UserCreateUpdateDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }
}
