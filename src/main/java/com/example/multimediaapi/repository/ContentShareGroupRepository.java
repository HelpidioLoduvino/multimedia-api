package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.ContentShareGroup;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentShareGroupRepository extends JpaRepository<ContentShareGroup, Long> {

    List<ContentShareGroup> findAllByMyGroup_Id(Long shareGroupId);
    List<ContentShareGroup> findAllByMyGroupGroupNameAndContent_MimetypeStartingWithOrContent_UserAndContent_MimetypeStartingWith(String shareGroup_groupName, String content_mimetype, User content_user, String content_mimetype2);

}
