package com.example.multimediaapi.controller;

import com.example.multimediaapi.model.Notification;
import com.example.multimediaapi.service.NotificationService;
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

    @GetMapping
    public DeferredResult<List<Notification>> getNotifications() {
        return notificationService.getNotifications();
    }

}
