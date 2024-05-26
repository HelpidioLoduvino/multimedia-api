package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.service.ContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@AllArgsConstructor
public class ContentController {
    private ContentService contentService;

    @GetMapping("/all")
    public ResponseEntity<List<Content>> getAll() {
        return ResponseEntity.ok(contentService.getAllContents());
    }
}
