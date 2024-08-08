package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Video;
import com.example.multimediaapi.service.VideoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@AllArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @ApiOperation(value = "Upload video", notes = "Uploads a new video item along with its associated group and video file.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Video uploaded successfully"),
            @ApiResponse(code = 400, message = "Bad request, possibly due to invalid data or missing files"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Video> save(@RequestPart("video") Video video, @RequestParam String group, @RequestPart("videoFile") MultipartFile videoFile) {
        return ResponseEntity.ok(videoService.uploadVideo(video, group, videoFile));
    }

}
