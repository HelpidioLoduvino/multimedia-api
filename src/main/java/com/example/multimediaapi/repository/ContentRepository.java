package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByUserId(Long userId);
    List<Content> findAllByAuthorNameOrTitle(String author_name, String title);
}
