package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.repository.ContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public Content getContent(Long id){
        return contentRepository.findById(id).orElse(null);
    }

    public ResponseEntity<Resource> playContent(Long id) throws IOException {
        Content content = contentRepository.findById(id).orElse(null);
        if (content != null) {
            Path path = Paths.get(content.getPath());
            Resource resource = new UrlResource(path.toUri());
            if(resource.exists() && resource.isReadable()){
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=\"" + path.getFileName().toString() + "\"");
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(path.toFile().length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }

        }
        return ResponseEntity.notFound().build();
    }

}
