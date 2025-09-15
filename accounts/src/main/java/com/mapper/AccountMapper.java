package com.mapper;

import com.model.dto.AccountDto;
import com.model.entities.Account;
import com.model.entities.User;

public class AccountMapper {

    public static AccountDto toAccountDto(Account account) {
        if (account == null) return null;

        return AccountDto.builder()
                .id(account.getId())
                .userId(account.getUser().getId())
                .currency(account.getCurrency())
                .exists(true).build();
    }

    public static Account toAccount(AccountDto accountDto) {
        if (accountDto == null) return null;

        return Account.builder()
                .id(accountDto.getId())
                .user(new User(accountDto.getUserId()))
                .currency(accountDto.getCurrency())
                .value(accountDto.getValue()).build();
    }

}
