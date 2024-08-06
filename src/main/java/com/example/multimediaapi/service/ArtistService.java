package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Artist;
import com.example.multimediaapi.repository.ArtistRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }
}
