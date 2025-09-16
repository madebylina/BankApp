package com.generator.dto;

import lombok.Getter;

@Getter
public enum CurrencyEnum {
    RUB("Рубли"),
    USD("Доллары"),
    CNY("Юани");

    private final String title;

    CurrencyEnum(String title) {
        this.title = title;
    }

}