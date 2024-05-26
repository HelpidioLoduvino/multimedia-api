package com.example.multimediaapi.service;

import com.example.multimediaapi.dto.MusicDto;
import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.InputStreamResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final String uploadImgDir = "src/main/resources/static/cover/";
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final SongWriterRepository songWriterRepository;
    private final MusicReleaseRepository musicReleaseRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final FeatureRepository featureRepository;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> uploadMusic(Music music, MultipartFile musicFile, MultipartFile imageFile) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);

            String musicFileName = generateUniqueFileName(musicFile.getOriginalFilename());
            String imageFileName = generateUniqueFileName(imageFile.getOriginalFilename());
            saveMusicFile(musicFile, musicFileName);
            saveImgFile(imageFile, imageFileName);

            Category genre = categoryRepository.findByName(music.getGenre().getName())
                    .orElseGet(() -> {
                        Category category = new Category();
                        category.setName(music.getGenre().getName());
                        category = categoryRepository.save(category);
                        return category;
                    });

            MusicRelease musicRelease = musicReleaseRepository.findByMusicReleaseName(music.getMusicRelease().getMusicReleaseName())
                    .orElseGet(() -> {
                        MusicRelease newRelease = new MusicRelease();
                        newRelease.setMusicReleaseName(music.getMusicRelease().getMusicReleaseName());
                        newRelease.setMusicReleaseDescription(music.getMusicRelease().getMusicReleaseDescription());
                        newRelease.setReleaseType(music.getMusicRelease().getReleaseType());
                        newRelease.setReleaseDate(music.getMusicRelease().getReleaseDate());
                        newRelease.setCover(uploadImgDir + imageFileName);
                        newRelease = musicReleaseRepository.save(newRelease);
                        return newRelease;
                    });

            Author author = authorRepository.findByArtistName(music.getAuthor().getArtistName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        Label label = labelRepository.findByLabelName(music.getAuthor().getLabel().getLabelName()).orElse(null);
                        if(label == null) {
                             label = new Label();
                             label.setLabelName(music.getAuthor().getLabel().getLabelName());
                             label = labelRepository.save(label);
                        }
                        Band band = null;
                        if (music.getAuthor().getBand() != null) {
                            band = bandRepository.findByBandName(music.getAuthor().getBand().getBandName()).orElse(null);
                            if (band == null) {
                                band = new Band();
                                band.setBandName(music.getAuthor().getBand().getBandName());
                                band.setHistory(music.getAuthor().getBand().getHistory());
                                band.setStart(music.getAuthor().getBand().getStart());
                                band.setEnd(music.getAuthor().getBand().getEnd());
                                band = bandRepository.save(band);
                            } else {
                                if (music.getAuthor().getBand().getEnd() != null) {
                                    band.setEnd(music.getAuthor().getBand().getEnd());
                                    band = bandRepository.save(band);
                                }
                            }
                        }
                        newAuthor.setArtistName(music.getAuthor().getArtistName());
                        newAuthor.setLabel(label);
                        newAuthor.setBand(band);
                        newAuthor = authorRepository.save(newAuthor);
                        return newAuthor;
                    });

            List<Feature> features = music.getFeatures().stream()
                    .map(featureDto -> {
                        Feature newFeature = featureRepository.findByArtistName(featureDto.getArtistName()).orElse(null);
                        if(newFeature == null){
                            newFeature = new Feature();
                            newFeature.setArtistName(featureDto.getArtistName());
                            newFeature = featureRepository.save(newFeature);
                        }
                        return newFeature;
                    }).collect(Collectors.toList());

            List<SongWriter> songWriters = music.getSongwriters().stream()
                    .map(songWriterDto -> {
                        SongWriter songWriter = songWriterRepository.findByArtistName(songWriterDto.getArtistName()).orElse(null);
                        if (songWriter == null){
                            songWriter = new SongWriter();
                            songWriter.setArtistName(songWriterDto.getArtistName());
                            songWriter = songWriterRepository.save(songWriter);
                        }
                        return songWriter;
                    }).collect(Collectors.toList());


            Music newMusic = new Music();
            newMusic.setTitle(music.getTitle());
            newMusic.setPath(uploadDir + musicFileName);
            newMusic.setUser(user);
            newMusic.setMusicRelease(musicRelease);
            newMusic.setGenre(genre);
            newMusic.setAuthor(author);
            newMusic.setFeatures(features);
            newMusic.setSongwriters(songWriters);
            newMusic.setLyric(music.getLyric());
            musicRepository.save(newMusic);

            return ResponseEntity.ok(newMusic);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading music: " + e.getMessage());
        }
    }

    public List<Music> getAll() {
        return musicRepository.findAll();
    }

    public Music getMusic(Long id){
        return musicRepository.findById(id).orElse(null);
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

    public ResponseEntity<Resource> displayImage(Long id) {
        try {
            Music music = musicRepository.findById(id).orElse(null);
            if (music != null) {
                Path path = Paths.get(music.getMusicRelease().getCover());
                Resource resource = new UrlResource(path.toUri());

                if (resource.exists() || resource.isReadable()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(resource);
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            } else {
                throw new RuntimeException("Could not read the file!");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    public void delete(Long id) {
        musicRepository.deleteById(id);
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private void saveMusicFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());
    }

    private void saveImgFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadImgDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadImgDir + fileName);
        Files.write(filePath, file.getBytes());
    }

}
