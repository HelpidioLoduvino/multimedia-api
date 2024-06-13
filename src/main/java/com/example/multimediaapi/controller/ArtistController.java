package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Artist;
import com.example.multimediaapi.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
@AllArgsConstructor
public class ArtistController {
    private ArtistService artistService;

    @GetMapping("/all")
    public ResponseEntity<List<Artist>> getAll() {
        return ResponseEntity.ok(artistService.findAll());
    }
}
