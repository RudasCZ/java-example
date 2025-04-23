package com.homework.morosystems.rest;

import com.homework.morosystems.model.UserDto;
import com.homework.morosystems.rest.api.UsersApi;
import com.homework.morosystems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;


    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
