package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String groupName);
    Group findByName(String groupName);
    Group findMyGroupById(Long groupId);
    List<Group> findByNameNotAndMembersIsNotContaining(String groupName, Member user);
    List<Group> findAllByMembersIsContaining(Member user);
    List<Group> findAllById(Long shareGroupId);
    Group findByMembersContainingAndId(Member members, Long id);
    List<Group> findAllByMembersContainingAndId(Member userStatus, Long shareGroup_id);
    List<Group> findAllByMembersIsContainingAndMembersIsNotContaining(Member user, Member member);
}
