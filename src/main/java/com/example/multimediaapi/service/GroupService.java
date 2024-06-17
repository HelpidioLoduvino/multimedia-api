package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ContentShareGroupRepository contentShareGroupRepository;
    private final ContentRepository contentRepository;
    private final RequestToJoinGroupRepository requestToJoinGroupRepository;
    private final WebSocketHandler webSocketHandler;

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

    public ShareGroup getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public ResponseEntity<List<ContentShareGroup>> getAllMusicsFromPublicGroup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);

        return ResponseEntity.ok(contentShareGroupRepository.findAllByShareGroupGroupNameAndContent_MimetypeStartingWithOrContent_UserAndContent_MimetypeStartingWith("Público", "audio", user, "audio"));
    }

    public ResponseEntity<List<ContentShareGroup>> getAllVideosFromPublicGroup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);

        return ResponseEntity.ok(contentShareGroupRepository.findAllByShareGroupGroupNameAndContent_MimetypeStartingWithOrContent_UserAndContent_MimetypeStartingWith("Público", "video", user, "video"));
    }


    public ResponseEntity<Object> getAllMyGroups(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(userGroupRepository.findAllByUserId(userId));
    }

    public ResponseEntity<Object> getAllExceptMyAndPublicGroups(){
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

        ShareGroup sg;
        if (groupId == null) {
            sg = groupRepository.findByGroupName("Público");
            if (sg == null) {
                throw new RuntimeException("Public Group not found");
            }
        } else {
            sg = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
        }

        ContentShareGroup contentShareGroup = new ContentShareGroup(null, content, sg);

        return ResponseEntity.ok(contentShareGroupRepository.save(contentShareGroup));

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

    public List<UserGroup> getAllUsersByGroupId(Long groupId) {
        return userGroupRepository.findAllByShareGroupId(groupId);
    }

    public ResponseEntity<Object> requestToJoinGroup(Long groupId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);

        UserGroup usergroup = userGroupRepository.findByShareGroupId(groupId);

        if(usergroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<RequestToJoinGroup> existingRequest = requestToJoinGroupRepository.findByUserAndUserGroup(user, usergroup);
        if (existingRequest.isPresent()) {
            return new ResponseEntity<>("Você já fez um pedido para entrar neste grupo", HttpStatus.BAD_REQUEST);
        }

        RequestToJoinGroup requestToJoinGroup = new RequestToJoinGroup(null, "PENDING", user, usergroup);
        requestToJoinGroupRepository.save(requestToJoinGroup);

        webSocketHandler.sendMessageToAll("User " + email + " requested to join your group " + usergroup.getShareGroup().getGroupName());

        return ResponseEntity.ok(requestToJoinGroup);
    }

    public ResponseEntity<List<RequestToJoinGroup>> getAllJoinRequestsByGroupId(Long id) {
        return ResponseEntity.ok(requestToJoinGroupRepository.findAllByUserGroupShareGroup_IdAndRequestStatus(id, "PENDING"));
    }

    @Transactional
    public ResponseEntity<Object> acceptRequestToJoinGroup(Long requestId) {

        RequestToJoinGroup requestToJoinGroup = requestToJoinGroupRepository.findById(requestId).orElse(null);

        if(requestToJoinGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        requestToJoinGroup.setRequestStatus("ACCEPTED");

        UserGroup newUser = new UserGroup(null, "Normal", false, requestToJoinGroup.getUser(), requestToJoinGroup.getUserGroup().getShareGroup());

        userGroupRepository.save(newUser);

        requestToJoinGroupRepository.save(requestToJoinGroup);

        return ResponseEntity.ok(requestToJoinGroup);
    }

    @Transactional
    public ResponseEntity<Object> rejectRequestToJoinGroup(Long requestId) {
        RequestToJoinGroup requestToJoinGroup = requestToJoinGroupRepository.findById(requestId).orElse(null);
        if(requestToJoinGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        requestToJoinGroup.setRequestStatus("REJECTED");
        requestToJoinGroupRepository.save(requestToJoinGroup);

        return ResponseEntity.ok(requestToJoinGroup);
    }

    public boolean isOwner(Long groupId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED).hasBody();}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return userGroupRepository.isUserStatusOwner(userId, groupId);
    }

    public boolean isGroupOwner(Long groupId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED).hasBody();}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return userGroupRepository.isGroupOwner(userId, groupId);
    }

    public boolean isEditor(Long groupId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED).hasBody();}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return userGroupRepository.isEditor(userId, groupId);
    }

    public boolean isNormal(Long groupId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED).hasBody();}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return userGroupRepository.isUserStatusNormal(userId, groupId);
    }

}
