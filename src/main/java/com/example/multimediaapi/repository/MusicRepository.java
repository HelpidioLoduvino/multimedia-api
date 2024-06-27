package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Content;
import com.example.multimediaapi.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    List<Music> findByMusicRelease_Id(Long albumId);
    List<Music> findAllByUserId(Long userId);
    List<Music> findAllByMusicRelease_MusicReleaseNameOrGenreNameOrAuthorName(String musicRelease_musicReleaseName, String genre_name, String author_name);

}
