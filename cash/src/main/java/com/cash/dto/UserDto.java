package com.cash.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String password;
    private String personName;
    private LocalDate dateOfBirth;
}
