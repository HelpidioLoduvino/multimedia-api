package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Notification;
import com.example.multimediaapi.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation(value = "Get notifications", notes = "Returns a list of notifications for the user. This endpoint uses DeferredResult for asynchronous processing.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of notifications retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public DeferredResult<List<Notification>> getNotifications() {
        return notificationService.getNotifications();
    }

}
