package com.example.multimediaapi.service;

import com.example.multimediaapi.dto.LoginDTO;
import com.example.multimediaapi.dto.LoginResponseDTO;
import com.example.multimediaapi.dto.UserDTO;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.UserRepository;
import com.example.multimediaapi.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private TokenService tokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        if(this.userRepository.findByEmail(user.getEmail()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        User newUser = new User(null, user.getName(), user.getSurname(), user.getEmail(), encryptedPassword, user.getUserRole(), user.isEditor(), null);
        userRepository.save(newUser);
        return ResponseEntity.ok(newUser);
    }

    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO){
        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        var emailPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        Authentication authentication = authenticationManager.authenticate(emailPassword);
        User user = (User) authentication.getPrincipal();
        var token = tokenService.generateToken((User) authentication.getPrincipal());
        String userRole = user.getAuthorities().iterator().next().getAuthority();
        String email = user.getEmail();
        Long id = user.getId();
        return ResponseEntity.ok(new LoginResponseDTO(id, token, userRole, email));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
