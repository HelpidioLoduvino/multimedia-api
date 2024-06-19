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

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ContentShareGroupRepository contentShareGroupRepository;
    private final ContentRepository contentRepository;
    private final RequestToJoinGroupRepository requestToJoinGroupRepository;
    private final NotificationRepository notificationRepository;

    public ResponseEntity<Object> createGroup(Group myGroup) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);

        Member member = new Member(true, true, user);

        myGroup.getMembers().add(member);

        return ResponseEntity.ok(groupRepository.save(myGroup));
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public ResponseEntity<List<ContentShareGroup>> getAllMusicsFromPublicGroup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);

        return ResponseEntity.ok(contentShareGroupRepository.findAllByMyGroupNameAndContent_MimetypeStartingWithOrContent_UserAndContent_MimetypeStartingWith("Público", "audio", user, "audio"));
    }

    public ResponseEntity<List<ContentShareGroup>> getAllVideosFromPublicGroup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);

        return ResponseEntity.ok(contentShareGroupRepository.findAllByMyGroupNameAndContent_MimetypeStartingWithOrContent_UserAndContent_MimetypeStartingWith("Público", "video", user, "video"));
    }

    public ResponseEntity<Object> getAllMyGroups(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Member member = new Member(true, true, user);
        return ResponseEntity.ok(groupRepository.findAllByMembersIsContaining(member));
    }

    public ResponseEntity<Object> getAllExceptMyAndPublicGroups(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUserEmail(email);
        Member member = new Member(user);
        return ResponseEntity.ok(groupRepository.findByNameNotAndMembersIsNotContaining("Público", member));
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public ResponseEntity<Object> addContentToSelectedGroup(Long contentId, Long groupId) {

        Content content = contentRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));

        Group sg;
        if (groupId == null) {
            sg = groupRepository.findByName("Público");
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

    public ResponseEntity<Object> getAllContentsByGroupId(Long groupId) {
        return ResponseEntity.ok(contentShareGroupRepository.findAllByMyGroup_Id(groupId));
    }

    public Group getAllUsersByGroupId(Long groupId) {
        return groupRepository.findMyGroupById(groupId);
    }

    @Transactional
    public ResponseEntity<Object> requestToJoinGroup(Long groupId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);

        Group myGroup = groupRepository.findMyGroupById(groupId);

        if(myGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<RequestToJoinGroup> existingRequest = requestToJoinGroupRepository.findByUserAndGroup(user, myGroup);

        if (existingRequest.isPresent()) {
            return new ResponseEntity<>("Você já fez um pedido para entrar neste grupo", HttpStatus.BAD_REQUEST);
        }

        RequestToJoinGroup requestToJoinGroup = new RequestToJoinGroup(null, "PENDING", user, myGroup);

        requestToJoinGroupRepository.save(requestToJoinGroup);

        Notification notification = new Notification();
        notification.setSender(user);
        notification.setMyGroup(myGroup);
        notification.setOpened(false);
        notification.setRecipient(null);
        notification.setMessage("Deseja fazer parte do grupo");

        notificationRepository.save(notification);

        return ResponseEntity.ok(requestToJoinGroup);
    }

    public ResponseEntity<List<RequestToJoinGroup>> getAllJoinRequestsByGroupId(Long id) {
        return ResponseEntity.ok(requestToJoinGroupRepository.findAllByGroup_IdAndRequestStatus(id, "PENDING"));
    }

    @Transactional
    public ResponseEntity<Object> acceptRequestToJoinGroup(Long requestId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);

        RequestToJoinGroup requestToJoinGroup = requestToJoinGroupRepository.findById(requestId).orElse(null);

        if(requestToJoinGroup == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        requestToJoinGroup.setRequestStatus("ACCEPTED");

        Group group = requestToJoinGroup.getGroup();

        Member member = new Member(false, false, requestToJoinGroup.getUser());

        group.getMembers().add(member);

        groupRepository.save(group);

        requestToJoinGroupRepository.save(requestToJoinGroup);

        Notification notification = new Notification();
        notification.setSender(user);
        notification.setRecipient(requestToJoinGroup.getUser());
        notification.setMyGroup(group);
        notification.setOpened(false);
        notification.setMessage("Deseja que vc faça parte do grupo");

        notificationRepository.save(notification);

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

    public ResponseEntity<Object> updateUserStatusToGroupOwner(Long userId, Long groupId) {

        User user = userRepository.findById(userId).orElse(null);

        Member member = new Member(user);

        Group group = groupRepository.findByMembersContainingAndId(member, groupId);

        if(group == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        group.getMembers().remove(member);

        Member newMember = new Member(true, true, user);

        group.getMembers().add(newMember);

        return ResponseEntity.ok(groupRepository.save(group));
    }

    public ResponseEntity<Object> updateUserToGroupEditor(Long userId, Long groupId) {

        User user = userRepository.findById(userId).orElse(null);

        Member member = new Member(user);

        Group group = groupRepository.findByMembersContainingAndId(member, groupId);

        if(group == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        group.getMembers().remove(member);

        Member newMember = new Member(false, true, user);

        group.getMembers().add(newMember);

        return ResponseEntity.ok(groupRepository.save(group));
    }

}
