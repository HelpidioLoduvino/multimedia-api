package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MusicService {

    private final String uploadDir = "src/main/resources/static/music/";
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final CategoryRepository categoryRepository;
    private final SongWriterRepository songWriterRepository;

    public ResponseEntity<Object> uploadMusic(Music music, MultipartFile file) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);

            String fileName = generateUniqueFileName(file.getOriginalFilename());
            saveFile(file, fileName);
            music.setTitle(music.getTitle());

            Album album = albumRepository.findByName(music.getAlbum().getName())
                    .orElseGet(() -> {
                        Album newAlbum = new Album(null, music.getAlbum().getName());
                        return albumRepository.save(newAlbum);
                    });

            Category genre = categoryRepository.findByName(music.getGenre().getName())
                    .orElseGet(() -> {
                        Category newGenre = new Category(null, music.getGenre().getName());
                        return categoryRepository.save(newGenre);
                    });

            List<Artist> artists = music.getArtists().stream()
                    .map(artistName -> {
                        Artist artist = artistRepository.findByName(artistName.getName()).orElse(null);
                        if (artist == null) {
                            artist = new Artist(null, artistName.getName());
                            artist = artistRepository.save(artist);
                        }
                        return artist;
                    })
                    .collect(Collectors.toList());

            List<SongWriter> songWriters = music.getSongwriter().stream()
                    .map(songWriterName -> {
                        SongWriter songWriter = songWriterRepository.findByName(songWriterName.getName()).orElse(null);
                        if (songWriter == null) {
                            songWriter = new SongWriter(null, songWriterName.getName());
                            songWriter = songWriterRepository.save(songWriter);
                        }
                        return songWriter;
                    })
                    .collect(Collectors.toList());

            music.setUser(user);
            music.setPath(uploadDir + fileName);
            music.setAlbum(album);
            music.setGenre(genre);
            music.setArtists(artists);
            music.setSongwriter(songWriters);
            musicRepository.save(music);

            return ResponseEntity.ok(music);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading music: " + e.getMessage());
        }

    }

    public List<Music> getAll() {
        return musicRepository.findAll();
    }

    public ResponseEntity<InputStreamResource> playMusic(Long id) throws IOException {
        Music music = musicRepository.findById(id).orElse(null);
        if (music != null) {
            Path path = Paths.get(music.getPath());
            BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(path.toFile()));
            InputStreamResource resource = new InputStreamResource(fileStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=\"" + path.getFileName().toString() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(path.toFile().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    public void delete(Long id) {
        musicRepository.deleteById(id);
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private void saveFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());
    }

}
