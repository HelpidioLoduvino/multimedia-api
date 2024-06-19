package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.RequestToJoinGroup;
import com.example.multimediaapi.model.MyGroup;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestToJoinGroupRepository extends JpaRepository<RequestToJoinGroup, Long> {
    //List<RequestToJoinGroup> findAllByUserGroupShareGroup_IdAndRequestStatus(Long groupId, String requestStatus);
    //Optional<RequestToJoinGroup> findByUserAndUserGroup(User user, UserGroup userGroup);
    Optional<RequestToJoinGroup> findByUserAndGroup(User user, MyGroup group);
    List<RequestToJoinGroup> findAllByGroup_IdAndRequestStatus(Long group_id, String requestStatus);

}
