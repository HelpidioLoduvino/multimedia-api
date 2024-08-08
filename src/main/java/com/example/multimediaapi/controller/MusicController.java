package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.service.MusicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/musics")
@AllArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @ApiOperation(value = "Upload music", notes = "Uploads a new music item along with its associated group, music file, and image file.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Music uploaded successfully"),
            @ApiResponse(code = 400, message = "Bad request, possibly due to invalid data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Object> upload(@RequestPart("music")Music music, @RequestParam String group,  @RequestPart("musicFile") MultipartFile musicFile, @RequestPart("imageFile") MultipartFile imgFile){
        return  ResponseEntity.ok(musicService.uploadMusic(music, group, musicFile, imgFile));
    }


    @ApiOperation(value = "Get all musics", notes = "Returns a paginated list of all music items.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all music items retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Music>> getAll(Pageable pageable) {
        return ResponseEntity.ok(musicService.getAll(pageable).getContent());
    }

    @ApiOperation(value = "Get all musics by user", notes = "Returns a list of music items uploaded by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of music items for the user retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/user")
    public ResponseEntity<List<Music>> getAllContentsByUserId() {
        return ResponseEntity.ok(musicService.getAllMusicsByUserId());
    }

    @ApiOperation(value = "Get music cover", notes = "Returns the cover image of the music item specified by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Music cover retrieved successfully"),
            @ApiResponse(code = 404, message = "Music not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> displayMusicCover(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.displayCover(id));
    }

    @ApiOperation(value = "Delete music", notes = "Deletes the music item specified by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Music deleted successfully"),
            @ApiResponse(code = 404, message = "Music not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        musicService.delete(id);
        return ResponseEntity.ok().build();
    }
}
