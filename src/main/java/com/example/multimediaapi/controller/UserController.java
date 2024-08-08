package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.*;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor()
@Api(tags = "User Management")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "Register a new user", notes = "Creates a new user and returns the registered user details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Bad request, invalid user data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }


    @ApiOperation(value = "User login", notes = "Authenticates the user and returns a login token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 401, message = "Unauthorized, invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto user) {
        return ResponseEntity.ok(userService.login(user));
    }

    @ApiOperation(value = "Get all users", notes = "Returns a list of all users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users found successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<User>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable).getContent());
    }

    @ApiOperation(value = "Delete a user", notes = "Deletes a user by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<Object> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "Refresh authentication token", notes = "Refreshes the authentication token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token refreshed successfully"),
            @ApiResponse(code = 400, message = "Bad request, invalid token refresh request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokensResponse> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @ApiOperation(value = "Get users by client role", notes = "Returns a list of users with the 'CLIENT' role")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/clients")
    public ResponseEntity<Object> getUsersByClientRole() {
        return ResponseEntity.ok(userService.getAllUsersByClientRole());
    }

}
