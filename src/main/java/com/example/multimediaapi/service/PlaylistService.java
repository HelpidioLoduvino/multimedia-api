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

    public Playlist addPlaylist(Playlist playList, List<Long> contentIds){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return null;}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);

            playList.setUser(user);

            List<Content> contents = contentIds.stream()
                    .map(contentId -> contentRepository.findById(contentId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            playList.setContents(contents);
            if(playList.getStatus().isEmpty()){
                playList.setStatus("Público");
            }

            playListRepository.save(playList);

            return playList;

        }catch (Exception e){
            throw new RuntimeException("Erro ao criar playlist: " + e.getMessage(), e);
        }
    }

    public Playlist getPlaylist(Long playlistId){
        return playListRepository.findById(playlistId).orElse(null);
    }

    public List<Playlist> getAllPlaylists(){
        return playListRepository.findAll();
    }
    public List<Playlist> getAllPlaylistsByUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return null;}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return playListRepository.findAllByUserId(userId);
    }

    public List<Playlist> getAllPublicPlaylists(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return null;}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return playListRepository.findAllPublicPlaylistsWithDifferentUserId(userId);
    }

    public void addContentToPlaylist(Long contentId, List<Long> playlistIds){

        Content content = contentRepository.findById(contentId).orElse(null);

        if (content == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Content not found");
            return;
        }

        List<Playlist> playlists = playlistIds.stream()
                .map(playlistId -> playListRepository.findById(playlistId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (playlists.isEmpty()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No playlists found");
            return;
        }

        for(Playlist playlist: playlists){
            playlist.getContents().add(content);
            playListRepository.save(playlist);
        }

    }

    public void deletePlaylist(Long id){
        playListRepository.deleteById(id);
    }

}
