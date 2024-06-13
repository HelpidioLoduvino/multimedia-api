package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Album;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    boolean existsByMusicReleaseNameAndUser(String albumName, User user);
}
