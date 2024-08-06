package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.service.MusicService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/musics")
@AllArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<Object> upload(@RequestPart("music")Music music, @RequestParam String group,  @RequestPart("musicFile") MultipartFile musicFile, @RequestPart("imageFile") MultipartFile imgFile){
        return  ResponseEntity.ok(musicService.uploadMusic(music, group, musicFile, imgFile));
    }

    @GetMapping
    public ResponseEntity<List<Music>> getAll() {
        return ResponseEntity.ok(musicService.getAll());
    }

    @GetMapping("/user")
    public ResponseEntity<List<Music>> getAllContentsByUserId() {
        return ResponseEntity.ok(musicService.getAllMusicsByUserId());
    }

    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> displayMusicCover(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.displayCover(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        musicService.delete(id);
        return ResponseEntity.ok().build();
    }


}
