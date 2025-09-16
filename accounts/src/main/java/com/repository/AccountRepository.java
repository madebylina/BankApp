package com.repository;

import com.model.dto.CurrencyEnum;
import com.model.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByUserIdAndCurrency(Long userId, CurrencyEnum currency);
}
