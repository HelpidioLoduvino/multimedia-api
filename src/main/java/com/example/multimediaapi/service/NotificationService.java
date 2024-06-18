package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Notification;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.NotificationRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;

    public DeferredResult<ResponseEntity<List<Notification>>> getNotifications() {
        DeferredResult<ResponseEntity<List<Notification>>> deferredResult = new DeferredResult<>(5000L);
        List<Notification> notifications = checkForNotifications();
        if (notifications.isEmpty()) {
            deferredResult.onTimeout(() -> deferredResult.setResult(ResponseEntity.noContent().build()));
        } else {
            deferredResult.setResult(ResponseEntity.ok(notifications));
            markNotificationsAsRead(notifications);
        }
        return deferredResult;
    }


    private List<Notification> checkForNotifications() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) { return null;}
        Object principal = auth.getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUserEmail(email);
        Long userId = user.getId();

        return notificationRepository.findAllByRecipientIdAndOpened(userId, false);
    }

    private void markNotificationsAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setOpened(true));
        notificationRepository.saveAll(notifications);
    }
}
