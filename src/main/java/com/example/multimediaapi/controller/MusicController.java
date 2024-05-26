package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.service.MusicService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@AllArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(@RequestPart("music") Music music, @RequestPart("musicFile") MultipartFile musicFile, @RequestPart("imageFile") MultipartFile imgFile) throws IOException {
        return  ResponseEntity.ok(musicService.uploadMusic(music, musicFile, imgFile));
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

    @GetMapping("/display/{id}")
    public ResponseEntity<Resource> displayImage(@PathVariable Long id){
        ResponseEntity<Resource> image = musicService.displayImage(id);
        return ResponseEntity.ok(image.getBody());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        musicService.delete(id);
        return ResponseEntity.ok().build();
    }


}
