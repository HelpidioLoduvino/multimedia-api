package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.PlaylistContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistContentRepository extends JpaRepository<PlaylistContent, Long> {

    List<PlaylistContent> findAllByPlaylistId(Long playlistId);
}
