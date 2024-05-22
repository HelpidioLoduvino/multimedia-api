package com.example.multimediaapi.service;

import com.example.multimediaapi.dto.MusicDto;
import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    private final AlbumRepository albumRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final SongWriterRepository songWriterRepository;
    private final MusicReleaseRepository musicReleaseRepository;
    private final SingleRepository singleRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final FeatureRepository featureRepository;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> uploadMusic(MusicDto musicDto, MultipartFile musicFile, MultipartFile imageFile) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);

            String musicFileName = generateUniqueFileName(musicFile.getOriginalFilename());
            String imageFileName = generateUniqueFileName(imageFile.getOriginalFilename());
            saveFile(musicFile, musicFileName);
            saveFile(imageFile, imageFileName);

            Category genre = categoryRepository.findByName(musicDto.music().getGenre().getName())
                    .orElseGet(() -> new Category(null, musicDto.music().getGenre().getName()));

            MusicRelease musicRelease = musicReleaseRepository.findByName(musicDto.musicRelease().getName())
                    .orElseGet(() -> {

                        if(musicDto.musicRelease().getType().equalsIgnoreCase("Album")){
                            Album newAlbum = new Album();
                            newAlbum.setName(musicDto.musicRelease().getName());
                            albumRepository.save(newAlbum);
                            return newAlbum;
                        } else if(musicDto.musicRelease().getType().equalsIgnoreCase("Single")){
                            Single newSingle = new Single();
                            newSingle.setName(musicDto.musicRelease().getName());
                            singleRepository.save(newSingle);
                            return newSingle;
                        }
                        throw new IllegalArgumentException("Unknown music release type: " + musicDto.musicRelease().getType());
                    });

            List<Author> authors = musicDto.music().getAuthors().stream()
                    .map(authorDto -> {
                        Author newAuthor = authorRepository.findByName(authorDto.getName()).orElse(null);
                        if(newAuthor == null){
                            newAuthor = new Author();
                            newAuthor.setName(authorDto.getName());
                            Label label = labelRepository.findByName(authorDto.getLabel().getName()).orElse(null);
                            if(label == null){
                                label = new Label();
                                label.setName(authorDto.getLabel().getName());
                                label = labelRepository.save(label);
                            }
                            Band band = bandRepository.findByName(authorDto.getBand().getName()).orElse(null);
                            if(band == null){
                                band = new Band();
                                band.setName(authorDto.getBand().getName());
                                band.setHistory(authorDto.getBand().getHistory());
                                band.setStart(authorDto.getBand().getStart());
                                band.setEnd(authorDto.getBand().getEnd());
                                band = bandRepository.save(band);
                            }

                            newAuthor.setLabel(label);
                            newAuthor.setBand(band);
                            newAuthor = authorRepository.save(newAuthor);
                        }
                        return newAuthor;
                    }).collect(Collectors.toList());

            List<Feature> features = musicDto.music().getFeatures().stream()
                    .map(featureDto -> {
                        Feature newFeature = featureRepository.findByName(featureDto.getName()).orElse(null);
                        if(newFeature == null){
                            newFeature = new Feature();
                            newFeature.setName(featureDto.getName());
                            newFeature = featureRepository.save(newFeature);
                        }
                        return newFeature;
                    }).collect(Collectors.toList());

            List<SongWriter> songWriters = musicDto.music().getSongwriters().stream()
                    .map(songWriterDto -> {
                        SongWriter songWriter = songWriterRepository.findByName(songWriterDto.getName()).orElse(null);
                        if (songWriter == null){
                            songWriter = new SongWriter();
                            songWriter.setName(songWriterDto.getName());
                            songWriter = songWriterRepository.save(songWriter);
                        }
                        return songWriter;
                    }).collect(Collectors.toList());


            Music newMusic = new Music();
            newMusic.setTitle(musicDto.music().getTitle());
            newMusic.setDescription(musicDto.music().getDescription());
            newMusic.setCover(uploadImgDir + imageFileName);
            newMusic.setPath(uploadDir + musicFileName);
            newMusic.setUser(user);
            newMusic.setMusicRelease(musicRelease);
            newMusic.setGenre(genre);
            newMusic.setAuthors(authors);
            newMusic.setFeatures(features);
            newMusic.setSongwriters(songWriters);
            newMusic.setLyric(musicDto.music().getLyric());
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
