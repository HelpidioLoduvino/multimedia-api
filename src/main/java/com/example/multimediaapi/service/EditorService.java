package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Editor;
import com.example.multimediaapi.repository.EditorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EditorService {
    private final EditorRepository editorRepository;

    public ResponseEntity<Object> addEditor(Editor editor) {
        return ResponseEntity.ok(editorRepository.save(editor));
    }

    public List<Editor> findAll() {
        return editorRepository.findAll();
    }
}
