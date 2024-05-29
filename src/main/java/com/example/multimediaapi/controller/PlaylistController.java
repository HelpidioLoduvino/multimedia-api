package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.service.PlaylistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlist")
@AllArgsConstructor
public class PlaylistController {
    private PlaylistService playlistService;

    @PostMapping("/add")
    public ResponseEntity<Object> addPlaylist(@RequestBody Playlist playlist){
        return ResponseEntity.ok(playlistService.addPlaylist(playlist));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }
}
