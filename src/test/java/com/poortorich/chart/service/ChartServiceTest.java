package com.poortorich.chart.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryFixture;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.expense.fixture.ExpenseFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChartServiceTest {

    @InjectMocks
    private ChartService chartService;

    @Test
    @DisplayName("총 지출액과 저축액을 계산한다.")
    void getTotalExpenseAndSavings_whenVariousCategoryExpenseAreGiven_shouldCalculateTotalAmountAndSavingCorrectly() {
        List<AccountBook> expenses = List.of(
                ExpenseFixture.HOUSING_EXPENSE_1(),
                ExpenseFixture.FOOD_EXPENSE_1(),
                ExpenseFixture.BEAUTY_EXPENSE_1(),
                ExpenseFixture.CULTURE_HOBBY_EXPENSE_1(),
                ExpenseFixture.ALCOHOL_ENTERTAINMENT_EXPENSE_1(),
                ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1()
        );
        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;

        long expectedTotalExpense = ExpenseFixture.HOUSING_EXPENSE_1().getCost()
                + ExpenseFixture.FOOD_EXPENSE_1().getCost()
                + ExpenseFixture.BEAUTY_EXPENSE_1().getCost()
                + ExpenseFixture.CULTURE_HOBBY_EXPENSE_1().getCost()
                + ExpenseFixture.ALCOHOL_ENTERTAINMENT_EXPENSE_1().getCost();
        long expectedTotalSavings = ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost();

        TotalAmountAndSavingResponse result = chartService.getTotalAmountAndSavings(
                expenses, expenses,
                savingCategory);

        assertThat(result.getTotalAmount()).isEqualTo(expectedTotalExpense);
        assertThat(result.getTotalSaving()).isEqualTo(expectedTotalSavings);
    }

    @Test
    @DisplayName("저축 카테고리만 있는 경우 총 지출액은 0이 된다.")
    void getTotalExpenseAndSavings_whenOnlySavingCategoryExists_shouldReturnZeroTotalAmountAndCalculateSavingsOnly() {
        List<AccountBook> expenses = List.of(
                ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1()
        );
        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;

        long expectedTotalExpense = 0L;
        long expectedTotalSavings = ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost();

        TotalAmountAndSavingResponse result = chartService.getTotalAmountAndSavings(
                expenses,
                expenses,
                savingCategory
        );

        assertThat(result.getTotalAmount()).isEqualTo(expectedTotalExpense);
        assertThat(result.getTotalSaving()).isEqualTo(expectedTotalSavings);
    }

    @Test
    @DisplayName("빈 지출 목록인 경우 모든 값이 0이 된다.")
    void getTotalExpenseAndSavings_whenEmptyAmount_shouldReturnAllZeroValues() {
        List<AccountBook> expenses = List.of();
        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;

        long expectedTotalExpense = 0L;
        long expectedTotalSaving = 0L;

        TotalAmountAndSavingResponse result = chartService.getTotalAmountAndSavings(
                expenses,
                expenses,
                savingCategory
        );

        assertThat(result.getTotalAmount()).isEqualTo(expectedTotalExpense);
        assertThat(result.getTotalSaving()).isEqualTo(expectedTotalSaving);
    }

    @Test
    @DisplayName("동일한 카테고리의 여러 지출이 있는 경우 합계를 정확히 계산한다.")
    void getTotalAmountAndSavings_whenMultipleExpensesInSameCategory_shouldCalculateSumCorrectly() {
        List<AccountBook> expenses = List.of(
                ExpenseFixture.FOOD_EXPENSE_1(),
                ExpenseFixture.FOOD_EXPENSE_2(),
                ExpenseFixture.FOOD_EXPENSE_3(),
                ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1(),
                ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1()
        );

        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;
        long expectedExpense = ExpenseFixture.FOOD_EXPENSE_1().getCost()
                + ExpenseFixture.FOOD_EXPENSE_2().getCost()
                + ExpenseFixture.FOOD_EXPENSE_3().getCost();

        long expectedSavings = ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost()
                + ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost();

        TotalAmountAndSavingResponse result = chartService.getTotalAmountAndSavings(expenses, expenses, savingCategory);

        assertThat(result.getTotalAmount()).isEqualTo(expectedExpense);
        assertThat(result.getTotalSaving()).isEqualTo(expectedSavings);
    }
}
