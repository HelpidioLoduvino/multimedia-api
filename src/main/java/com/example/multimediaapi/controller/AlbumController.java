package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.AlbumReview;
import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.model.MusicRelease;
import com.example.multimediaapi.service.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
@AllArgsConstructor
@Api(tags = "Album Management")
public class AlbumController {

    private final AlbumService albumService;

    @ApiOperation(value = "Get all albums", notes = "Returns a list of all music releases")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all albums retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<MusicRelease>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @ApiOperation(value = "Get album cover image", notes = "Returns the cover image of the album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Album cover image retrieved successfully"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> displayMusicCover(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.showAlbumImage(id));
    }


    @ApiOperation(value = "Add a review for an album", notes = "Allows adding a review to an album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Album review added successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/criticise")
    public ResponseEntity<AlbumReview> criticiseAlbum(@RequestBody AlbumReview albumReview, @RequestParam Long albumId) {
        return ResponseEntity.ok(albumService.addAlbumReview(albumReview, albumId));
    }


    @ApiOperation(value = "Get album info by ID", notes = "Returns detailed information about the album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Album information retrieved successfully"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MusicRelease> getAlbumInfoById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbum(id));
    }

    @ApiOperation(value = "Get all reviews for an album", notes = "Returns a list of all reviews for the album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of album reviews retrieved successfully"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/review/{id}")
    public ResponseEntity<List<AlbumReview>> getAlbumReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviews(id));
    }


    @ApiOperation(value = "Get overall review score for an album", notes = "Returns the overall review score for the album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Overall review score retrieved successfully"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/overall/{id}")
    public ResponseEntity<Double> getAlbumReviewOverallById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumReviewOverall(id));
    }


    @ApiOperation(value = "Get all music tracks from an album", notes = "Returns a list of all music tracks from the album specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of music tracks retrieved successfully"),
            @ApiResponse(code = 404, message = "Album not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/music/{id}")
    public ResponseEntity<List<Music>> getAlbumMusic(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getMusicFromAlbum(id));
    }
}
