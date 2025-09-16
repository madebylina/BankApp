package com.account.mapper;

import com.account.model.dto.UserDto;
import com.account.model.entities.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) return null;
        return UserDto.builder().id(user.getId()).username(user.getUsername())
                .password(user.getPassword()).
                personName(user.getPersonName()).dateOfBirth(user.getDateOfBirth()).build();
    }

    public static User toUser(UserDto userDto) {
        if (userDto == null) return null;

        return User.builder().id(userDto.getId()).username(userDto.getUsername()).password(userDto.getPassword())
                .personName(userDto.getPersonName()).dateOfBirth(userDto.getDateOfBirth()).build();
    }


}
