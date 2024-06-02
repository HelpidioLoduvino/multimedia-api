package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ContentShareGroupRepository contentShareGroupRepository;
    private final ContentRepository contentRepository;

    public ResponseEntity<Object> createGroup(ShareGroup shareGroup) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);

        ShareGroup newShareGroup = groupRepository.save(shareGroup);

        UserGroup newUserGroup = new UserGroup(null, "Owner", true, user, newShareGroup);

        return ResponseEntity.ok(userGroupRepository.save(newUserGroup));
    }

    public ResponseEntity<Object> getAllMyGroups(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(userGroupRepository.findAllByUserIdOrShareGroupGroupName(userId, "Público"));
    }

    public ResponseEntity<Object> getAllGroupsExceptPublic(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(userGroupRepository.findByShareGroupGroupNameNotAndUserIdNot("Público", userId));
    }

    public List<ShareGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public ResponseEntity<Object> addContentToSelectedGroup(Long contentId, Long groupId) {

        Content content = contentRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));

        ShareGroup sg = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        ContentShareGroup contentShareGroup = new ContentShareGroup(null, content, sg);

        return ResponseEntity.ok(contentShareGroupRepository.save(contentShareGroup));
    }

    public ResponseEntity<Object> addUserToGroup(Long userId, Long groupId){

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ShareGroup sg = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        UserGroup userGroup = new UserGroup(null, "Normal", false, user, sg);

        return ResponseEntity.ok(userGroupRepository.save(userGroup));
    }

    public ResponseEntity<Object> updateUserStatusToGroupOwner(Long userId, Long groupId) {

        UserGroup userGroup = userGroupRepository.findByUserIdAndShareGroupId(userId, groupId);

        if(userGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userGroup.setUserStatus("Owner");
        userGroup.setEditor(true);
        return ResponseEntity.ok(userGroupRepository.save(userGroup));
    }

    public ResponseEntity<Object> updateUserToGroupEditor(Long userId, Long groupId) {
        UserGroup userGroup = userGroupRepository.findByUserIdAndShareGroupId(userId, groupId);
        if(userGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userGroup.setEditor(true);
        return ResponseEntity.ok(userGroupRepository.save(userGroup));
    }

    public ResponseEntity<Object> getAllContentsByGroupId(Long groupId) {
        return ResponseEntity.ok(contentShareGroupRepository.findAllByShareGroupId(groupId));
    }

    public ResponseEntity<Object> getAllUsersByGroupId(Long groupId) {
        return ResponseEntity.ok(userGroupRepository.findAllByShareGroupId(groupId));
    }

}
