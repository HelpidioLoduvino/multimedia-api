package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Category;
import com.example.multimediaapi.model.MusicRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicReleaseRepository extends JpaRepository<MusicRelease, Long> {
    Optional<MusicRelease> findByMusicReleaseName(String name);
    List<MusicRelease> findAllByReleaseType(String releaseType);
}
