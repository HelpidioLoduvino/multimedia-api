package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.ShareGroup;
import com.example.multimediaapi.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<ShareGroup, Long> {
    boolean existsByGroupName(String groupName);
    ShareGroup findByGroupName(String groupName);
    ShareGroup findShareGroupById(Long groupId);
    List<ShareGroup> findByGroupNameNotAndFirstOwnerIdNot(String groupName, Long firstOwnerId);
    List<ShareGroup> findAllByFirstOwnerId(Long userId);
}
