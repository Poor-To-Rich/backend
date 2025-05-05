package com.poortorich.iteration.request;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.iteration.fixture.enums.CycleValidationCase;
import com.poortorich.iteration.fixture.enums.EndValidationCase;
import com.poortorich.iteration.fixture.enums.IterationRuleValidationCase;
import com.poortorich.iteration.fixture.enums.MonthlyOptionValidationCase;
import com.poortorich.iteration.util.CustomIterationTestBuilder;
import com.poortorich.iteration.util.IterationRuleTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.poortorich.global.util.RequestValidTestHelper.assertValidationMessage;

public class CustomIterationValidTest {

    private CustomIterationTestBuilder customIterationTestBuilder;
    private IterationRuleTestBuilder iterationRuleTestBuilder;

    static Stream<Arguments> iterationRuleValidationCases() {
        return TestCase.getAllTestCases(IterationRuleValidationCase.class);
    }

    static Stream<Arguments> monthlyOptionValidationCases() {
        return TestCase.getAllTestCases(MonthlyOptionValidationCase.class);
    }

    static Stream<Arguments> cycleValidationCases() {
        return TestCase.getAllTestCases(CycleValidationCase.class);
    }

    static Stream<Arguments> endValidationCases() {
        return TestCase.getAllTestCases(EndValidationCase.class);
    }

    @BeforeEach
    void setUp() {
        customIterationTestBuilder = new CustomIterationTestBuilder();
        iterationRuleTestBuilder = new IterationRuleTestBuilder();
    }

    @ParameterizedTest
    @MethodSource("iterationRuleValidationCases")
    @DisplayName("Valid iterationRule")
    void invalidIterationRule_shouldFailValidation(IterationRule iterationRule, String expectedErrorMessage) {
        assertValidationMessage(
                customIterationTestBuilder.iterationRule(iterationRule).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("monthlyOptionValidationCases")
    @DisplayName("Valid monthlyOption")
    void invalidMonthlyOption_shouldFailValidation(MonthlyOption monthlyOption, String expectedErrorMessage) {
        assertValidationMessage(
                customIterationTestBuilder.iterationRule(iterationRuleTestBuilder.monthlyOption(monthlyOption).build()).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("cycleValidationCases")
    @DisplayName("Valid cycle")
    void invalidCycle_shouldFailValidation(Integer cycle, String expectedErrorMessage) {
        assertValidationMessage(
                customIterationTestBuilder.cycle(cycle).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("endValidationCases")
    @DisplayName("Valid end")
    void invalidEnd_shouldFailValidation(End end, String expectedErrorMessage) {
        assertValidationMessage(
                customIterationTestBuilder.end(end).build(),
                expectedErrorMessage
        );
    }
}
