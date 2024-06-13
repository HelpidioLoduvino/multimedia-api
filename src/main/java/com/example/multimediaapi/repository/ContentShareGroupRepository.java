package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.ContentShareGroup;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentShareGroupRepository extends JpaRepository<ContentShareGroup, Long> {

    List<ContentShareGroup> findAllByShareGroupId(Long shareGroupId);
    List<ContentShareGroup> findAllByShareGroupGroupNameOrContent_User(String shareGroup_groupName, User content_user);
}
