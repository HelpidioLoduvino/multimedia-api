package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MusicService {

    private static final String uploadDir = "src/main/resources/static/music/";
    private static final String uploadImgDir = "src/main/resources/static/cover/";
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final ContentShareGroupRepository contentShareGroupRepository;
    private final SongWriterRepository songWriterRepository;
    private final MusicReleaseRepository musicReleaseRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final FeatureRepository featureRepository;
    private final ContentRepository contentRepository;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> uploadMusic(Music music, String group, MultipartFile musicFile, MultipartFile imageFile) {
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
            newMusic.setGenre(genre);
            newMusic.setMusicRelease(musicRelease);
            newMusic.setAuthor(author);
            newMusic.setMimetype(musicFile.getContentType());
            newMusic.setSize(musicFile.getSize());
            newMusic.setFeatures(features);
            newMusic.setSongwriters(songWriters);
            newMusic.setLyric(music.getLyric());
            Music savedMusic = musicRepository.save(newMusic);

            if(group.isEmpty()){
                group = "Público";
            }

            ShareGroup shareGroup = groupRepository.findByGroupName(group);

            ContentShareGroup contentShareGroup = new ContentShareGroup(null, savedMusic, shareGroup);

            contentShareGroupRepository.save(contentShareGroup);

            return ResponseEntity.ok(contentShareGroup);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading music: " + e.getMessage());
        }
    }

    public List<Music> getAll() {
        return musicRepository.findAll();
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

    public ResponseEntity<Resource> displayCover(Long id){
        try{
            Content content = contentRepository.findById(id).orElse(null);
            if(content != null){
                if(content.getMimetype().startsWith("audio")){
                    Music music = musicRepository.findById(content.getId()).orElse(null);
                    if(music != null){
                        Path path = Paths.get(music.getMusicRelease().getCover());
                        Resource resource = new UrlResource(path.toUri());
                        if(resource.exists() || resource.isReadable()){
                            HttpHeaders headers = new HttpHeaders();
                            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");

                            return ResponseEntity.ok()
                                    .headers(headers)
                                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                    .body(resource);
                        } else {
                            throw new RuntimeException("Não foi possível ler o arquivo!");
                        }
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }
            }
        }catch (MalformedURLException e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public ResponseEntity<List<Music>> getAllMusicsByUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return ResponseEntity.ok(musicRepository.findAllByUserId(userId));

    }

}
