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

    @GetMapping("/public")
    public ResponseEntity<Object> getPublicGroup() {
        return ResponseEntity.ok(groupService.getPublicGroup());
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
    public ResponseEntity<Object> addContentToGroup(@RequestParam Long id1, @RequestParam Long id2) {
        return ResponseEntity.ok(groupService.addContentToSelectedGroup(id1, id2));
    }
}
