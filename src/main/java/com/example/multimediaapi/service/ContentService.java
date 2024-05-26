package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.repository.ContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }
}
