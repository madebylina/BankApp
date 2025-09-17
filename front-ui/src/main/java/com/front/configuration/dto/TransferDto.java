package com.front.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

    private Long userId;
    private ExchangeDto fromExchange;
    private ExchangeDto toExchange;
    private Double value;
    private Long toUserId;

}