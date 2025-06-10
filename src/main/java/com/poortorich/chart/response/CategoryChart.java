package com.poortorich.chart.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryChart {
    
    private Long id;
    private String color;
    private String name;
    private BigDecimal rate;
    private Long amount;
}
