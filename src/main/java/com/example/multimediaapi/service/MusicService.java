package com.example.multimediaapi.service;

import com.example.multimediaapi.configuration.UploadPath;
import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final UserRepository userRepository;
    private final UserService userService;
    private final MusicRepository musicRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final SongWriterRepository songWriterRepository;
    private final MusicReleaseRepository musicReleaseRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final FeatureRepository featureRepository;
    private final ContentRepository contentRepository;
    private final UploadPath uploadPath;

    @Transactional(rollbackFor = Exception.class)
    public Music uploadMusic(Music music, String group, MultipartFile musicFile, MultipartFile imageFile) {
        try {
            String email = userService.getCurrentUser();

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
                        newRelease.setCover(uploadPath.getImageUploadDir() + imageFileName);
                        newRelease = musicReleaseRepository.save(newRelease);
                        return newRelease;
                    });

            Author author = authorRepository.findByName(music.getAuthor().getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        Label label = labelRepository.findByName(music.getAuthor().getLabel().getName()).orElse(null);
                        if(label == null) {
                             label = new Label();
                             label.setName(music.getAuthor().getLabel().getName());
                             label = labelRepository.save(label);
                        }
                        Band band = null;
                        if (music.getAuthor().getBand() != null) {
                            band = bandRepository.findByName(music.getAuthor().getBand().getName()).orElse(null);
                            if (band == null) {
                                band = new Band();
                                band.setName(music.getAuthor().getBand().getName());
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
                        newAuthor.setName(music.getAuthor().getName());
                        newAuthor.setLabel(label);
                        newAuthor.setBand(band);
                        newAuthor = authorRepository.save(newAuthor);
                        return newAuthor;
                    });

            List<Feature> features = music.getFeatures().stream()
                    .map(featureDto -> {
                        Feature newFeature = featureRepository.findByName(featureDto.getName()).orElse(null);
                        if(newFeature == null){
                            newFeature = new Feature();
                            newFeature.setName(featureDto.getName());
                            newFeature = featureRepository.save(newFeature);
                        }
                        return newFeature;
                    }).collect(Collectors.toList());

            List<SongWriter> songWriters = music.getSongwriters().stream()
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
            newMusic.setTitle(music.getTitle());
            newMusic.setPath(uploadPath.getMusicUploadDir() + musicFileName);
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

            Group myGroup = groupRepository.findByName(group);

            myGroup.getContents().add(savedMusic);

            groupRepository.save(myGroup);

            return newMusic;

        }catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da música: " + e.getMessage(), e);
        }
    }

    public Page<Music> getAll(Pageable pageable) {
        return musicRepository.findAll(pageable);
    }

    public void delete(Long id) {
        musicRepository.deleteById(id);
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private void saveMusicFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadPath.getMusicUploadDir());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadPath.getMusicUploadDir() + fileName);
        Files.write(filePath, file.getBytes());
    }

    private void saveImgFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadPath.getImageUploadDir());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadPath.getImageUploadDir() + fileName);
        Files.write(filePath, file.getBytes());
    }

    public Resource displayCover(Long id){
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
                                    .body(resource).getBody();
                        } else {
                            throw new RuntimeException("Não foi possível ler o arquivo!");
                        }
                    }
                } else {
                    return null;
                }
            }
        }catch (MalformedURLException e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
        return null;
    }

    public List<Music> getAllMusicsByUserId(){
        String email = userService.getCurrentUser();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return musicRepository.findAllByUserId(userId);
    }

}
