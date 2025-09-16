package com.account.service;

import com.AccountService;
import com.account.model.dto.*;
import com.model.dto.*;
import com.model.entities.Account;
import com.model.entities.User;
import com.repository.AccountRepository;
import com.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.management.OperationsException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AccountService.class)
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void getUsers() {
        entityManager.persistAndFlush(new User(null, "user_1", "password_1", "name_1", null));
        entityManager.persistAndFlush(new User(null, "user_2", "password_2", "name_2", null));
        List<UserDto> result = accountService.getUsers();
        assertEquals(2, result.size());
        assertEquals("user_1", result.get(0).getUsername());
        assertEquals("user_2", result.get(1).getUsername());

    }

    @Test
    void getUserByUsername() throws AccountNotFoundException {
        entityManager.persistAndFlush(new User
                (null, "user_1", "password_1", "name_1", null));

        UserDto result = accountService.getUserByUsername("user_1");

        assertNotNull(result);
        assertEquals("name_1", result.getPersonName());
    }

    @Test
    void getUserById() {
        User user = new User(null, "user_1", "password_1", "name_1", null);
        entityManager.persistAndFlush(user);

        UserDto result = accountService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals("user_1", result.getUsername());
    }

    @Test
    void saveUser() {
        UserDto userDto = new UserDto
                (null, "user_1", "password_1", "name_1", null);

        accountService.saveUser(userDto);

        Optional<User> found = userRepository.findByUsername("user_1");
        assertTrue(found.isPresent());
        assertEquals("user_1", found.get().getUsername());
        assertEquals("name_1", found.get().getPersonName());
    }

    @Test
    void getAccountsByUserIdAndCurrency() {
        User user = new User();
        entityManager.persist(user);
        Account account = new Account();
        account.setUser(new User(user.getId()));
        account.setCurrency(CurrencyEnum.RUB);
        account.setValue(100.0);
        entityManager.persist(account);
        entityManager.flush();

        AccountDto result = accountService.getAccountsByUserIdAndCurrency(user.getId(), CurrencyEnum.RUB);

        assertNotNull(result);
        assertEquals(100.0, result.getValue());
    }

    @Test
    void saveAccount() {
        User user = new User();
        Long userId = entityManager.persist(user).getId();
        entityManager.flush();
        AccountDto accountDto = new AccountDto(null, userId, CurrencyEnum.RUB, true, 50.0);

        AccountDto savedAccount = accountService.saveAccount(accountDto);

        Optional<Account> found = accountRepository.findById(savedAccount.getId());
        assertTrue(found.isPresent());
        assertEquals(50.0, found.get().getValue());
    }

    @Test
    void deleteAccountById() {
        User user = entityManager.persist(new User());
        entityManager.flush();

        Account account = new Account(null, user, CurrencyEnum.RUB, 50.0);
        entityManager.persist(account);
        entityManager.flush();
        Long accountId = account.getId();

        accountService.deleteAccountById(accountId);

        Optional<Account> found = accountRepository.findById(accountId);
        assertFalse(found.isPresent());
    }


    @Test
    void testTransfer_shouldSucceed() throws OperationsException {
        User fromUser = entityManager.persist(new User());
        User toUser = entityManager.persist(new User());

        Account fromAccount = new Account(null, fromUser, CurrencyEnum.RUB, 1000.0);
        entityManager.persist(fromAccount);

        Account toAccount = new Account(null, toUser, CurrencyEnum.USD, 50.0);
        entityManager.persist(toAccount);
        entityManager.flush();

        ExchangeDto fromExchange = new ExchangeDto(CurrencyEnum.RUB, 1.0);
        ExchangeDto toExchange = new ExchangeDto(CurrencyEnum.USD, 90.0);

        TransferDto transferDto = new TransferDto(fromUser.getId(), fromExchange, toExchange, 180.0, toUser.getId());
        accountService.transfer(transferDto);
        entityManager.flush();

        assertEquals(820.0, fromAccount.getValue(), 0.01);
        assertEquals(52.0, toAccount.getValue(), 0.01);
    }

}
