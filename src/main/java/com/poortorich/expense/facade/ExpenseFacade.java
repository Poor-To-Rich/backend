package com.poortorich.expense.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.expense.request.ExpenseDeleteRequest;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.expense.response.ExpenseInfoResponse;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.response.Response;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.iteration.service.IterationService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseFacade {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final IterationService iterationService;
    private final UserService userService;

    @Transactional
    public Response createExpense(ExpenseRequest expenseRequest, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName(), user);
        Expense expense = expenseService.create(expenseRequest, category, user);
        if (expense.getIterationType() != IterationType.DEFAULT) {
            createIterationExpense(expenseRequest, expense, user);
        }

        return ExpenseResponse.CREATE_EXPENSE_SUCCESS;
    }

    public void createIterationExpense(ExpenseRequest expenseRequest, Expense expense, User user) {
        List<Expense> iterationExpenses
                = iterationService.createIterationExpenses(expenseRequest.getCustomIteration(), expense, user);
        List<Expense> savedExpenses = expenseService.createExpenseAll(iterationExpenses);
        iterationService.createIterationInfo(expenseRequest, expense, savedExpenses, user);
    }

    @Transactional
    public ExpenseInfoResponse getExpense(Long id, String username) {
        User user = userService.findUserByUsername(username);
        IterationExpenses iterationExpenses = expenseService.getIterationExpenses(id, user);

        CustomIterationInfoResponse customIteration = null;
        if (iterationExpenses != null) {
            customIteration = iterationService.getCustomIteration(iterationExpenses);
        }

        return expenseService.getExpenseInfoResponse(id, user, customIteration);
    }

    @Transactional
    public ExpenseResponse deleteExpense(Long expenseId, ExpenseDeleteRequest expenseDeleteRequest, String username) {
        User user = userService.findUserByUsername(username);
        if (expenseDeleteRequest.parseIterationAction() == IterationAction.NONE) {
            expenseService.deleteExpense(expenseId, user);
        }

        if (expenseDeleteRequest.parseIterationAction() != IterationAction.NONE) {
            Expense expenseToDelete = expenseService.getExpenseOrThrow(expenseId, user);
            expenseService.deleteExpenseAll(
                    iterationService.deleteIterationExpenses(
                            expenseToDelete,
                            user,
                            expenseDeleteRequest.parseIterationAction())
            );
        }

        return ExpenseResponse.DELETE_EXPENSE_SUCCESS;
    }

    @Transactional
    public ExpenseResponse modifyExpense(String username, Long expenseId, ExpenseRequest expenseRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName(), user);
        Expense expense = expenseService.modifyExpense(expenseId, expenseRequest, category, user);

        IterationAction iterationAction = expenseRequest.parseIterationAction();
        if (iterationAction == IterationAction.NONE) {
            modifySingleExpense(expense, expenseRequest, user);
        }

        if (iterationAction != IterationAction.NONE) {
            modifyIterationExpenses(expense, expenseRequest, iterationAction, category, user);
        }

        return ExpenseResponse.MODIFY_EXPENSE_SUCCESS;
    }

    private void modifySingleExpense(Expense expense, ExpenseRequest expenseRequest, User user) {
        expenseService.modifyExpenseDate(expense, expenseRequest.parseDate());

        if (expenseRequest.getIsIterationModified()) {
            createIterationExpense(expenseRequest, expense, user);
        }
    }

    private void modifyIterationExpenses(
            Expense expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        if (expenseRequest.getIsIterationModified()) {
            handleModifiedIteration(expense, expenseRequest, iterationAction, category, user);
            return;
        }

        handleUnmodifiedIteration(expense, expenseRequest, iterationAction, category, user);
    }

    private void handleModifiedIteration(
            Expense expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        expenseService.deleteExpenseAll(
                iterationService.deleteIterationExpenses(expense, user, iterationAction)
        );
        Expense newExpense = expenseService.create(expenseRequest, category, user);
        createIterationExpense(expenseRequest, newExpense, user);
    }

    private void handleUnmodifiedIteration(
            Expense expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        LocalDate expenseDate = expense.getExpenseDate();
        LocalDate requestDate = expenseRequest.parseDate();

        if (iterationAction == IterationAction.THIS_ONLY && !expenseDate.equals(requestDate)) {
            expenseService.modifyExpenseDate(expense, requestDate);
            return;
        }

        modifyIterationGeneratedExpenses(expense, expenseRequest, iterationAction, category, user);
    }

    private void modifyIterationGeneratedExpenses(
            Expense expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        List<IterationExpenses> modifyIterationExpenses
                = iterationService.getIterationExpensesByIterationAction(expense, user, iterationAction);
        for (IterationExpenses iterationExpenses : modifyIterationExpenses) {
            expenseService.modifyExpense(iterationExpenses.getGeneratedExpense(), expenseRequest, category);
        }
    }
}
