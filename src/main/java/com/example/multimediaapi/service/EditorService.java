package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Editor;
import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.EditorRepository;
import com.example.multimediaapi.repository.MusicRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EditorService {
    private final EditorRepository editorRepository;
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    public void addEditor(Long userId, Long musicId) {
        User user = userRepository.findById(userId).orElse(null);
        Music music = musicRepository.findById(musicId).orElse(null);
        Editor editor = new Editor(null, true, user, music);
        editorRepository.save(editor);
    }

    public List<Editor> findAll() {
        return editorRepository.findAll();
    }
}
