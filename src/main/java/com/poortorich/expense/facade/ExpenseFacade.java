package com.poortorich.expense.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseFacade {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    @Transactional
    public Response createExpense(ExpenseRequest expenseRequest) {
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName());
        expenseService.createExpense(expenseRequest, category);

        return ExpenseResponse.CREATE_EXPENSE_SUCCESS;
    }
}
