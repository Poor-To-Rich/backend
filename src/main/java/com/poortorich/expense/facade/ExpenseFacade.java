package com.poortorich.expense.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.accountbook.response.InfoResponse;
import com.poortorich.accountbook.response.IterationDetailsResponse;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.request.ExpenseDeleteRequest;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.response.Response;
import com.poortorich.iteration.entity.Iteration;
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

    private static final AccountBookType accountBookType = AccountBookType.EXPENSE;

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final IterationService iterationService;
    private final UserService userService;
    // Change Service Layer
    private final AccountBookService accountBookService;

    @Transactional
    public Response createExpense(ExpenseRequest expenseRequest, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName(), user);
        AccountBook expense = accountBookService.create(user, category, expenseRequest, accountBookType);
        if (expense.getIterationType() != IterationType.DEFAULT) {
            createIterationExpense(user, expenseRequest, expense);
        }

        return ExpenseResponse.CREATE_EXPENSE_SUCCESS;
    }

    public void createIterationExpense(User user, ExpenseRequest expenseRequest, AccountBook expense) {
        List<AccountBook> iterationExpenses
                = iterationService.createIterations(user, expenseRequest.getCustomIteration(), expense, accountBookType);
        List<AccountBook> savedExpenses = accountBookService.createAccountBookAll(iterationExpenses, accountBookType);
        iterationService.createIterationInfo(user, expenseRequest, expense, savedExpenses, accountBookType);
    }

    @Transactional
    public InfoResponse getExpense(Long id, String username) {
        User user = userService.findUserByUsername(username);
        Iteration iterationExpenses = accountBookService.getIteration(user, id, accountBookType);

        CustomIterationInfoResponse customIteration = null;
        if (iterationExpenses != null) {
            customIteration = iterationService.getCustomIteration(iterationExpenses);
        }

        return accountBookService.getInfoResponse(user, id, customIteration, accountBookType);
    }

    @Transactional
    public ExpenseResponse deleteExpense(Long expenseId, ExpenseDeleteRequest expenseDeleteRequest, String username) {
        User user = userService.findUserByUsername(username);
        if (expenseDeleteRequest.parseIterationAction() == IterationAction.NONE) {
            accountBookService.deleteAccountBook(expenseId, user, accountBookType);
            // expenseService.deleteExpense(expenseId, user);
        }

        if (expenseDeleteRequest.parseIterationAction() != IterationAction.NONE) {
            AccountBook expenseToDelete = accountBookService.getAccountBookOrThrow(expenseId, user, accountBookType);
//            Expense expenseToDelete = expenseService.getExpenseOrThrow(expenseId, user);
            accountBookService.deleteAccountBookAll(
                    iterationService.deleteIterations(
                            expenseToDelete,
                            user,
                            expenseDeleteRequest.parseIterationAction(),
                            accountBookType),
                    accountBookType
            );
        }

        return ExpenseResponse.DELETE_EXPENSE_SUCCESS;
    }

    @Transactional
    public ExpenseResponse modifyExpense(String username, Long expenseId, ExpenseRequest expenseRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(expenseRequest.getCategoryName(), user);
        AccountBook expense = accountBookService.modifyAccountBook(user, category, expenseId, expenseRequest, accountBookType);
        expenseService.modifyPaymentMethod((Expense) expense, expenseRequest.parsePaymentMethod());

        IterationAction iterationAction = expenseRequest.parseIterationAction();
        if (iterationAction == IterationAction.NONE) {
            modifySingleExpense(expense, expenseRequest, user);
        }

        if (iterationAction != IterationAction.NONE) {
            modifyIterationExpenses(expense, expenseRequest, iterationAction, category, user);
        }

        return ExpenseResponse.MODIFY_EXPENSE_SUCCESS;
    }

    private void modifySingleExpense(AccountBook expense, ExpenseRequest expenseRequest, User user) {
        expenseService.modifyExpenseDate((Expense) expense, expenseRequest.parseDate());

        if (expenseRequest.getIsIterationModified()) {
            createIterationExpense(user, expenseRequest, expense);
        }
    }

    private void modifyIterationExpenses(
            AccountBook expense,
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
            AccountBook expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        accountBookService.deleteAccountBookAll(
                iterationService.deleteIterations(expense, user, iterationAction, accountBookType),
                accountBookType
        );
        AccountBook newExpense = accountBookService.create(user, category, expenseRequest, accountBookType);
        createIterationExpense(user, expenseRequest, newExpense);
    }

    private void handleUnmodifiedIteration(
            AccountBook expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        LocalDate expenseDate = expense.getAccountBookDate();
        LocalDate requestDate = expenseRequest.parseDate();

        if (iterationAction == IterationAction.THIS_ONLY && !expenseDate.equals(requestDate)) {
            expenseService.modifyExpenseDate((Expense) expense, requestDate);
            return;
        }

        modifyIterationGeneratedExpenses(expense, expenseRequest, iterationAction, category, user);
    }

    private void modifyIterationGeneratedExpenses(
            AccountBook expense,
            ExpenseRequest expenseRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        List<Iteration> modifyIterationExpenses
                = iterationService.getIterationByIterationAction(expense, user, iterationAction, accountBookType);
        for (Iteration iteration : modifyIterationExpenses) {
            accountBookService.modifyAccountBook(iteration.getGeneratedAccountBook(), expenseRequest, category);
            expenseService.modifyPaymentMethod((Expense) expense, expenseRequest.parsePaymentMethod());
        }
    }

    public IterationDetailsResponse getExpenseIterationDetails(String username) {
        User user = userService.findUserByUsername(username);

        List<Long> originalExpenseIds = iterationService.getIterationAccountBookIds(accountBookType);
        return accountBookService.getIterationDetails(user, originalExpenseIds, accountBookType);
    }
}
