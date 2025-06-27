package com.poortorich.chart.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryChart {

    private Long id;
    private String color;
    private String name;
    private Double rate;
    private Long amount;
}
