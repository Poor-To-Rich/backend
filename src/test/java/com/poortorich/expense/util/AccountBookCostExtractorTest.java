package com.poortorich.expense.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryFixture;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.fixture.ExpenseFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AccountBookCostExtractorTest {

    @Test
    @DisplayName("모든 비용을 추출한다.")
    void extract_ExtractCostAll() {
        List<Expense> expenses = ExpenseFixture.getAllExpense();

        List<Long> costs = AccountBookCostExtractor.extract(expenses.stream()
                .map(expense -> (AccountBook) expense)
                .toList());

        assertThat(costs).hasSize(15);
        assertThat(costs).containsExactly(
                ExpenseFixture.HOUSING_EXPENSE_1().getCost(),
                ExpenseFixture.FOOD_EXPENSE_1().getCost(),
                ExpenseFixture.TRANSPORTATION_EXPENSE_1().getCost(),
                ExpenseFixture.SHOPPING_EXPENSE_1().getCost(),
                ExpenseFixture.HEALTH_MEDICAL_EXPENSE_1().getCost(),
                ExpenseFixture.EDUCATION_EXPENSE_1().getCost(),
                ExpenseFixture.CULTURE_HOBBY_EXPENSE_1().getCost(),
                ExpenseFixture.GIFTS_EVENTS_EXPENSE_1().getCost(),
                ExpenseFixture.BEAUTY_EXPENSE_1().getCost(),
                ExpenseFixture.CAFE_EXPENSE_1().getCost(),
                ExpenseFixture.PET_CATE_EXPENSE_1().getCost(),
                ExpenseFixture.OTHER_EXPENSE_1().getCost(),
                ExpenseFixture.ALCOHOL_ENTERTAINMENT_EXPENSE_1().getCost(),
                ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost(),
                ExpenseFixture.TRAVEL_ACCOMMODATION_EXPENSE_1().getCost()
        );
    }

    @Test
    @DisplayName("저축 카테고리 비용만 추출한다.")
    void extractByCategory_savingsInvestmentCategory() {
        List<AccountBook> expenses = ExpenseFixture.getAllExpense().stream()
                .map(expense -> (AccountBook) expense)
                .toList();
        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;

        List<Long> costs = AccountBookCostExtractor.extractByCategory(expenses, savingCategory);

        assertThat(costs).hasSize(1);
        assertThat(costs).containsExactly(ExpenseFixture.SAVINGS_INVESTMENT_EXPENSE_1().getCost());
    }

    @Test
    @DisplayName("주택 카테고리 비용만 추출한다")
    void extractByCategory_housing() {
        List<AccountBook> expenses = ExpenseFixture.getAllExpense().stream()
                .map(expense -> (AccountBook) expense)
                .toList();
        Category housingCategory = CategoryFixture.HOUSING;

        List<Long> costs = AccountBookCostExtractor.extractByCategory(expenses, housingCategory);

        assertThat(costs).hasSize(1);
        assertThat(costs).containsExactly(ExpenseFixture.HOUSING_EXPENSE_1().getCost());
    }

    @Test
    @DisplayName("여러 개의 음식 카테고리가 존재하는 경우 음식 카테고리만 추출한다.")
    void extractByCategory_food() {
        List<Expense> expenses = List.of(
                ExpenseFixture.HOUSING_EXPENSE_1(),
                ExpenseFixture.BEAUTY_EXPENSE_1(),
                ExpenseFixture.FOOD_EXPENSE_1(),
                ExpenseFixture.FOOD_EXPENSE_2(),
                ExpenseFixture.FOOD_EXPENSE_3()
        );
        Category foodCategory = CategoryFixture.FOOD;

        List<Long> costs = AccountBookCostExtractor.extractByCategory(expenses.stream()
                        .map(expense -> (AccountBook) expense)
                        .toList(),
                foodCategory);

        assertThat(costs).hasSize(3);
        assertThat(costs).containsExactly(
                ExpenseFixture.FOOD_EXPENSE_1().getCost(),
                ExpenseFixture.FOOD_EXPENSE_2().getCost(),
                ExpenseFixture.FOOD_EXPENSE_3().getCost()
        );
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 조회하면 빈 리스트를 반환한다.")
    void extractByCategory_NotExistsCategory() {
        List<AccountBook> expenses = ExpenseFixture.getAllExpense().stream()
                .map(expense -> (AccountBook) expense)
                .toList();
        Category nonExistentCategory = null;

        List<Long> costs = AccountBookCostExtractor.extractByCategory(expenses, nonExistentCategory);

        assertThat(costs).isEmpty();
    }

    @Test
    @DisplayName("빈 리스트에서 카테고리 추출하면 빈 리스트를 반환한다.")
    void extractByCategory_EmptyList() {
        List<AccountBook> emptyExpenses = List.of();
        Category savingsCategory = CategoryFixture.SAVINGS_INVESTMENT;

        List<Long> costs = AccountBookCostExtractor.extractByCategory(emptyExpenses, savingsCategory);

        assertThat(costs).isEmpty();
    }

    @Test
    @DisplayName("저축/투자 카테고리를 제외한 모든 비용을 추출한다.")
    void extractExcludingCategory_savingsInvestment() {
        List<AccountBook> expenses = ExpenseFixture.getAllExpense().stream()
                .map(expense -> (AccountBook) expense)
                .toList();
        Category excludedCategory = CategoryFixture.SAVINGS_INVESTMENT;

        List<Long> costs = AccountBookCostExtractor.extractExcludingCategory(expenses, excludedCategory);

        assertThat(costs).hasSize(14); // 15개 중 1개 제외
        assertThat(costs).containsExactly(
                ExpenseFixture.HOUSING_EXPENSE_1().getCost(),
                ExpenseFixture.FOOD_EXPENSE_1().getCost(),
                ExpenseFixture.TRANSPORTATION_EXPENSE_1().getCost(),
                ExpenseFixture.SHOPPING_EXPENSE_1().getCost(),
                ExpenseFixture.HEALTH_MEDICAL_EXPENSE_1().getCost(),
                ExpenseFixture.EDUCATION_EXPENSE_1().getCost(),
                ExpenseFixture.CULTURE_HOBBY_EXPENSE_1().getCost(),
                ExpenseFixture.GIFTS_EVENTS_EXPENSE_1().getCost(),
                ExpenseFixture.BEAUTY_EXPENSE_1().getCost(),
                ExpenseFixture.CAFE_EXPENSE_1().getCost(),
                ExpenseFixture.PET_CATE_EXPENSE_1().getCost(),
                ExpenseFixture.OTHER_EXPENSE_1().getCost(),
                ExpenseFixture.ALCOHOL_ENTERTAINMENT_EXPENSE_1().getCost(),
                ExpenseFixture.TRAVEL_ACCOMMODATION_EXPENSE_1().getCost()
        );
    }
}
