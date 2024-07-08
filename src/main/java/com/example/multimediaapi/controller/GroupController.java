package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<Object> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @GetMapping("/get-group/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    @GetMapping("/get-public-group")
    public ResponseEntity<Group> getPublicGroup() {
        return ResponseEntity.ok(groupService.getPublicGroup());
    }

    @GetMapping("/all-my-groups")
    public ResponseEntity<Object> getAllMyGroups() {
        return ResponseEntity.ok(groupService.getAllMyGroups());
    }

    @GetMapping("all-except-my-and-public-groups")
    public ResponseEntity<Object> getAllExceptMyAndPublicGroups() {
        return ResponseEntity.ok(groupService.getAllExceptMyAndPublicGroups());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/all-users-by-group-id/{id}")
    public ResponseEntity<Group> getAllUsersByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllUsersByGroupId(id));
    }

    @PutMapping("/update-user-to-owner")
    public ResponseEntity<Object> updateUserToOwner(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserStatusToGroupOwner(userId, groupId));
    }

    @PutMapping("/update-user-to-editor")
    public ResponseEntity<Object> updateUserToEditor(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserToGroupEditor(userId, groupId));
    }

    @PostMapping("/request-to-join-group")
    public ResponseEntity<Object> requestToJoinGroup(@RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.requestToJoinGroup(groupId));
    }

    @GetMapping("/get-all-requests/{id}")
    public ResponseEntity<List<RequestToJoinGroup>> getAllRequests(@PathVariable Long id){
        return ResponseEntity.ok(groupService.getAllJoinRequestsByGroupId(id).getBody());
    }

    @PutMapping("/accept-request-to-join-group")
    public ResponseEntity<Object> acceptRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.acceptRequestToJoinGroup(id));
    }


    @PutMapping("/reject-request-to-join-group")
    public ResponseEntity<Object> rejectRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.rejectRequestToJoinGroup(id));
    }

    @PostMapping("/add-content-to-group")
    public ResponseEntity<Object> addContentToGroup(@RequestParam Long contentId, @RequestParam List<Long> groupId) {
        groupService.addContentToGroup(contentId, groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all-my-friends")
    public ResponseEntity<List<Group>> getAllMyFriends() {
        return ResponseEntity.ok(groupService.getAllMyFriends().getBody());
    }

    @GetMapping("/is-owner-or-editor/{id}")
    public ResponseEntity<Boolean> isOwnerOrEditor(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwnerOrEditor(id));
    }

    @GetMapping("/is-owner/{id}")
    public ResponseEntity<Boolean> isOwner(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwner(id));
    }

}
