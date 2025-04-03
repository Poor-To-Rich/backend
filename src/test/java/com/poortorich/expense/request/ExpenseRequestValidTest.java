package com.poortorich.expense.request;

import com.poortorich.expense.fixture.enums.CategoryNameValidationCase;
import com.poortorich.expense.fixture.enums.CostValidationCase;
import com.poortorich.expense.fixture.enums.DateValidationCase;
import com.poortorich.expense.fixture.enums.MemoValidationCase;
import com.poortorich.expense.fixture.enums.TitleValidationCase;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.global.testcases.TestCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseRequestValidTest {

    private Validator validator;
    private ExpenseRequestTestBuilder expenseRequestTestBuilder;

    static Stream<Arguments> dateValidationCases() {
        return TestCase.getAllTestCases(DateValidationCase.class);
    }

    static Stream<Arguments> categoryNameValidationCases() {
        return TestCase.getAllTestCases(CategoryNameValidationCase.class);
    }

    static Stream<Arguments> titleValidationCases() {
        return TestCase.getAllTestCases(TitleValidationCase.class);
    }

    static Stream<Arguments> costValidationCases() {
        return TestCase.getAllTestCases(CostValidationCase.class);
    }

    static Stream<Arguments> memoValidationCases() {
        return TestCase.getAllTestCases(MemoValidationCase.class);
    }

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
        expenseRequestTestBuilder = new ExpenseRequestTestBuilder();
    }

    @ParameterizedTest
    @MethodSource("dateValidationCases")
    @DisplayName("Valid date")
    void invalidDate_shouldFailValidation(LocalDate date, String expectedErrorMessage) {
        ExpenseRequest request = expenseRequestTestBuilder.date(date).build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("categoryNameValidationCases")
    @DisplayName("Valid categoryName")
    void invalidCategoryName_shouldFailValidation(String categoryName, String expectedErrorMessage) {
        ExpenseRequest request = expenseRequestTestBuilder.categoryName(categoryName).build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("titleValidationCases")
    @DisplayName("Valid title")
    void invalidTitle_shouldFailValidation(String title, String expectedErrorMessage) {
        ExpenseRequest request = expenseRequestTestBuilder.title(title).build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("costValidationCases")
    @DisplayName("Valid cost")
    void invalidCost_shouldFailValidation(Long cost, String expectedErrorMessage) {
        ExpenseRequest request = expenseRequestTestBuilder.cost(cost).build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("memoValidationCases")
    @DisplayName("Valid memo")
    void invalidMemo_shouldFailValidation(String memo, String expectedErrorMessage) {
        ExpenseRequest request = expenseRequestTestBuilder.memo(memo).build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);

        violations.forEach(v -> {
            System.out.println(v.getMessage());
            System.out.println(v.getMessageTemplate());
        });

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }
}
