package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Editor;
import com.example.multimediaapi.service.EditorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editor")
@AllArgsConstructor
public class EditorController {

    private EditorService editorService;

    @PostMapping("/add/{userId}/{musicId}")
    public ResponseEntity<Object> addEditor(@PathVariable Long userId, @PathVariable Long musicId) {
        editorService.addEditor(userId, musicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllEditors() {
        return ResponseEntity.ok(editorService.findAll());
    }
}
