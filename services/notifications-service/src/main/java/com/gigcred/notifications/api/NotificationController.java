package com.gigcred.notifications.api;

import com.gigcred.notifications.application.NotificationService;
import com.gigcred.notifications.domain.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification preferences and sending")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_notifications:write')")
    @Operation(summary = "Send a notification")
    public ResponseEntity<Notification> send(@RequestBody SendRequest request) {
        return ResponseEntity.ok(notificationService.send(request.userId(), request.channel(), request.template()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_notifications:read')")
    @Operation(summary = "List notifications for a user")
    public ResponseEntity<List<Notification>> list(@RequestParam @NotBlank String userId) {
        return ResponseEntity.ok(notificationService.list(userId));
    }

    public record SendRequest(String userId, String channel, String template) {}
}
