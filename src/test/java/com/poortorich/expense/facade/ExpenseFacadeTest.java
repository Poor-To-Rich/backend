package com.poortorich.expense.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.iteration.service.IterationService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
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

    @Mock
    private UserService userService;

    @Mock
    private IterationService iterationService;

    @InjectMocks
    private ExpenseFacade expenseFacade;

    private ExpenseRequest expenseRequest;
    private Category category;
    private User user;
    private Expense expense;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequestTestBuilder().build();
        category = Category.builder()
                .name(ExpenseFixture.VALID_CATEGORY_NAME)
                .build();
        user = User.builder()
                .username("test")
                .build();
        expense = Expense.builder().build();
    }

    @Test
    @DisplayName("Category, Expense 서비스가 적절히 호출된다.")
    void createExpense_shouldCallServiceMethods() {
        when(userService.findUserByUsername(user.getUsername())).thenReturn(user);
        when(categoryService.findCategoryByName(expenseRequest.getCategoryName(), user)).thenReturn(category);
        when(expenseService.createExpense(expenseRequest, category, user)).thenReturn(expense);

        expenseFacade.createExpense(expenseRequest, user.getUsername());
        verify(expenseService).createExpense(expenseRequest, category, user);
        verify(categoryService).findCategoryByName(expenseRequest.getCategoryName(), user);
        verify(iterationService).createIterationExpenses(expenseRequest.getCustomIteration(), expense, user);
    }
}
