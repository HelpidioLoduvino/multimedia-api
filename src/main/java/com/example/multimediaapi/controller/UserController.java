package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.LoginDto;
import com.example.multimediaapi.dto.TokenRefreshRequest;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/clients")
    public ResponseEntity<Object> getClients() {
        return ResponseEntity.ok(userService.getUsersByRoleClient());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

}
