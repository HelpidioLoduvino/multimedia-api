package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.model.Music;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.model.Video;
import com.example.multimediaapi.repository.ContentRepository;
import com.example.multimediaapi.repository.MusicRepository;
import com.example.multimediaapi.repository.UserRepository;
import com.example.multimediaapi.repository.VideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MusicRepository musicRepository;
    private final VideoRepository videoRepository;

    public Page<Content> getAllContents(Pageable pageable) {
        return contentRepository.findAll(pageable);
    }

    public List<Content> getAllContentsByUserId() {

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Long userId = user.getId();

        return contentRepository.findAllByUserId(userId);
    }

    public Content getContent(Long id){
        return contentRepository.findById(id).orElse(null);
    }

    public void streamContent(Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {

        Content content = contentRepository.findById(id).orElse(null);

        if (content == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video not found");
            return;
        }

        String contentPath = content.getPath();
        File file = new File(contentPath);
        long fileSize = file.length();
        String range = request.getHeader("Range");

        long chunkSize = 1024L * 1024L; // 1MB chunks
        long start = 0;
        long end = chunkSize - 1;

        if (range != null) {
            String[] parts = range.replace("bytes=", "").split("-");
            start = parseLong(parts[0]);
            end = parts.length > 1 ? Long.parseLong(parts[1]) : Math.min(start + chunkSize - 1, fileSize - 1);

            if (end >= fileSize) {
                end = fileSize - 1;
            }

            long contentLength = end - start + 1;

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-Type", content.getMimetype());

            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                inputStream.skip(start);
                long bytesRead = 0;
                while (bytesRead < contentLength) {
                    int bytesToRead = (int) Math.min(buffer.length, contentLength - bytesRead);
                    int read = inputStream.read(buffer, 0, bytesToRead);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                    bytesRead += read;
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Type", content.getMimetype());

            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                long bytesRead = 0;
                while (bytesRead < fileSize) {
                    long remainingBytes = fileSize - bytesRead;
                    long chunkEnd = Math.min(bytesRead + chunkSize - 1, fileSize - 1);
                    long contentLength = chunkEnd - bytesRead + 1;

                    response.setHeader("Content-Range", "bytes " + bytesRead + "-" + chunkEnd + "/" + fileSize);
                    response.setHeader("Content-Length", String.valueOf(contentLength));

                    while (bytesRead <= chunkEnd) {
                        int bytesToRead = (int) Math.min(buffer.length, remainingBytes);
                        int read = inputStream.read(buffer, 0, bytesToRead);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, read);
                        bytesRead += read;
                        remainingBytes -= read;
                    }

                    if (bytesRead >= fileSize) {
                        break;
                    }
                }
            }
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public List<Content> searchContent(String query) {

        List<Music> musics = musicRepository.findAllByMusicRelease_MusicReleaseNameOrGenreNameOrAuthorName(query, query, query);

        List<Video> videos = videoRepository.findAllByAuthorName(query);

        List<Content> results = new ArrayList<>();
        results.addAll(musics);
        results.addAll(videos);

        return results;
    }

    public Resource download(Long contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) {
            throw new RuntimeException("Conteudo não encontrado!");
        }

        Path path = Path.of(content.getPath());

        Resource resource;

        try {
            resource = new org.springframework.core.io.UrlResource(path.toUri());
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(content.getMimetype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource).getBody();

    }
}
