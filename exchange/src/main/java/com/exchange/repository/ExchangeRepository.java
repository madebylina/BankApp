package com.exchange.repository;

import com.exchange.model.dto.CurrencyEnum;
import com.exchange.model.entities.Exchange;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRepository extends CrudRepository<Exchange, Long> {

    Exchange findByCurrency(CurrencyEnum currencyEnum);
}
