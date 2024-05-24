package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.SongWriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongWriterRepository extends JpaRepository<SongWriter, Long> {
    Optional<SongWriter> findByArtistName(String songwriterName);
}
