package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Artist;
import com.example.multimediaapi.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByArtistName(String name);
}
