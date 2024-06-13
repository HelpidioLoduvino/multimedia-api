package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.AlbumReview;
import com.example.multimediaapi.model.MusicRelease;
import com.example.multimediaapi.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
