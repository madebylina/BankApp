package com.front.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {

    private String title;
    private String name;
    private Double value;

}