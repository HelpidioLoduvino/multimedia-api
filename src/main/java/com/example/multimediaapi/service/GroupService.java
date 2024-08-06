package com.example.multimediaapi.service;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ContentRepository contentRepository;
    private final RequestToJoinGroupRepository requestToJoinGroupRepository;
    private final NotificationRepository notificationRepository;

    public Group createGroup(Group myGroup) {

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Member member = new Member(true, true, user);

        myGroup.getMembers().add(member);

        return groupRepository.save(myGroup);
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public Group getPublicGroup() {
        return groupRepository.findByName("Público");
    }

    public List<Group> getAllMyGroups(){
        String email = userService.getCurrentUser();
        User user = userRepository.findByUserEmail(email);
        Member member = new Member(user);
        Member owner = new Member(true, true, user);
        return groupRepository.findAllByMembersContainingOrMembersContaining(member, owner);
    }

    public List<Group> getAllExceptMyAndPublicGroups(){
        String email = userService.getCurrentUser();
        User user = userRepository.findByUserEmail(email);
        Member member = new Member(user);
        return groupRepository.findByNameNotAndMembersIsNotContaining("Público", member);
    }

    public List<Group> getAllMyGroupsOrPublicGroups(){
        String email = userService.getCurrentUser();
        User user = userRepository.findByUserEmail(email);
        Member member = new Member(user);
        Member owner = new Member(true, true, user);
        return groupRepository.findByNameOrMembersIsContainingOrMembersContaining("Público", member, owner);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getAllUsersByGroupId(Long groupId) {
        return groupRepository.findMyGroupById(groupId);
    }

    @Transactional
    public RequestToJoinGroup requestToJoinGroup(Long groupId) {

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Group myGroup = groupRepository.findMyGroupById(groupId);

        if(myGroup == null){
            throw new RuntimeException("Group not found");
        }

        Optional<RequestToJoinGroup> existingRequest = requestToJoinGroupRepository.findByUserAndGroup(user, myGroup);

        if (existingRequest.isPresent()) {
            throw  new RuntimeException("Você já fez um pedido para entrar neste grupo");
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

        return requestToJoinGroup;
    }

    public List<RequestToJoinGroup> getAllJoinRequestsByGroupId(Long id) {
        return requestToJoinGroupRepository.findAllByGroup_IdAndRequestStatus(id, "PENDING");
    }

    @Transactional
    public RequestToJoinGroup acceptRequestToJoinGroup(Long requestId) {

        RequestToJoinGroup requestToJoinGroup = requestToJoinGroupRepository.findById(requestId).orElse(null);

        if(requestToJoinGroup == null){
            throw new RuntimeException("Request not found");
        }

        requestToJoinGroup.setRequestStatus("ACCEPTED");

        Group group = requestToJoinGroup.getGroup();

        Member member = new Member(false, false, requestToJoinGroup.getUser());

        group.getMembers().add(member);

        groupRepository.save(group);

        requestToJoinGroupRepository.save(requestToJoinGroup);

        return requestToJoinGroup;
    }

    @Transactional
    public RequestToJoinGroup rejectRequestToJoinGroup(Long requestId) {
        RequestToJoinGroup requestToJoinGroup = requestToJoinGroupRepository.findById(requestId).orElse(null);
        if(requestToJoinGroup == null){
            throw new RuntimeException("Request not found");
        }
        requestToJoinGroup.setRequestStatus("REJECTED");
        requestToJoinGroupRepository.save(requestToJoinGroup);

        return requestToJoinGroup;
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

    public Group updateUserToGroupEditor(Long userId, Long groupId) {

        User user = userRepository.findById(userId).orElse(null);

        Member member = new Member(user);

        Group group = groupRepository.findByMembersContainingAndId(member, groupId);

        if(group == null){
            throw new RuntimeException("Group not found");
        }

        group.getMembers().remove(member);

        Member newMember = new Member(false, true, user);

        group.getMembers().add(newMember);

        return groupRepository.save(group);
    }

    public void addContentToGroup(Long contentId, List<Long> groupId){

        Content content = contentRepository.findById(contentId).orElse(null);

        List<Group> groups = groupId.stream()
                .map(id -> groupRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for(Group group : groups){
            group.getContents().add(content);
            groupRepository.save(group);
        }

    }

    public List<Group> getAllMyFriends(){
        String email = userService.getCurrentUser();
        User user = userRepository.findByUserEmail(email);
        Member owner = new Member(true, true, user);
        List<Group> groups = groupRepository.findAllByMembersContaining(owner);

        Set<String> uniqueMemberNames = new HashSet<>();

        return groups.stream()
                .map(group -> {
                    if (!group.getMembers().isEmpty()) {
                        List<Member> membersWithoutCreator = group.getMembers().stream()
                                .skip(1)
                                .filter(m -> uniqueMemberNames.add(m.getUser().getName()))
                                .collect(Collectors.toList());
                        return new Group(group.getId(), group.getName(), group.getStatus(), membersWithoutCreator, group.getContents());
                    } else {
                        return group;
                    }
                })
                .collect(Collectors.toList());

    }

    public Boolean isOwnerOrEditor(Long groupId){

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Group group = groupRepository.findMyGroupById(groupId);

        if(group == null){
            return false;
        }

        for (Member member : group.getMembers()) {
            if (member.getUser().equals(user)) {
                return member.isOwnerOrEditor();
            }
        }

        return false;
    }


    public Boolean isOwner(Long groupId){

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Group group = groupRepository.findMyGroupById(groupId);

        if(group == null){
            return false;
        }

        for (Member member : group.getMembers()) {
            if (member.getUser().equals(user)) {
                return member.isOwner();
            }
        }

        return false;
    }


}
