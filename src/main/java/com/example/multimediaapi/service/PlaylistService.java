package com.example.multimediaapi.service;

import com.example.multimediaapi.model.PlayList;
import com.example.multimediaapi.repository.MusicRepository;
import com.example.multimediaapi.repository.PlayListRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaylistService {
    private final PlayListRepository playListRepository;
    private final MusicRepository musicRepository;

    public ResponseEntity<Object> addPlaylist(@RequestBody PlayList playList){
        return ResponseEntity.ok(playListRepository.save(playList));
    }

    public List<PlayList> getAllPlaylists(){
        return playListRepository.findAll();
    }
}
