package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Object> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    @GetMapping("/public")
    public ResponseEntity<Group> getPublicGroup() {
        return ResponseEntity.ok(groupService.getPublicGroup());
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getAllMyGroups() {
        return ResponseEntity.ok(groupService.getAllMyGroups());
    }

    @GetMapping("/others")
    public ResponseEntity<Object> getAllExceptMyAndPublicGroups() {
        return ResponseEntity.ok(groupService.getAllExceptMyAndPublicGroups());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Group>> getAllMyGroupsOrPublicGroups() {
        return ResponseEntity.ok(groupService.getAllMyGroupsOrPublicGroups());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Group> getAllUsersByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllUsersByGroupId(id));
    }

    @PutMapping("/owner")
    public ResponseEntity<Object> updateUserToOwner(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserStatusToGroupOwner(userId, groupId));
    }

    @PutMapping("/editor")
    public ResponseEntity<Object> updateUserToEditor(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserToGroupEditor(userId, groupId));
    }

    @PostMapping("/request")
    public ResponseEntity<Object> requestToJoinGroup(@RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.requestToJoinGroup(groupId));
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<List<RequestToJoinGroup>> getAllRequests(@PathVariable Long id){
        return ResponseEntity.ok(groupService.getAllJoinRequestsByGroupId(id));
    }

    @PutMapping("/accept-request")
    public ResponseEntity<Object> acceptRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.acceptRequestToJoinGroup(id));
    }


    @PutMapping("/reject-request")
    public ResponseEntity<Object> rejectRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.rejectRequestToJoinGroup(id));
    }

    @PostMapping("/content")
    public ResponseEntity<Object> addContentToGroup(@RequestParam Long contentId, @RequestParam List<Long> groupId) {
        groupService.addContentToGroup(contentId, groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Group>> getAllMyFriends() {
        return ResponseEntity.ok(groupService.getAllMyFriends());
    }

    @GetMapping("/owner-or-editor/{id}")
    public ResponseEntity<Boolean> isOwnerOrEditor(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwnerOrEditor(id));
    }

    @GetMapping("/owner/{id}")
    public ResponseEntity<Boolean> isOwner(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwner(id));
    }

}
