package com.poortorich.expense.service;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseInfoResponse;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.user.entity.User;
import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public IterationExpenses getIterationExpenses(Long id, User user) {
        Expense expense = getExpenseOrThrow(id, user);
        return expense.getGeneratedIterationExpenses();
    }

    public ExpenseInfoResponse getExpenseInfoResponse(Long id, User user, CustomIterationInfoResponse customIteration) {
        Expense expense = getExpenseOrThrow(id, user);
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

    @Transient
    public Expense modifyExpense(Long expenseId, ExpenseRequest expenseRequest, Category category, User user) {
        Expense expense = getExpenseOrThrow(expenseId, user);
        modifyExpense(expense, expenseRequest, category);
        return expense;
    }

    public void modifyExpense(Expense expense, ExpenseRequest expenseRequest, Category category) {
        expense.updateExpense(
                expenseRequest.trimTitle(),
                expenseRequest.getCost(),
                expenseRequest.parsePaymentMethod(),
                expenseRequest.getMemo(),
                expenseRequest.parseIterationType(),
                category);
    }

    @Transient
    public void modifyExpenseDate(Expense expense, LocalDate expenseDate) {
        expense.updateExpenseDate(expenseDate);
    }

    public void deleteExpense(Long expenseId, User user) {
        Expense expense = getExpenseOrThrow(expenseId, user);
        expenseRepository.delete(expense);
    }

    public Expense getExpenseOrThrow(Long id, User user) {
        return expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException(ExpenseResponse.EXPENSE_NON_EXISTENT));
    }

    public void deleteExpenseAll(List<Expense> deleteExpenses) {
        expenseRepository.deleteAll(deleteExpenses);
    }

    public List<Expense> getExpensesBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        return Optional.of(expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate))
                .orElseThrow(() -> new NotFoundException(ExpenseResponse.EXPENSE_NON_EXISTENT));
    }
}
