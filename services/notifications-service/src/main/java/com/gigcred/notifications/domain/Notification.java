package com.gigcred.notifications.domain;

import java.time.Instant;

public record Notification(String id, String userId, String channel, String template, Instant createdAt) {
}
