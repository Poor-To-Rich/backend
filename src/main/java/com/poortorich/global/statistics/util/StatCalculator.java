package com.poortorich.global.statistics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatCalculator {

    private static final BigDecimal PERCENTAGE_OFFSET = BigDecimal.valueOf(100);
    private static final int DIVISION_SCALE = 10;
    private static final int ROUNDING_SCALE = 1;

    public static BigDecimal calculateSum(List<Long> numbers) {
        if (Objects.isNull(numbers) || numbers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return numbers.stream()
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calculateAverage(List<Long> numbers) {
        if (Objects.isNull(numbers) || numbers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return StatCalculator.calculateSum(numbers)
                .divide(BigDecimal.valueOf(numbers.size()), DIVISION_SCALE, RoundingMode.HALF_UP)
                .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
    }

    public static List<BigDecimal> calculatePercentages(List<Long> numbers) {
        if (Objects.isNull(numbers) || numbers.isEmpty()) {
            return Collections.emptyList();
        }

        BigDecimal sum = StatCalculator.calculateSum(numbers);

        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            return numbers.stream()
                    .map(number -> BigDecimal.ZERO)
                    .toList();
        }

        return StatCalculator.adjustPercentages(numbers.stream()
                .map(BigDecimal::valueOf)
                .map(number -> number
                        .divide(sum, DIVISION_SCALE, RoundingMode.HALF_UP)
                        .multiply(PERCENTAGE_OFFSET)
                        .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private static List<BigDecimal> adjustPercentages(List<BigDecimal> numbers) {
        BigDecimal diff = PERCENTAGE_OFFSET.subtract(
                numbers.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        int maxIndex = IntStream.range(0, numbers.size())
                .boxed()
                .max(Comparator.comparing(numbers::get))
                .orElse(0);

        numbers.set(maxIndex, numbers.get(maxIndex).add(diff));

        return numbers;
    }
}
