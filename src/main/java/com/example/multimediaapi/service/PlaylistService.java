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

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistService {

    private final PlayListRepository playListRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public ResponseEntity<Playlist> addPlaylist(Playlist playList, List<Long> contentIds){
        System.out.println("Content Id : " + contentIds);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);

            playList.setUser(user);

            List<Content> contents = contentIds.stream()
                    .map(contentId -> contentRepository.findById(contentId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            playList.setContents(contents);

            playListRepository.save(playList);

            return ResponseEntity.ok().build();

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public Playlist getPlaylist(Long playlistId){
        return playListRepository.findById(playlistId).orElse(null);
    }

    public List<Playlist> getAllPlaylists(){
        return playListRepository.findAll();
    }
    public ResponseEntity<List<Playlist>> getAllPlaylistsByUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(playListRepository.findAllByUserId(userId));
    }

    public ResponseEntity<List<Playlist>> getAllPublicPlaylists(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(playListRepository.findAllPublicPlaylistsWithDifferentUserId(userId));
    }

    public void deletePlaylist(Long id){
        playListRepository.deleteById(id);
    }

}
