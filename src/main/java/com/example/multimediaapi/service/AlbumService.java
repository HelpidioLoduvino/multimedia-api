package com.example.multimediaapi.service;

import com.example.multimediaapi.model.AlbumReview;
import com.example.multimediaapi.repository.AlbumReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AlbumService {

    /*
    private AlbumReviewRepository albumReviewRepository;

    public List<Album> getAll() {
        return albumRepository.findAll();
    }

    public void addAlbumReview(Long albumId, AlbumReview albumReview) {
        Album album = albumRepository.findById(albumId).get();

        albumReview.setAlbum(album);
        albumReview.setRating(albumReview.getRating());
        albumReview.setOverview(albumReview.getOverview());
        albumReviewRepository.save(albumReview);
    }

    public List<AlbumReview> getAlbumReview(Long albumId) {
        return albumReviewRepository.findOverviewsByAlbumId(albumId);
    }



     */
}
