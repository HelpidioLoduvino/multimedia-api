package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.MyGroup;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<MyGroup, Long> {
    boolean existsByGroupName(String groupName);
    MyGroup findByGroupName(String groupName);
    MyGroup findMyGroupById(Long groupId);
    List<MyGroup> findByGroupNameNotAndMembersIsNotContaining(String groupName, Member user);
    List<MyGroup> findAllByMembersIsContaining(Member user);
    List<MyGroup> findAllById(Long shareGroupId);
    MyGroup findByMembersContainingAndId(Member members, Long id);
    List<MyGroup> findAllByMembersContainingAndId(Member userStatus, Long shareGroup_id);
}
