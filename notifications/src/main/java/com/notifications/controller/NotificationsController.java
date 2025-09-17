package com.notifications.controller;

import com.notifications.dto.NotificationDto;
import com.notifications.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_NOTIFICATIONS')")
    public void notificate(@RequestBody NotificationDto notificationDto) {
        notificationsService.sendNotification(notificationDto);
    }
}
