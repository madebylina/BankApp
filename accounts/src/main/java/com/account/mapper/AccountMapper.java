package com.account.mapper;

import com.account.model.dto.AccountDto;
import com.account.model.entities.Account;
import com.account.model.entities.User;

    public class AccountMapper {

        public static AccountDto toAccountDto(Account account){
            return new AccountDto(account.getId(), account.getUser().getId(), account.getCurrency(), true, account.getValue());
        }

        public static Account toAccount(AccountDto accountDto){
            return new Account(accountDto.getId(), new User(accountDto.getUserId()), accountDto.getCurrency(), accountDto.getValue());
        }

    }
