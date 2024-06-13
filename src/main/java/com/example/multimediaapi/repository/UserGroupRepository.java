package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUserId(Long userId);

    List<UserGroup> findByShareGroupGroupNameNotAndUserIdNot(String shareGroup, Long ownerId);

    List<UserGroup> findAllByShareGroupId(Long shareGroupId);

    UserGroup findByUserIdAndShareGroupId(Long user_id, Long shareGroup_id);

}
