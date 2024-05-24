package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.AlbumReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumReviewRepository extends JpaRepository<AlbumReview, Long> {

    /*
    @Query("SELECT new com.example.multimediaapi.model.AlbumReview(a.id, a.rating, a.overview, a.album) from AlbumReview a where a.album.id = :albumId")
    List<AlbumReview> findOverviewsByAlbumId(Long albumId);

     */
}
