package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.ContentRepository;
import com.example.multimediaapi.repository.PlayListRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistService {
    private final PlayListRepository playListRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Object> addPlaylist(Playlist playList){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByUserEmail(email);
            playList.setUser(user);
            return ResponseEntity.ok(playListRepository.save(playList));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating playlist: " + e.getMessage());
        }
    }

    public List<Playlist> getAllPlaylists(){
        return playListRepository.findAll();
    }

    public void deletePlaylist(Long id){
        playListRepository.deleteById(id);
    }

}
