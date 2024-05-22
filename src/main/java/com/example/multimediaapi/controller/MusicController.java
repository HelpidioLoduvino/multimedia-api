package com.example.multimediaapi.controller;

import com.example.multimediaapi.dto.MusicDto;
import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.service.MusicService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(@RequestBody MusicDto musicDto, @RequestPart("music") MultipartFile musicFile, @RequestPart("image") MultipartFile imgFile) throws IOException {
        return  ResponseEntity.ok(musicService.uploadMusic(musicDto, musicFile, imgFile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Music>> getAll() {
        return ResponseEntity.ok(musicService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Music> getMusicById(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.getMusic(id));
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<InputStreamResource> play(@PathVariable Long id) throws IOException {
        ResponseEntity<InputStreamResource> music = musicService.playMusic(id);
        return ResponseEntity.ok(music.getBody());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        musicService.delete(id);
        return ResponseEntity.ok().build();
    }
}
