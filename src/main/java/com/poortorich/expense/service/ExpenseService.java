package com.poortorich.expense.service;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public Expense createExpense(ExpenseRequest expenseRequest, Category category, User user) {
        Expense expense = buildExpense(expenseRequest, category, user);
        expenseRepository.save(expense);
        return expense;
    }

    public List<Expense> createExpenseAll(List<Expense> expenses) {
        return expenseRepository.saveAll(expenses);
    }

    private Expense buildExpense(ExpenseRequest expenseRequest, Category category, User user) {
        return Expense.builder()
                .expenseDate(expenseRequest.parseDate())
                .category(category)
                .title(expenseRequest.trimTitle())
                .cost(expenseRequest.getCost())
                .paymentMethod(expenseRequest.parsePaymentMethod())
                .memo(expenseRequest.getMemo())
                .iterationType(expenseRequest.parseIterationType())
                .user(user)
                .build();
    }
}
