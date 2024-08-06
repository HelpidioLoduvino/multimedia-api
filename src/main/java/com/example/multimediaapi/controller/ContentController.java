package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<List<Content>> getAll() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getAllContentsByUserId() {
        return ResponseEntity.ok(contentService.getAllContentsByUserId());
    }

    @GetMapping("/stream/{id}")
    public void streamVideo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        contentService.streamContent(id, request, response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Content>> search(@RequestParam String query) {
        return ResponseEntity.ok(contentService.searchContent(query));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id){
        return ResponseEntity.ok(contentService.download(id));
    }
}
