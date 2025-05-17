package com.poortorich.global.statistics.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poortorich.global.statistics.fixture.StatCalculatorTestFixture;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StatCalculatorTest {

    @Test
    @DisplayName("calculateSum 메소드는 숫자 리스트의 합계를 계산한다.")
    void calculateSum_whenListOfNumbers_thenReturnSum() {
        List<Long> numbers = StatCalculatorTestFixture.NUMBERS;
        BigDecimal expected = StatCalculatorTestFixture.EXPECTED_SUM;

        BigDecimal result = StatCalculator.calculateSum(numbers);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("calculateSum 메소드는 빈 리스트의 경우 0을 반환한다.")
    void calculateSum_whenEmptyList_thenReturnZero() {
        List<Long> emptyList = StatCalculatorTestFixture.EMPTY_LIST;
        BigDecimal expected = BigDecimal.ZERO;

        BigDecimal result = StatCalculator.calculateSum(emptyList);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("calculateAverage 메소드는 숫자 리스트의 평균을 계산한다.")
    void calculateAverage_whenListOfNumber_thenReturnAverage() {
        List<Long> numbers = StatCalculatorTestFixture.NUMBERS;
        BigDecimal expected = StatCalculatorTestFixture.EXPECTED_AVERAGE;

        BigDecimal result = StatCalculator.calculateAverage(numbers);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("calculateAverage 메소드는 빈 리스트인 경우 예외를 발생시킨다.")
    void calculateAverage_whenEmptyList_thenThrowException() {
        List<Long> emptyList = StatCalculatorTestFixture.EMPTY_LIST;

        assertThatThrownBy(() -> StatCalculator.calculateAverage(emptyList))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("calculatePercentages 메소드는 숫자 리스트의 백분율을 계산한다.")
    void calculatePercentages_whenListOfNumbers_thenReturnPercentages() {
        List<Long> numbers = StatCalculatorTestFixture.NUMBERS;
        List<BigDecimal> expected = StatCalculatorTestFixture.EXPECTED_PERCENTAGES;

        List<BigDecimal> result = StatCalculator.calculatePercentages(numbers);

        assertThat(expected.size()).isEqualTo(result.size());
        for (int i = 0; i < expected.size(); i++) {
            assertThat(expected.get(i)).isEqualTo(result.get(i));
        }
    }

    @Test
    @DisplayName("calculatePercentages 메소드는 백분율의 합이 100이 되도록 조정한다.")
    void calculatePercentages_whenListOfNumbers_thenAdjustPercentagesToTotal100() {
        List<Long> numbers = StatCalculatorTestFixture.NUMBERS;
        BigDecimal expectedSum = StatCalculatorTestFixture.PERCENTAGE_TOTAL;

        List<BigDecimal> percentages = StatCalculator.calculatePercentages(numbers);
        BigDecimal actualSum = percentages.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(expectedSum).isEqualTo(actualSum);
    }

    @Test
    @DisplayName("calculatePercentages 메소드는 단일 요소 리스트인 경우 100%를 반환한다.")
    void calculatePercentages_whenSingleElementList_thenReturn100Percent() {
        List<Long> singleElement = StatCalculatorTestFixture.SINGLE_ELEMENT_LIST;
        List<BigDecimal> expected = StatCalculatorTestFixture.EXPECTED_SINGLE_ELEMENT_PERCENTAGES;

        List<BigDecimal> result = StatCalculator.calculatePercentages(singleElement);

        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("calculatePercentages 메소드는 빈 리스트인 경우 예외를 발생시킨다.")
    void calculatePercentages_whenEmptyList_thenThrowException() {
        List<Long> emptyList = StatCalculatorTestFixture.EMPTY_LIST;

        assertThatThrownBy(() -> StatCalculator.calculatePercentages(emptyList))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("백분율의 합이 100%를 초과할 때 적절히 조정한다.")
    void calculatePercentages_whenSumIsOver100_thenAdjustCorrectly() {
        List<Long> numbers = StatCalculatorTestFixture.OVER_HUNDERED_LIST;

        List<BigDecimal> percentages = StatCalculator.calculatePercentages(numbers);

        BigDecimal result = percentages.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(result).isEqualTo(StatCalculatorTestFixture.PERCENTAGE_TOTAL);
    }

    @Test
    @DisplayName("백분율의 합이 100% 미만일 때 적절히 조정한다.")
    void calculatePercentages_whenSumIsUnder100_thenAdjustCorrectly() {
        List<Long> numbers = StatCalculatorTestFixture.UNDER_HUNDRED_LIST;

        List<BigDecimal> percentages = StatCalculator.calculatePercentages(numbers);

        BigDecimal result = percentages.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(result).isEqualTo(StatCalculatorTestFixture.PERCENTAGE_TOTAL);
    }
}
