package com.poortorich.expense.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseFacadeTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ExpenseFacade expenseFacade;

    private ExpenseRequest expenseRequest;
    private Category category;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequestTestBuilder().build();
        category = Category.builder()
                .name(ExpenseFixture.VALID_CATEGORY_NAME)
                .build();
    }

    @Test
    @DisplayName("Category, Expense 서비스가 적절히 호출된다.")
    void createExpense_shouldCallServiceMethods() {
        when(categoryService.findCategoryByName(expenseRequest.getCategoryName())).thenReturn(category);

        expenseFacade.createExpense(expenseRequest);
        verify(expenseService).createExpense(expenseRequest, category);
        verify(categoryService).findCategoryByName(expenseRequest.getCategoryName());
    }
}
