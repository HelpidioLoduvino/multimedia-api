package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Video;
import com.example.multimediaapi.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/video")
@AllArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<Object> save(@RequestPart("video") Video video, @RequestParam String group, @RequestPart("videoFile") MultipartFile videoFile) {
        return ResponseEntity.ok(videoService.uploadVideo(video, group, videoFile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Video>> getAll() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

}
