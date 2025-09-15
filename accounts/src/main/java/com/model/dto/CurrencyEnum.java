package com.model.dto;

public enum CurrencyEnum {
    RUB("Рубли"),
    USD("Доллары"),
    CNY("Юани");

    private final String title;

    CurrencyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
