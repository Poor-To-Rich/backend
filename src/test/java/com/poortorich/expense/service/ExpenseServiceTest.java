package com.poortorich.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Captor
    private ArgumentCaptor<Expense> expenseCaptor;

    private ExpenseRequest expenseRequest;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequestTestBuilder().build();
        category = Category.builder()
                .name(ExpenseFixture.VALID_CATEGORY_NAME)
                .build();
        user = User.builder()
                .username("test")
                .build();
    }

    @Test
    @DisplayName("유효한 지출 정보가 성공적으로 저장된다.")
    void createValidExpense() {
        expenseService.create(expenseRequest, category, user);

        verify(expenseRepository).save(expenseCaptor.capture());
        Expense savedExpense = expenseCaptor.getValue();
        assertAll(
                () -> assertThat(savedExpense.getExpenseDate()).isEqualTo(expenseRequest.getDate()),
                () -> assertThat(savedExpense.getCategory()).isEqualTo(category),
                () -> assertThat(savedExpense.getTitle()).isEqualTo(expenseRequest.getTitle()),
                () -> assertThat(savedExpense.getCost()).isEqualTo(expenseRequest.getCost()),
                () -> assertThat(savedExpense.getPaymentMethod()).isEqualTo(expenseRequest.parsePaymentMethod()),
                () -> assertThat(savedExpense.getMemo()).isEqualTo(expenseRequest.getMemo()),
                () -> assertThat(savedExpense.getIterationType()).isEqualTo(expenseRequest.parseIterationType())
        );
    }
}