package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.*;
import com.example.multimediaapi.service.GroupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @ApiOperation(value = "Create a new group", notes = "Creates a new group with the provided details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group created successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Object> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @ApiOperation(value = "Get group by ID", notes = "Returns details of the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group retrieved successfully"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    @ApiOperation(value = "Get public group", notes = "Returns the public group details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Public group retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/public")
    public ResponseEntity<Group> getPublicGroup() {
        return ResponseEntity.ok(groupService.getPublicGroup());
    }

    @ApiOperation(value = "Get all groups of the current user", notes = "Returns a list of groups that the authenticated user belongs to")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of user groups retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/user")
    public ResponseEntity<Object> getAllMyGroups() {
        return ResponseEntity.ok(groupService.getAllMyGroups());
    }

    @ApiOperation(value = "Get all groups except the user's and public groups", notes = "Returns a list of all groups excluding the authenticated user's groups and public groups")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of other groups retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/others")
    public ResponseEntity<Object> getAllExceptMyAndPublicGroups() {
        return ResponseEntity.ok(groupService.getAllExceptMyAndPublicGroups());
    }

    @ApiOperation(value = "Get all user and public groups", notes = "Returns a list of groups that the authenticated user belongs to or public groups")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of user and public groups retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/mine")
    public ResponseEntity<List<Group>> getAllMyGroupsOrPublicGroups() {
        return ResponseEntity.ok(groupService.getAllMyGroupsOrPublicGroups());
    }

    @ApiOperation(value = "Get all groups with pagination", notes = "Returns a paginated list of all groups")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Paginated list of groups retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Group>> getAll(Pageable pageable) {
        return ResponseEntity.ok(groupService.getAllGroups(pageable).getContent());
    }

    @ApiOperation(value = "Get all users by group ID", notes = "Returns a list of all users associated with the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of users in the group retrieved successfully"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<Group> getAllUsersByGroupId(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getAllUsersByGroupId(id));
    }

    @ApiOperation(value = "Update user to group owner", notes = "Updates the specified user to be the owner of the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated to owner successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/owner")
    public ResponseEntity<Object> updateUserToOwner(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserStatusToGroupOwner(userId, groupId));
    }


    @ApiOperation(value = "Update user to group editor", notes = "Updates the specified user to be an editor of the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated to editor successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/editor")
    public ResponseEntity<Object> updateUserToEditor(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.updateUserToGroupEditor(userId, groupId));
    }


    @ApiOperation(value = "Request to join group", notes = "Allows the authenticated user to request to join the specified group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Join request submitted successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/request")
    public ResponseEntity<Object> requestToJoinGroup(@RequestParam Long groupId) {
        return ResponseEntity.ok(groupService.requestToJoinGroup(groupId));
    }

    @ApiOperation(value = "Get all join requests by group ID", notes = "Returns a list of all join requests for the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of join requests retrieved successfully"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/requests/{id}")
    public ResponseEntity<List<RequestToJoinGroup>> getAllRequests(@PathVariable Long id){
        return ResponseEntity.ok(groupService.getAllJoinRequestsByGroupId(id));
    }

    @ApiOperation(value = "Accept join request", notes = "Accepts the join request with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Join request accepted successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/accept-request")
    public ResponseEntity<Object> acceptRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.acceptRequestToJoinGroup(id));
    }


    @ApiOperation(value = "Reject join request", notes = "Rejects the join request with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Join request rejected successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/reject-request")
    public ResponseEntity<Object> rejectRequest(@RequestParam Long id){
        return ResponseEntity.ok(groupService.rejectRequestToJoinGroup(id));
    }

    @ApiOperation(value = "Add content to group", notes = "Adds the specified content to the group(s)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Content added to group(s) successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/content")
    public ResponseEntity<Object> addContentToGroup(@RequestParam Long contentId, @RequestParam List<Long> groupId) {
        groupService.addContentToGroup(contentId, groupId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get all friends", notes = "Returns a list of all friends of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of friends retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/friends")
    public ResponseEntity<List<Group>> getAllMyFriends() {
        return ResponseEntity.ok(groupService.getAllMyFriends());
    }


    @ApiOperation(value = "Check if user is owner or editor", notes = "Checks if the authenticated user is an owner or editor of the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User ownership or editor status retrieved successfully"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/owner-or-editor/{id}")
    public ResponseEntity<Boolean> isOwnerOrEditor(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwnerOrEditor(id));
    }

    @ApiOperation(value = "Check if user is owner", notes = "Checks if the authenticated user is the owner of the group specified by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User ownership status retrieved successfully"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/owner/{id}")
    public ResponseEntity<Boolean> isOwner(@PathVariable Long id){
        return ResponseEntity.ok(groupService.isOwner(id));
    }

}
