package com.mapper;

import com.model.dto.AccountDto;
import com.model.entities.Account;
import com.model.entities.User;

    public class AccountMapper {

        public static AccountDto toAccountDto(Account account){
            return new AccountDto(account.getId(), account.getUser().getId(), account.getCurrency(), true, account.getValue());
        }

        public static Account toAccount(AccountDto accountDto){
            return new Account(accountDto.getId(), new User(accountDto.getUserId()), accountDto.getCurrency(), accountDto.getValue());
        }

    }
