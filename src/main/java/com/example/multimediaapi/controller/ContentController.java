package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.service.ContentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @ApiOperation(value = "Get all contents", notes = "Returns a paginated list of all contents")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of contents retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Content>> getAll(Pageable pageable) {
        return ResponseEntity.ok(contentService.getAllContents(pageable).getContent());
    }

    @ApiOperation(value = "Get all contents by user ID", notes = "Returns a list of all contents associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of user contents retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/user")
    public ResponseEntity<Object> getAllContentsByUserId() {
        return ResponseEntity.ok(contentService.getAllContentsByUserId());
    }

    @ApiOperation(value = "Stream video content", notes = "Streams the video content specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Video content streamed successfully"),
            @ApiResponse(code = 404, message = "Content not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/stream/{id}")
    public void streamVideo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        contentService.streamContent(id, request, response);
    }

    @ApiOperation(value = "Get content by ID", notes = "Returns detailed information about the content specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Content information retrieved successfully"),
            @ApiResponse(code = 404, message = "Content not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }

    @ApiOperation(value = "Search contents", notes = "Searches contents based on the provided query string")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of contents matching the search query retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Content>> search(@RequestParam String query) {
        return ResponseEntity.ok(contentService.searchContent(query));
    }

    @ApiOperation(value = "Download content", notes = "Downloads the content specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Content downloaded successfully"),
            @ApiResponse(code = 404, message = "Content not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id){
        return ResponseEntity.ok(contentService.download(id));
    }
}
