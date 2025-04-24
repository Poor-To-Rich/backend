package com.poortorich.expense.request;

import com.poortorich.expense.fixture.enums.CategoryNameValidationCase;
import com.poortorich.expense.fixture.enums.CostValidationCase;
import com.poortorich.expense.fixture.enums.DateValidationCase;
import com.poortorich.expense.fixture.enums.MemoValidationCase;
import com.poortorich.expense.fixture.enums.TitleValidationCase;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.global.testcases.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.poortorich.global.util.RequestValidTestHelper.assertValidationMessage;

public class ExpenseRequestValidTest {

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
        expenseRequestTestBuilder = new ExpenseRequestTestBuilder();
    }

    @ParameterizedTest
    @MethodSource("dateValidationCases")
    @DisplayName("Valid date")
    void invalidDate_shouldFailValidation(String date, String expectedErrorMessage) {
        assertValidationMessage(
                expenseRequestTestBuilder.date(date).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("categoryNameValidationCases")
    @DisplayName("Valid categoryName")
    void invalidCategoryName_shouldFailValidation(String categoryName, String expectedErrorMessage) {
        assertValidationMessage(
                expenseRequestTestBuilder.categoryName(categoryName).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("titleValidationCases")
    @DisplayName("Valid title")
    void invalidTitle_shouldFailValidation(String title, String expectedErrorMessage) {
        assertValidationMessage(
                expenseRequestTestBuilder.title(title).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("costValidationCases")
    @DisplayName("Valid cost")
    void invalidCost_shouldFailValidation(Long cost, String expectedErrorMessage) {
        assertValidationMessage(
                expenseRequestTestBuilder.cost(cost).build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("memoValidationCases")
    @DisplayName("Valid memo")
    void invalidMemo_shouldFailValidation(String memo, String expectedErrorMessage) {
        assertValidationMessage(
                expenseRequestTestBuilder.memo(memo).build(),
                expectedErrorMessage
        );
    }
}
