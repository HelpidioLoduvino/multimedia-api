package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.LoginDto;
import com.example.multimediaapi.dto.TokenRefreshRequest;
import com.example.multimediaapi.dto.UserDto;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor()
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto user) {
        return userService.login(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @GetMapping("/get-all-clients")
    public ResponseEntity<Object> getUsersByClientRole() {
        return ResponseEntity.ok(userService.getAllUsersByClientRole());
    }

}
