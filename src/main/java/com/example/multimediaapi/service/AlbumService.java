package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
@AllArgsConstructor
public class AlbumService {

    private final MusicReleaseRepository musicReleaseRepository;
    private final AlbumReviewRepository albumReviewRepository;
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;


    public List<MusicRelease> getAllAlbums() {
        return musicReleaseRepository.findAllByReleaseType("Album");
    }

    public Resource showAlbumImage(Long id){
        try{
            MusicRelease album = musicReleaseRepository.findById(id).orElse(null);
            if(album != null){
                Path path = Paths.get(album.getCover());
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

        }catch (MalformedURLException e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
        return null;
    }

    public AlbumReview addAlbumReview(AlbumReview albumReview, Long albumId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        AlbumReview userReview = albumReviewRepository.findByUser_IdAndMusicRelease_Id(userId, albumId);
        if (userReview != null) {
            throw new RuntimeException("Conflito");
        }

        MusicRelease album = musicReleaseRepository.findById(albumId).orElse(null);
        if (album == null) {
            throw new RuntimeException("Album não encontrado");
        }

        AlbumReview review = new AlbumReview(null, albumReview.getRating(), 0, albumReview.getOverview(), album, user);
        albumReviewRepository.save(review);

        List<AlbumReview> albumReviews = albumReviewRepository.findByMusicRelease_Id(albumId);
        double totalRatings = albumReviews.stream().mapToInt(AlbumReview::getRating).sum();
        double media = totalRatings / albumReviews.size();

        albumReviews.forEach(r -> {
            r.setMedia(media);
            albumReviewRepository.save(r);
        });

        return review;
    }

    public MusicRelease getAlbum(Long albumId){
        return musicReleaseRepository.findById(albumId).orElse(null);
    }

    public List<AlbumReview> getAlbumReviews(Long albumId){
        return albumReviewRepository.findByMusicRelease_Id(albumId);
    }

    public Double getAlbumReviewOverall(Long albumId) {
        List<Double> medias = albumReviewRepository.findMediaByMusicRelease_id(albumId);

        if (medias == null || medias.isEmpty()) {
            return 0.0;
        }

        return medias.get(0);
    }

    public List<Music> getMusicFromAlbum(Long albumId){
        return musicRepository.findByMusicRelease_Id(albumId);
    }

}
