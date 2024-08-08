package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Playlist;
import com.example.multimediaapi.service.PlaylistService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@AllArgsConstructor
public class PlaylistController {

    private PlaylistService playlistService;

    @ApiOperation(value = "Add a new playlist", notes = "Creates a new playlist with the provided details and associates it with the specified content IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Playlist created successfully"),
            @ApiResponse(code = 400, message = "Bad request, possibly due to invalid data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Playlist> addPlaylist(@RequestBody Playlist playlist, @RequestParam List<Long> contentIds){
        return ResponseEntity.ok(playlistService.addPlaylist(playlist, contentIds));
    }

    @ApiOperation(value = "Get a playlist by ID", notes = "Retrieves the playlist details specified by the ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Playlist retrieved successfully"),
            @ApiResponse(code = 404, message = "Playlist not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id){
        return ResponseEntity.ok(playlistService.getPlaylist(id));
    }

    @ApiOperation(value = "Get all playlists", notes = "Returns a paginated list of all playlists.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of playlists retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists(Pageable pageable){
        return ResponseEntity.ok(playlistService.getAllPlaylists(pageable).getContent());
    }

    @ApiOperation(value = "Get all playlists by user", notes = "Returns a list of playlists created by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of user playlists retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/user")
    public ResponseEntity<Object> getPlaylistById(){
        return ResponseEntity.ok(playlistService.getAllPlaylistsByUserId());
    }

    @ApiOperation(value = "Get all public playlists", notes = "Returns a list of all public playlists.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of public playlists retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/public")
    public ResponseEntity<Object> getAllPublicPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPublicPlaylists());
    }

    @ApiOperation(value = "Delete a playlist", notes = "Deletes the playlist specified by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Playlist deleted successfully"),
            @ApiResponse(code = 404, message = "Playlist not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Add content to playlist", notes = "Associates the specified content ID with the given playlist IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Content added to playlist successfully"),
            @ApiResponse(code = 400, message = "Bad request, possibly due to invalid data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/content")
    public ResponseEntity<Object> addContentToPlaylist(@RequestParam Long contentId, @RequestParam List<Long> playlistIds){
        playlistService.addContentToPlaylist(contentId, playlistIds);
        return ResponseEntity.ok().build();
    }

}
