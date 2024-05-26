package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.service.ContentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

    @GetMapping("/play/{id}")
    public ResponseEntity<Resource> playContent(@PathVariable Long id) throws IOException {
        ResponseEntity<Resource> content = contentService.playContent(id);
        return ResponseEntity.ok(content.getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }
}
