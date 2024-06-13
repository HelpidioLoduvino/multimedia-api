package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private AlbumReviewRepository albumReviewRepository;
    private final UserRepository userRepository;

    public ResponseEntity<List<MusicRelease>> getAllAlbums() {
        List<MusicRelease> albums = musicReleaseRepository.findAllByReleaseType("Album");
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    public ResponseEntity<Resource> showAlbumImage(Long id){
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
                            .body(resource);
                } else {
                    throw new RuntimeException("Não foi possível ler o arquivo!");
                }
            }

        }catch (MalformedURLException e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public ResponseEntity<AlbumReview> addAlbumReview(AlbumReview albumReview, Long albumId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);

        MusicRelease album = musicReleaseRepository.findById(albumId).orElse(null);
        if(album == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AlbumReview review = new AlbumReview(null, albumReview.getRating(), albumReview.getOverview(), album, user);

        return ResponseEntity.ok(albumReviewRepository.save(review));

    }

}
