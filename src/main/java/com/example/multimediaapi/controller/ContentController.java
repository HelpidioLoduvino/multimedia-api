package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.repository.ContentRepository;
import com.example.multimediaapi.service.ContentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentRepository contentRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Content>> getAll() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    @GetMapping("all-contents-by-user-id")
    public ResponseEntity<Object> getAllContentsByUserId() {
        return ResponseEntity.ok(contentService.getAllContentsByUserId());
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<InputStreamResource> playContent(@PathVariable Long id, @RequestHeader HttpHeaders headers) throws IOException {
        Content content = contentRepository.findById(id).orElse(null);

        if (content == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String contentPath = content.getPath();
        File file = new File(contentPath);
        long fileSize = file.length();

        List<HttpRange> ranges = headers.getRange();
        if (ranges.isEmpty()) {
            InputStream inputStream = new FileInputStream(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, content.getMimetype())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                    .body(new InputStreamResource(inputStream));
        } else {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(fileSize);
            long end = range.getRangeEnd(fileSize);
            long contentLength = end - start + 1;

            InputStream inputStream = new FileInputStream(file);
            inputStream.skip(start);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, content.getMimetype());
            responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
            responseHeaders.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(responseHeaders)
                    .body(new InputStreamResource(inputStream));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }
}
