package com.poortorich.iteration.request;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.util.EndTestBuilder;
import com.poortorich.iteration.util.IterationRuleTestBuilder;
import com.poortorich.iteration.util.MonthlyOptionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomIterationConverterTest {

    private IterationRuleTestBuilder iterationRuleTestBuilder;
    private MonthlyOptionTestBuilder monthlyOptionTestBuilder;
    private EndTestBuilder endTestBuilder;

    @BeforeEach
    void setUp() {
        iterationRuleTestBuilder = new IterationRuleTestBuilder();
        monthlyOptionTestBuilder = new MonthlyOptionTestBuilder();
        endTestBuilder = new EndTestBuilder();
    }

    @Nested
    @DisplayName("Iteration Rule 예외 처리 예외 처리 테스트")
    class ExceptionIterationRule {

        @Test
        @DisplayName("유효하지 않은 반복 데이터 타입 입력 시 예외가 발생한다.")
        void whenInvalidIterationRuleType_throwsException() {
            IterationRule iterationRule = iterationRuleTestBuilder
                    .type("유효하지않은반복데이터타입")
                    .build();

            assertThatThrownBy(iterationRule::parseIterationType)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(IterationResponseMessages.ITERATION_RULE_TYPE_INVALID);
        }

        @Test
        @DisplayName("daysOfWeek 입력이 null 인 경우 null 을 반환한다.")
        void parseDaysOfWeek_whenNull_returnsNull() {
            IterationRule iterationRule = iterationRuleTestBuilder
                    .daysOfWeek(null)
                    .build();

            assertThat(iterationRule.parseDaysOfWeek()).isNull();
        }

        @Test
        @DisplayName("dayOfWeek 입력이 제대로 들어온 경우 입력을 String 으로 변환한다")
        void parseDaysOfWeek_whenValid_returnsString() {
            IterationRule iterationRule = iterationRuleTestBuilder
                    .daysOfWeek(List.of("월", "화"))
                    .build();

            assertThat(iterationRule.parseDaysOfWeek()).isEqualTo("MONDAY,TUESDAY");
        }

        @Test
        @DisplayName("daysOfWeek 가 null 인 경우 빈 리스트를 반환한다.")
        void daysOfWeekToList_whenNull_returnsEmptyList() {
            IterationRule iterationRule = iterationRuleTestBuilder
                    .daysOfWeek(null)
                    .build();

            assertThat(iterationRule.daysOfWeekToList()).isEmpty();
        }

        @Test
        @DisplayName("daysOfWeek 입력이 제대로 들어온 경우 String List 를 반환한다.")
        void daysOfWeekToList_whenValid_returnStringList() {
            IterationRule iterationRule = iterationRuleTestBuilder
                    .daysOfWeek(List.of("월", "화"))
                    .build();

            assertThat(iterationRule.daysOfWeekToList()).isEqualTo(List.of(Weekday.MONDAY, Weekday.TUESDAY));
        }
    }

    @Nested
    @DisplayName("Monthly Option 예외 처리 예외 처리 테스트")
    class ExceptionMonthlyOption {

        @Test
        @DisplayName("유효하지 않은 모드가 입력 시 예외가 발생한다.")
        void whenInvalidMonthlyMode_throwsException() {
            MonthlyOption monthlyOption = monthlyOptionTestBuilder
                    .mode("유효하지않은모드")
                    .build();

            assertThatThrownBy(monthlyOption::parseMonthlyMode)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(IterationResponseMessages.MONTHLY_MODE_INVALID);
        }

        @Test
        @DisplayName("유효하지 않은 요일 입력 시 예외가 발생한다.")
        void whenInvalidDayOfWeek_throwsException() {
            MonthlyOption monthlyOption = monthlyOptionTestBuilder
                    .dayOfWeek("유효하지않은요일")
                    .build();

            assertThatThrownBy(monthlyOption::parseDayOfWeek)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(IterationResponseMessages.DAY_OF_WEEK_INVALID);
        }
    }

    @Nested
    @DisplayName("End 예외 처리 예외 처리 테스트")
    class ExceptionEnd {

        @Test
        @DisplayName("유효하지 않은 종료 타입 입력 시 예외가 발생한다.")
        void whenInvalidEndType_throwsException() {
            End end = endTestBuilder
                    .type("유효하지않은타입")
                    .build();

            assertThatThrownBy(end::parseEndType)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(IterationResponseMessages.END_TYPE_INVALID);
        }

        @Test
        @DisplayName("유효하지 않은 종료 날짜 타입 입력 시 예외가 발생한다.")
        void whenInvalidEndDate_throwsException() {
            End end = endTestBuilder
                    .date("유효하지않은날짜타입")
                    .build();

            assertThatThrownBy(end::parseDate)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(IterationResponseMessages.END_DATE_INVALID);
        }
    }
}
