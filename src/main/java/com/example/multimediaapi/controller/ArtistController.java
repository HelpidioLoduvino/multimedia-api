package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Artist;
import com.example.multimediaapi.service.ArtistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@AllArgsConstructor
@Api(tags = "Artist Management")
public class ArtistController {

    private final ArtistService artistService;

    @ApiOperation(value = "Get all artists", notes = "Returns a paginated list of all artists")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of artists retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Artist>> getAll(Pageable pageable) {
        return ResponseEntity.ok(artistService.findAll(pageable).getContent());
    }
}
