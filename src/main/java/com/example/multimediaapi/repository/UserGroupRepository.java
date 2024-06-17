package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUserId(Long userId);

    List<UserGroup> findByShareGroupGroupNameNotAndUserIdNot(String shareGroup, Long ownerId);

    List<UserGroup> findAllByShareGroupId(Long shareGroupId);

    UserGroup findByUserIdAndShareGroupId(Long user_id, Long shareGroup_id);

    UserGroup findByShareGroupId(Long shareGroupId);

    @Query("SELECT CASE WHEN COUNT(ug) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserGroup ug WHERE ug.user.id = :userId " +
            "AND ug.shareGroup.id = :shareGroupId " +
            "AND ug.userStatus = 'Owner' " +
            "AND ug.id = (SELECT MIN(ug2.id) FROM UserGroup ug2 WHERE ug2.shareGroup.id = :shareGroupId AND ug2.user.id = :userId)")
    boolean isGroupOwner(Long userId, Long shareGroupId);


    @Query("SELECT CASE WHEN COUNT(ug) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserGroup ug WHERE ug.user.id = :userId AND ug.shareGroup.id = :shareGroupId AND ug.userStatus = 'Owner'")
    boolean isUserStatusOwner(Long userId, Long shareGroupId);

    @Query("SELECT CASE WHEN COUNT(ug) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserGroup ug WHERE ug.user.id = :userId AND ug.shareGroup.id = :shareGroupId AND ug.userStatus = 'Normal'")
    boolean isUserStatusNormal(Long userId, Long shareGroupId);


    @Query("SELECT CASE WHEN COUNT(ug) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserGroup ug WHERE ug.user.id = :userId AND ug.shareGroup.id = :shareGroupId AND ug.isEditor = true ")
    boolean isEditor(Long userId, Long shareGroupId);


}
