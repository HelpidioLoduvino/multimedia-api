package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.ContentPlaylistDto;
import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.service.PlaylistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@AllArgsConstructor
public class PlaylistController {
    private PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Playlist> addPlaylist(@RequestBody Playlist playlist, @RequestParam List<Long> contentIds){
        return ResponseEntity.ok(playlistService.addPlaylist(playlist, contentIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id){
        return ResponseEntity.ok(playlistService.getPlaylist(id));
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getPlaylistById(){
        return ResponseEntity.ok(playlistService.getAllPlaylistsByUserId());
    }

    @GetMapping("/public")
    public ResponseEntity<Object> getAllPublicPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPublicPlaylists());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/content")
    public ResponseEntity<Object> addContentToPlaylist(@RequestParam Long contentId, @RequestParam List<Long> playlistIds){
        playlistService.addContentToPlaylist(contentId, playlistIds);
        return ResponseEntity.ok().build();
    }

}
