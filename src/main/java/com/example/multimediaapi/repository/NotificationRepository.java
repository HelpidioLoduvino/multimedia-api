package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.Notification;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMyGroup_MembersIsContainingAndOpenedOrRecipientAndOpened(Member member, boolean opened, User recipient, boolean opened2);

}
