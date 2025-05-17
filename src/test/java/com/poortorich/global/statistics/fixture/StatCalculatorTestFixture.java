package com.poortorich.global.statistics.fixture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class StatCalculatorTestFixture {

    public static final List<Long> EMPTY_LIST = List.of();
    public static final List<Long> NUMBERS = Arrays.asList(10L, 20L, 30L, 40L);
    public static final List<Long> SINGLE_ELEMENT_LIST = List.of(50L);

    public static final BigDecimal EXPECTED_SUM = BigDecimal.valueOf(100L);
    public static final BigDecimal EXPECTED_AVERAGE = BigDecimal.valueOf(25.0).setScale(1, RoundingMode.HALF_UP);
    public static final List<BigDecimal> EXPECTED_PERCENTAGES = Arrays.asList(
            BigDecimal.valueOf(10.0).setScale(1, RoundingMode.HALF_UP),
            BigDecimal.valueOf(20.0).setScale(1, RoundingMode.HALF_UP),
            BigDecimal.valueOf(30.0).setScale(1, RoundingMode.HALF_UP),
            BigDecimal.valueOf(40.0).setScale(1, RoundingMode.HALF_UP)
    );

    public static final List<BigDecimal> EXPECTED_SINGLE_ELEMENT_PERCENTAGES = List.of(
            BigDecimal.valueOf(100.0).setScale(1, RoundingMode.HALF_UP)
    );
    public static final BigDecimal PERCENTAGE_TOTAL = BigDecimal.valueOf(100.0).setScale(1, RoundingMode.HALF_UP);

    public static final List<Long> OVER_HUNDERED_LIST = List.of(3L, 3L, 1L);
    public static final List<Long> UNDER_HUNDRED_LIST = List.of(1L, 1L, 1L);
}
