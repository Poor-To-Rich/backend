package com.poortorich.expense.service;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseInfoResponse;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.iteration.entity.IterationExpenses;
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

    public IterationExpenses getIterationExpenses(Long id, String username) {
        Expense expense = getExpenseOrThrow(id, username);
        return expense.getGeneratedIterationExpenses();
    }

    public ExpenseInfoResponse getExpenseInfoResponse(Long id, String username, CustomIterationInfoResponse customIteration) {
        Expense expense = getExpenseOrThrow(id, username);
        return ExpenseInfoResponse.builder()
                .date(expense.getExpenseDate())
                .categoryName(expense.getCategory().getName())
                .title(expense.getTitle())
                .cost(expense.getCost())
                .paymentMethod(expense.getPaymentMethod().toString())
                .memo(expense.getMemo())
                .iterationType(expense.getIterationType().toString())
                .customIteration(customIteration)
                .build();
    }

    public void deleteExpense(Long expenseId, String username) {
        Expense expense = getExpenseOrThrow(expenseId, username);
        expenseRepository.delete(expense);
    }

    public Expense getExpenseOrThrow(Long id, String username) {
        return expenseRepository.findByIdAndUser(id, findUserByUsername(username))
                .orElseThrow(() -> new NotFoundException(ExpenseResponse.EXPENSE_NON_EXISTENT));
    }

    public void deleteExpenseAll(List<Expense> deleteExpenses) {
        expenseRepository.deleteAll(deleteExpenses);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
    }
}
