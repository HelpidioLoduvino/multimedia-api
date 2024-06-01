package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.ShareGroup;
import com.example.multimediaapi.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    @Query("SELECT ug FROM UserGroup ug WHERE ug.user.id = :userId OR ug.shareGroup.groupName = :groupName")
    List<UserGroup> findAllByUserIdOrShareGroupGroupName(@Param("userId") Long userId, @Param("groupName") String groupName);

    List<UserGroup> findByShareGroupGroupNameNotAndUserIdNot(String shareGroup, Long ownerId);

}
