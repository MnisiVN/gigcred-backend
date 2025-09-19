package com.gigcred.notifications;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.notifications.application.NotificationService;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

    private final NotificationService service = new NotificationService();

    @Test
    void sendAndList() {
        service.send("user-1", "SMS", "welcome");
        assertThat(service.list("user-1")).hasSize(1);
    }
}
