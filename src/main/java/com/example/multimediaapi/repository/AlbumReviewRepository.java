package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.AlbumReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumReviewRepository extends JpaRepository<AlbumReview, Long> {
    List<AlbumReview> findByMusicRelease_Id(Long albumId);

    AlbumReview findByUser_IdAndMusicRelease_Id(Long userId, Long musicReleaseId);

    @Query("SELECT ar.media FROM AlbumReview ar WHERE ar.musicRelease.id = :albumId ORDER BY ar.id DESC")
    List<Double> findMediaByMusicRelease_id(Long albumId);

}
