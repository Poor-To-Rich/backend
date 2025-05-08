package com.poortorich.expense.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.response.Response;
import com.poortorich.iteration.service.IterationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseFacade {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final IterationService iterationService;

    @Transactional
    public Response createExpense(ExpenseRequest expenseRequest, String username) {
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName(), username);
        Expense expense = expenseService.createExpense(expenseRequest, category, username);
        if (expense.getIterationType() != IterationType.DEFAULT) {
            createIterationExpense(expenseRequest, expense, username);
        }

        return ExpenseResponse.CREATE_EXPENSE_SUCCESS;
    }

    public void createIterationExpense(ExpenseRequest expenseRequest, Expense expense, String username) {
        List<Expense> iterationExpenses
                = iterationService.createIterationExpenses(expenseRequest.getCustomIteration(), expense, username);
        List<Expense> savedExpenses = expenseService.createExpenseAll(iterationExpenses);
        if (expense.getIterationType() == IterationType.CUSTOM) {
            iterationService.createIterationInfo(expenseRequest.getCustomIteration(), expense, savedExpenses, username);
        }
    }
}
