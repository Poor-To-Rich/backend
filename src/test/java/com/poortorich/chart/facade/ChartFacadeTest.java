package com.poortorich.chart.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryFixture;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.service.ChartService;
import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChartFacadeTest {

    @Mock
    UserService userService;

    @Mock
    CategoryService categoryService;

    @Mock
    ExpenseService expenseService;

    @Mock
    ChartService chartService;

    @Mock
    AccountBookService accountBookService;

    @InjectMocks
    ChartFacade chartFacade;

    @Test
    @DisplayName("총 지출 금액과 저축 정보를 조회할 때 모든 서비스가 올바르게 호출된다.")
    void getTotalExpenseAndSaving_whenValidUsernameAndDate_thenCallsAllServicesCorrectly() {
        User mockUser = UserFixture.createDefaultUser();
        List<AccountBook> mockExpense = ExpenseFixture.getAllExpense().stream()
                .map(expense -> (AccountBook) expense)
                .toList();
        Category savingCategory = CategoryFixture.SAVINGS_INVESTMENT;
        String date = "2025-05";

        when(userService.findUserByUsername(anyString())).thenReturn(mockUser);
        when(accountBookService.getAccountBookBetweenDates(
                any(User.class), any(LocalDate.class), any(LocalDate.class), any(AccountBookType.class)))
                .thenReturn(mockExpense);

        when(categoryService.findCategoryByName(anyString(), any(User.class))).thenReturn(savingCategory);

        chartFacade.getTotalExpenseAmountAndSaving(mockUser.getUsername(), date, AccountBookType.EXPENSE);

        verify(userService, times(1)).findUserByUsername(mockUser.getUsername());
        verify(accountBookService, times(1)).getAccountBookBetweenDates(
                any(User.class), any(LocalDate.class), any(LocalDate.class), any(AccountBookType.class));
        verify(categoryService, times(1)).findCategoryByName(anyString(), any(User.class));
    }
}
