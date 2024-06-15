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
@RequestMapping("/api/album")
@AllArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    public ResponseEntity<List<MusicRelease>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums().getBody());
    }

    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> displayMusicCover(@PathVariable Long id) {
        ResponseEntity<Resource> cover = albumService.showAlbumImage(id);
        return ResponseEntity.ok(cover.getBody());
    }

    @PostMapping("/criticise-album")
    public ResponseEntity<AlbumReview> criticiseAlbum(@RequestBody AlbumReview albumReview, @RequestParam Long albumId) {
        ResponseEntity<AlbumReview> newAlbumReview = albumService.addAlbumReview(albumReview, albumId);
        return ResponseEntity.ok(newAlbumReview.getBody());
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<MusicRelease> getAlbumInfoById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbum(id).getBody());
    }

    @GetMapping("/album-review/{id}")
    public ResponseEntity<List<AlbumReview>> getAlbumReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviews(id));
    }

    @GetMapping("/album-review-overall/{id}")
    public ResponseEntity<Double> getAlbumReviewOverallById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviewOverall(id));
    }

    @GetMapping("/album-music/{id}")
    public ResponseEntity<List<Music>> getAlbumMusic(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getMusicFromAlbum(id));
    }




}
