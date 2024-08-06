package com.example.multimediaapi.service;

import com.example.multimediaapi.dto.*;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.UserRepository;
import com.example.multimediaapi.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext applicationContext;
    private final TokenService tokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        try {
            if (this.userRepository.findByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("O usuário já está registrado.");
            }

            String encodedPassword = passwordEncoder.encode(user.getPassword());

            User newUser = new User(null, user.getName(), user.getSurname(), user.getEmail(), encodedPassword, user.getUserRole(), null);

            userRepository.save(newUser);

            return newUser;

        }catch (Exception e){
            throw new RuntimeException("Erro ao registrar o usuário: " + e.getMessage(), e);
        }
    }

    public LoginResponseDto login(LoginDto loginDTO){
        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        var emailPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        Authentication authentication = authenticationManager.authenticate(emailPassword);
        User user = (User) authentication.getPrincipal();
        var token = tokenService.generateToken((User) authentication.getPrincipal());
        var refreshToken = tokenService.generateRefreshToken((User) authentication.getPrincipal());
        String email = user.getEmail();
        String userRole = user.getAuthorities().iterator().next().getAuthority();
        Long id = user.getId();
        return new LoginResponseDto(id, token, refreshToken, email, userRole);
    }

    public TokensResponse refresh(TokenRefreshRequest request) {
        try {
            String email = tokenService.validateToken(request.refreshToken());
            User user = new User();
            user.setEmail(email);
            String newAccessToken = tokenService.generateToken(user);
            String newRefreshToken = tokenService.generateRefreshToken(user);
            return new TokensResponse(newAccessToken, newRefreshToken);
        } catch (RuntimeException e) {
            return new TokensResponse(null, e.getMessage());
        }
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsersByClientRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long id = user.getId();
        return userRepository.findAllByUserRoleAndIdNot("CLIENT", id);
    }

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
        }
        return username;
    }

}
