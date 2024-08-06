package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Video;
import com.example.multimediaapi.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@AllArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<Video> save(@RequestPart("video") Video video, @RequestParam String group, @RequestPart("videoFile") MultipartFile videoFile) {
        return ResponseEntity.ok(videoService.uploadVideo(video, group, videoFile));
    }

}
