package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipientIdAndOpened(Long recipient_id, boolean opened);
}
