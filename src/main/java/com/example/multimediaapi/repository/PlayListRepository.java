package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findAllByUserId(Long userId);

    @Query("SELECT p FROM Playlist p WHERE p.status = 'Público' AND p.user.id != ?1")
    List<Playlist> findAllPublicPlaylistsWithDifferentUserId(Long userId);
}
