package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.ShareGroup;
import com.example.multimediaapi.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("all-except-public")
    public ResponseEntity<Object> getAllExceptPublicGroups() {
        return ResponseEntity.ok(groupService.getAllGroupsExceptPublic());
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

    @PutMapping("/update-user-to-owner")
    public ResponseEntity<Object> updateUserToOwner(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserStatusToGroupOwner(userId, groupId));
    }

    @PutMapping("/update-user-to-editor")
    public ResponseEntity<Object> updateUserToEditor(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserToGroupEditor(userId, groupId));
    }
}
