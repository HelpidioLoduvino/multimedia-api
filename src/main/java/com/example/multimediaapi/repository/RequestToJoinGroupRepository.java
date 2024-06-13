package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.RequestToJoinGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestToJoinGroupRepository extends JpaRepository<RequestToJoinGroup, Long> {
}
