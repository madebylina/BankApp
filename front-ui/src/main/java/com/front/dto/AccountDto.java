package com.front.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {

    private Long id;

    private Long userId;
    private CurrencyEnum Currency;
    private Boolean exists;
    private Double value;

}