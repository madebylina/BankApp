package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private Long userId;
    private CurrencyEnum currency;
    private Boolean exists;
    private Double value;

}
