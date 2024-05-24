package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.AlbumReview;
import com.example.multimediaapi.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
@AllArgsConstructor
public class AlbumController {
    /*
    private AlbumService albumService;

    @GetMapping("/all")
    public ResponseEntity<List<Album>> getAll() {
        return ResponseEntity.ok(albumService.getAll());
    }

    @PostMapping("/overview/{id}")
    public ResponseEntity<Album> addOverview(@PathVariable Long id, @RequestBody AlbumReview albumReview) {
        albumService.addAlbumReview(id, albumReview);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<AlbumReview>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReview(id));
    }

     */
}
