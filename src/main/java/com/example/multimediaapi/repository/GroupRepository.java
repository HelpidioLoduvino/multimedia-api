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
    List<Group> findByNameOrMembersIsContainingOrMembersContaining(String groupName, Member user, Member owner);
    List<Group> findAllByMembersContainingOrMembersContaining(Member user, Member owner);
    List<Group> findAllByMembersContaining(Member owner);
    Group findByMembersContainingAndId(Member members, Long id);
}
