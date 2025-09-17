package com.notifications.service;

import com.notifications.dto.NotificationDto;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {

    public void sendNotification(NotificationDto notificationDto) {
        System.out.printf("Уведомление для пользователя %s: %s%n",
                notificationDto.getLogin(), notificationDto.getMessage());
    }
}
