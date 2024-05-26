package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Video;
import com.example.multimediaapi.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/video")
@AllArgsConstructor
public class VideoController {
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<Object> save(@RequestPart("video") Video video,@RequestPart("videoFile") MultipartFile videoFile) {
        return ResponseEntity.ok(videoService.uploadVideo(video,videoFile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Video>> getAll() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }
}
