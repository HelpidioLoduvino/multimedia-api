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
    public ResponseEntity<Object> createGroup(@RequestBody ShareGroup group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @GetMapping("/get-group/{id}")
    public ResponseEntity<ShareGroup> getGroup(@PathVariable long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
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

    @PostMapping("/add-content-to-group")
    public ResponseEntity<Object> addContentToGroup(@RequestParam Long contentId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.addContentToSelectedGroup(contentId, groupId));
    }

    @GetMapping("/all-contents-by-group-id/{id}")
    public ResponseEntity<Object> getAllContentsByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllContentsByGroupId(id));
    }

    @GetMapping("/all-users-by-group-id/{id}")
    public ResponseEntity<List<UserGroup>> getAllUsersByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllUsersByGroupId(id));
    }

    @GetMapping("/all-musics-from-public-group")
    public ResponseEntity<List<ContentShareGroup>> getAllMusicsFromPublicGroup() {
        ResponseEntity<List<ContentShareGroup>> response = groupService.getAllMusicsFromPublicGroup();
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/all-videos-from-public-group")
    public ResponseEntity<List<ContentShareGroup>> getAllVideosFromPublicGroup() {
        ResponseEntity<List<ContentShareGroup>> response = groupService.getAllVideosFromPublicGroup();
        return ResponseEntity.ok(response.getBody());
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

    @GetMapping("/is-owner")
    public boolean isOwner(@RequestParam Long groupId) {
        return groupService.isOwner(groupId);
    }

    @GetMapping("/is-editor")
    public boolean isEditor(@RequestParam Long groupId) {
        return groupService.isEditor(groupId);
    }

    @GetMapping("/is-group-owner")
    public boolean isGroupOwner(@RequestParam Long groupId) {
        return groupService.isGroupOwner(groupId);
    }

    @GetMapping("/is-normal")
    public boolean isNormal(@RequestParam Long groupId) {
        return groupService.isNormal(groupId);
    }


}
