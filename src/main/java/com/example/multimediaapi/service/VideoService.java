package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthorRepository authorRepository;
    private final FeatureRepository featureRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final GroupRepository groupRepository;
    private final VideoCompressionService videoCompressionService;
    private static final String uploadVideoDir = "src/main/resources/static/video/";
    private static final String compressedVideoDir = "src/main/resources/static/video/";


    @Transactional(rollbackFor = Exception.class)
    public Video uploadVideo(Video video, String group, MultipartFile videoFile) {
        try{

            String email = userService.getCurrentUser();

            User user = userRepository.findByUserEmail(email);

            String videoFileName = generateUniqueFileName(videoFile.getOriginalFilename());

            saveVideoFile(videoFile, videoFileName);

            String compressedVideoFileName = "compressed_" + videoFileName;
            File compressedFile = videoCompressionService.compressVideo(new File(uploadVideoDir + videoFileName), compressedVideoDir + compressedVideoFileName);

            File originalFile = new File(uploadVideoDir + videoFileName);
            if (originalFile.exists()) {
                originalFile.delete();
            }

            Author author = authorRepository.findByName(video.getAuthor().getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        Label label = labelRepository.findByName(video.getAuthor().getLabel().getName()).orElse(null);
                        if(label == null) {
                            label = new Label();
                            label.setName(video.getAuthor().getLabel().getName());
                            label = labelRepository.save(label);
                        }
                        Band band = null;
                        if (video.getAuthor().getBand() != null) {
                            band = bandRepository.findByName(video.getAuthor().getBand().getName()).orElse(null);
                            if (band == null) {
                                band = new Band();
                                band.setName(video.getAuthor().getBand().getName());
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
                        newAuthor.setName(video.getAuthor().getName());
                        newAuthor.setLabel(label);
                        newAuthor.setBand(band);
                        newAuthor = authorRepository.save(newAuthor);
                        return newAuthor;
                    });
            List<Feature> features = video.getFeatures().stream()
                    .map(featureDto -> {
                        Feature newFeature = featureRepository.findByName(featureDto.getName()).orElse(null);
                        if (newFeature == null) {
                            newFeature = new Feature();
                            newFeature.setName(featureDto.getName());
                            newFeature = featureRepository.save(newFeature);
                        }
                        return newFeature;
                    }).collect(Collectors.toList());

            Video newVideo = new Video();
            newVideo.setTitle(video.getTitle());
            newVideo.setDescription(video.getDescription());
            //newVideo.setPath(uploadVideoDir + videoFileName);
            newVideo.setPath(compressedVideoDir + compressedVideoFileName);
            newVideo.setUser(user);
            newVideo.setAuthor(author);
            newVideo.setMimetype(videoFile.getContentType());
            newVideo.setSize(compressedFile.length());
            newVideo.setFeatures(features);
            Video savedVideo = videoRepository.save(newVideo);

            if(group.isEmpty()){
                group = "Público";
            }

            Group myGroup = groupRepository.findByName(group);

            myGroup.getContents().add(savedVideo);

            return newVideo;

        }catch(Exception e){
            throw new RuntimeException("Erro ao fazer upload do vídeo" + e.getMessage(), e);
        }
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
