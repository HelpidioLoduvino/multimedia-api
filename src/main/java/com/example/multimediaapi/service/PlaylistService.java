package com.example.multimediaapi.service;
import com.example.multimediaapi.dto.ContentPlaylistDto;
import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.model.PlaylistContent;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.ContentRepository;
import com.example.multimediaapi.repository.PlayListRepository;
import com.example.multimediaapi.repository.PlaylistContentRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class PlaylistService {
    private final PlayListRepository playListRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final PlaylistContentRepository playlistContentRepository;

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


    public ResponseEntity<Object> addContentToPlaylist(ContentPlaylistDto contentToPlaylistDto) {

        Content content = contentRepository.findById(contentToPlaylistDto.contentId())
                .orElseThrow(() -> new RuntimeException("Content not found"));

        Playlist playlist = playListRepository.findById(contentToPlaylistDto.playlistId())
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        PlaylistContent playlistContent = new PlaylistContent(null, playlist, content);

        return ResponseEntity.ok(playlistContentRepository.save(playlistContent));
    }

    public ResponseEntity<List<PlaylistContent>> getAllPlaylistContentByPlaylistId(Long id){
        List<PlaylistContent> playlistContents = playlistContentRepository.findAllByPlaylistId(id);
        if (playlistContents.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(playlistContents);
    }

    public List<PlaylistContent> getAllPlaylistContents(){
        return playlistContentRepository.findAll();
    }

}
