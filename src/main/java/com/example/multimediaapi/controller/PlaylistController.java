package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.ContentPlaylistDto;
import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.service.PlaylistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
@AllArgsConstructor
public class PlaylistController {
    private PlaylistService playlistService;

    @PostMapping("/add")
    public ResponseEntity<Playlist> addPlaylist(@RequestBody Playlist playlist, @RequestParam List<Long> contentIds){
        return ResponseEntity.ok(playlistService.addPlaylist(playlist, contentIds).getBody());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }


    @GetMapping("/user-playlists")
    public ResponseEntity<Object> getPlaylistById(){
        return ResponseEntity.ok(playlistService.getAllPlaylistsByUserId());
    }

    @GetMapping("public-playlists")
    public ResponseEntity<Object> getAllPublicPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPublicPlaylists());
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }

}
