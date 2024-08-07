package com.example.multimediaapi.service;

import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser() {
        User user = new User(null, "Helpidio", "Mateus", "helpidio@gmail.com", "hththrjkkker", "USER", null);
        User encodedUser = new User(null, "Helpidio", "Mateus", "helpidio@gmail.com", "hththrjkkker", "USER", null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(encodedUser);

        User result = userService.register(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAll(){
        User first = new User(null, "Helpidio", "Mateus", "helpidio@gmail.com", "hththrjkkker", "USER", null);
        User second = new User(null, "Josefa", "Zinga", "josefa@gmail.com", "ggrhhrtjrj", "ADMIN", null);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(first, second), PageRequest.of(0, 2), 2));
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> users = userService.getAll(pageable);
        assertNotNull(users);
        assertEquals(2, users.getContent().size());
        assertEquals(first, users.getContent().get(0));
        assertEquals(second, users.getContent().get(1));
    }
}