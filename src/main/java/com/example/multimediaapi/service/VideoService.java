package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.apache.commons.io.FileUtils;

@Service
@AllArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final FeatureRepository featureRepository;
    private final LabelRepository labelRepository;
    private final BandRepository bandRepository;
    private final GroupRepository groupRepository;
    private final ContentShareGroupRepository contentShareGroupRepository;
    private final VideoCompressionService videoCompressionService;
    private static final String uploadVideoDir = "src/main/resources/static/video/";
    private static final String compressedVideoDir = "src/main/resources/static/video/";


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> uploadVideo(Video video, String group, MultipartFile videoFile) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
            Object principal = auth.getPrincipal();
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByUserEmail(email);
            String videoFileName = generateUniqueFileName(videoFile.getOriginalFilename());

            saveVideoFile(videoFile, videoFileName);

            String compressedVideoFileName = "compressed_" + videoFileName;
            File compressedFile = videoCompressionService.compressVideo(new File(uploadVideoDir + videoFileName), compressedVideoDir + compressedVideoFileName);

            File originalFile = new File(uploadVideoDir + videoFileName);
            if (originalFile.exists()) {
                originalFile.delete();
            }

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

            ShareGroup shareGroup = groupRepository.findByGroupName(group);

            ContentShareGroup contentShareGroup = new ContentShareGroup(null, savedVideo, shareGroup);

            contentShareGroupRepository.save(contentShareGroup);

            return ResponseEntity.ok(contentShareGroup);

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
