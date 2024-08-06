package com.example.multimediaapi.service;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.Notification;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.NotificationRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public DeferredResult<List<Notification>> getNotifications() {
        DeferredResult<List<Notification>> deferredResult = new DeferredResult<>(5000L);
        List<Notification> notifications = checkForNotifications();
        if (notifications.isEmpty()) {
            deferredResult.onTimeout(() -> deferredResult.setResult(null));
        } else {
            deferredResult.setResult(notifications);
            markNotificationsAsRead(notifications);
        }
        return deferredResult;
    }


    private List<Notification> checkForNotifications() {

        String email = userService.getCurrentUser();

        User user = userRepository.findByUserEmail(email);

        Member member = new Member(true,true, user);

        return notificationRepository.findAllByMyGroup_MembersIsContainingAndOpenedOrRecipientAndOpened(member, false, user, false);
    }

    private void markNotificationsAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setOpened(true));
        notificationRepository.saveAll(notifications);
    }
}
