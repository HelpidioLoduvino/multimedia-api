package com.example.multimediaapi.service;

import com.example.multimediaapi.dto.*;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.UserRepository;
import com.example.multimediaapi.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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

    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        try {
            if(this.userRepository.findByEmail(user.getEmail()) != null) return ResponseEntity.badRequest().build();

            if(!user.getPassword().equals(user.getConfirmPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Passwords do not match");
            }

            String encodedPassword = passwordEncoder.encode(user.getPassword());

            User newUser = new User(null, user.getName(), user.getSurname(), user.getEmail(), encodedPassword, user.getUserRole(), null);

            userRepository.save(newUser);

            return ResponseEntity.ok(newUser);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration");
        }

    }

    public ResponseEntity<Object> login(@RequestBody LoginDto loginDTO){
        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        var emailPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        Authentication authentication = authenticationManager.authenticate(emailPassword);
        User user = (User) authentication.getPrincipal();
        var token = tokenService.generateToken((User) authentication.getPrincipal());
        var refreshToken = tokenService.generateRefreshToken((User) authentication.getPrincipal());
        String email = user.getEmail();
        String userRole = user.getAuthorities().iterator().next().getAuthority();
        Long id = user.getId();
        return ResponseEntity.ok(new LoginResponseDto(id, token, refreshToken, email, userRole));
    }

    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {
        try {
            String email = tokenService.validateToken(request.refreshToken());
            User user = new User();
            user.setEmail(email);
            String newAccessToken = tokenService.generateToken(user);
            String newRefreshToken = tokenService.generateRefreshToken(user);

            return ResponseEntity.ok(new TokensResponse(newAccessToken, newRefreshToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public List<UserDto> getUsersByRoleClient() {
        return userRepository.findAllUsersByRoleClient();
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
