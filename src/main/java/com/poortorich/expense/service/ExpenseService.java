package com.poortorich.expense.service;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public Expense createExpense(ExpenseRequest expenseRequest, Category category, String username) {
        Expense expense = buildExpense(expenseRequest, category, findUserByUsername(username));
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

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
    }
}
