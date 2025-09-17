package com.exchange.contracts.service;

import com.exchange.model.dto.CurrencyEnum;
import com.exchange.model.dto.ExchangeDto;
import com.exchange.model.entities.Exchange;
import com.exchange.repository.ExchangeRepository;
import com.exchange.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Equals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ExchangeService.class)
public class ExchangeServiceTest {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Test
    void setExchange() {
        ExchangeDto exchangeDto1 = new ExchangeDto();
        exchangeDto1.setCurrency(CurrencyEnum.USD);
        exchangeDto1.setValue(1.1);
        exchangeService.setExchange(exchangeDto1);

        Optional<Exchange> found1 = Optional.ofNullable(exchangeRepository.findByCurrency(CurrencyEnum.USD));
        assertTrue(found1.isPresent());
        assertEquals(1.1, found1.get().getValue());
    }

    @Test
    void getExchange() {
        Exchange mockExchange = new Exchange();
        mockExchange.setCurrency(CurrencyEnum.USD);
        mockExchange.setValue(1.05);
        exchangeRepository.save(mockExchange);

        Double result = exchangeService.getExchange(CurrencyEnum.USD);
        assertNotNull(result);
        assertEquals(1.05, result);
    }
}
