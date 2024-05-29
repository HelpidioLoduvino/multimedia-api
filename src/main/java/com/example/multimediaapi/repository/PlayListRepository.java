package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayListRepository extends JpaRepository<Playlist, Long> {
}
