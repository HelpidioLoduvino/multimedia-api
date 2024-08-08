package com.example.multimediaapi.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NotificationMessage {
    @Value("${notification.message}")
    private String message;
}
