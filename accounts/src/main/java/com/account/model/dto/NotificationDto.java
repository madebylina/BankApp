package com.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {

    private String login;
    private String message;
}
