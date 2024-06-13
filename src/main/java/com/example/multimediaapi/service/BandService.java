package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Band;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.BandRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BandService {
    private BandRepository bandRepository;
    private UserRepository userRepository;

    public ResponseEntity<Band> addBand(Band band) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        band.setUser(user);
        bandRepository.save(band);
        return ResponseEntity.ok(band);
    }

    public ResponseEntity<List<Band>> getAllMyBands() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(bandRepository.findAllByUserId(userId));
    }
}
