package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.ContentShareGroup;
import com.example.multimediaapi.model.ShareGroup;
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
    public ResponseEntity<Object> getAllUsersByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllUsersByGroupId(id));
    }

    @GetMapping("/all-contents-from-public-group")
    public ResponseEntity<List<ContentShareGroup>> getAllContentsFromPublicGroup() {
        ResponseEntity<List<ContentShareGroup>> response = groupService.getAllContentsFromPublicGroup();
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
}
