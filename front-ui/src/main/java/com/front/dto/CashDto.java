package com.front.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashDto {

    private Long userId;
    private CurrencyEnum currency;
    private Double value;
    private CashActionEnum cashAction;

}
