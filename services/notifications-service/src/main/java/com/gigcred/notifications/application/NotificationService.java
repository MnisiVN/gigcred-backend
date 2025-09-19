package com.gigcred.notifications.application;

import com.gigcred.notifications.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final Map<String, Notification> sentNotifications = new ConcurrentHashMap<>();

    public Notification send(String userId, String channel, String template) {
        Notification notification = new Notification(UUID.randomUUID().toString(), userId, channel, template, Instant.now());
        sentNotifications.put(notification.id(), notification);
        return notification;
    }

    public List<Notification> list(String userId) {
        return sentNotifications.values().stream().filter(notification -> notification.userId().equals(userId)).toList();
    }
}
