package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final FeatureRepository featureRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final String uploadVideoDir = "src/main/resources/static/video/";


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> uploadVideo(Video video, MultipartFile videoFile) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);
            String videoFileName = generateUniqueFileName(videoFile.getOriginalFilename());
            saveVideoFile(videoFile, videoFileName);

            Author author = authorRepository.findByArtistName(video.getAuthor().getArtistName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        Label label = labelRepository.findByLabelName(video.getAuthor().getLabel().getLabelName()).orElse(null);
                        if(label == null) {
                            label = new Label();
                            label.setLabelName(video.getAuthor().getLabel().getLabelName());
                            label = labelRepository.save(label);
                        }
                        Band band = null;
                        if (video.getAuthor().getBand() != null) {
                            band = bandRepository.findByBandName(video.getAuthor().getBand().getBandName()).orElse(null);
                            if (band == null) {
                                band = new Band();
                                band.setBandName(video.getAuthor().getBand().getBandName());
                                band.setHistory(video.getAuthor().getBand().getHistory());
                                band.setStart(video.getAuthor().getBand().getStart());
                                band.setEnd(video.getAuthor().getBand().getEnd());
                                band = bandRepository.save(band);
                            } else {
                                if (video.getAuthor().getBand().getEnd() != null) {
                                    band.setEnd(video.getAuthor().getBand().getEnd());
                                    band = bandRepository.save(band);
                                }
                            }
                        }
                        newAuthor.setArtistName(video.getAuthor().getArtistName());
                        newAuthor.setLabel(label);
                        newAuthor.setBand(band);
                        newAuthor = authorRepository.save(newAuthor);
                        return newAuthor;
                    });
            List<Feature> features = video.getFeatures().stream()
                    .map(featureDto -> {
                        Feature newFeature = featureRepository.findByArtistName(featureDto.getArtistName()).orElse(null);
                        if (newFeature == null) {
                            newFeature = new Feature();
                            newFeature.setArtistName(featureDto.getArtistName());
                            newFeature = featureRepository.save(newFeature);
                        }
                        return newFeature;
                    }).collect(Collectors.toList());

            Video newVideo = new Video();
            newVideo.setTitle(video.getTitle());
            newVideo.setDescription(video.getDescription());
            newVideo.setPath(uploadVideoDir + videoFileName);
            newVideo.setUser(user);
            newVideo.setAuthor(author);
            newVideo.setFeatures(features);
            videoRepository.save(newVideo);
            return ResponseEntity.ok(newVideo);

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading video: " + e.getMessage());
        }
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private void saveVideoFile(MultipartFile file, String fileName) throws IOException {

        File directory = new File(uploadVideoDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadVideoDir + fileName);
        Files.write(filePath, file.getBytes());
    }
}
