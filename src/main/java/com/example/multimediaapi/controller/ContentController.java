package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.repository.ContentRepository;
import com.example.multimediaapi.service.ContentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentRepository contentRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Content>> getAll() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    @GetMapping("all-contents-by-user-id")
    public ResponseEntity<Object> getAllContentsByUserId() {
        return ResponseEntity.ok(contentService.getAllContentsByUserId());
    }

    @GetMapping("/stream-content/{id}")
    public void streamVideo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        contentService.streamContent(id, request, response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }
}
