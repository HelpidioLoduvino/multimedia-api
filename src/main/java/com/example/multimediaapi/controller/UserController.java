package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.*;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor()
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto user) {
        return ResponseEntity.ok(userService.login(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensResponse> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @GetMapping("/clients")
    public ResponseEntity<Object> getUsersByClientRole() {
        return ResponseEntity.ok(userService.getAllUsersByClientRole());
    }

}
