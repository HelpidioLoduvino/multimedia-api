package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.AlbumReview;
import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.model.MusicRelease;
import com.example.multimediaapi.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
@AllArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<MusicRelease>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> displayMusicCover(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.showAlbumImage(id));
    }

    @PostMapping("/criticise")
    public ResponseEntity<AlbumReview> criticiseAlbum(@RequestBody AlbumReview albumReview, @RequestParam Long albumId) {
        return ResponseEntity.ok(albumService.addAlbumReview(albumReview, albumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicRelease> getAlbumInfoById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbum(id));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<List<AlbumReview>> getAlbumReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviews(id));
    }

    @GetMapping("/overall/{id}")
    public ResponseEntity<Double> getAlbumReviewOverallById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviewOverall(id));
    }

    @GetMapping("/music/{id}")
    public ResponseEntity<List<Music>> getAlbumMusic(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getMusicFromAlbum(id));
    }




}
