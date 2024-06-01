package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.ContentShareGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentShareGroupRepository extends JpaRepository<ContentShareGroup, Long> {
}
