package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.RequestToJoinGroup;
import com.example.multimediaapi.model.ShareGroup;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestToJoinGroupRepository extends JpaRepository<RequestToJoinGroup, Long> {
    List<RequestToJoinGroup> findAllByUserGroupShareGroup_IdAndRequestStatus(Long groupId, String requestStatus);
    Optional<RequestToJoinGroup> findByUserAndUserGroup(User user, UserGroup userGroup);
}
