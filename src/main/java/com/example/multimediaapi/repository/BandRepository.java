package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
    Optional<Band> findByName(String name);
}
