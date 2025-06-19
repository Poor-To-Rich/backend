package com.poortorich.expense.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.accountbook.response.AccountBookCreateResponse;
import com.poortorich.accountbook.response.InfoResponse;
import com.poortorich.accountbook.response.IterationDetailsResponse;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.service.CategoryService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.accountbook.request.AccountBookDeleteRequest;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.service.ExpenseService;
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
    private static final CategoryType categoryType = CategoryType.DEFAULT_EXPENSE;

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final IterationService iterationService;
    private final UserService userService;
    private final AccountBookService accountBookService;

    @Transactional
    public AccountBookCreateResponse createExpense(ExpenseRequest expenseRequest, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(user, expenseRequest.getCategoryName(), categoryType);
        AccountBook expense = accountBookService.create(user, category, expenseRequest, accountBookType);
        if (expense.getIterationType() != IterationType.DEFAULT) {
            createIterationExpense(user, expenseRequest, expense);
        }

        return AccountBookCreateResponse.builder()
                .id(expense.getId())
                .build();
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
        AccountBook expense = accountBookService.getAccountBookOrThrow(id, user, accountBookType);
        Iteration iterationExpenses = accountBookService.getIteration(expense);

        CustomIterationInfoResponse customIteration = null;
        if (expense.getIterationType() == IterationType.CUSTOM) {
            customIteration = iterationService.getCustomIteration(iterationExpenses);
        }

        return accountBookService.getInfoResponse(expense, customIteration, accountBookType);
    }

    @Transactional
    public ExpenseResponse deleteExpense(Long expenseId, AccountBookDeleteRequest accountBookDeleteRequest, String username) {
        User user = userService.findUserByUsername(username);
        if (accountBookDeleteRequest.parseIterationAction() == IterationAction.NONE) {
            accountBookService.deleteAccountBook(expenseId, user, accountBookType);
        }

        if (accountBookDeleteRequest.parseIterationAction() != IterationAction.NONE) {
            AccountBook expenseToDelete = accountBookService.getAccountBookOrThrow(expenseId, user, accountBookType);
            accountBookService.deleteAccountBookAll(
                    iterationService.deleteIterations(
                            expenseToDelete,
                            user,
                            accountBookDeleteRequest.parseIterationAction(),
                            accountBookType),
                    accountBookType
            );
        }

        return ExpenseResponse.DELETE_EXPENSE_SUCCESS;
    }

    @Transactional
    public ExpenseResponse modifyExpense(String username, Long expenseId, ExpenseRequest expenseRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(user, expenseRequest.getCategoryName(), categoryType);
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
        if (newExpense.getIterationType() != IterationType.DEFAULT) {
            createIterationExpense(user, expenseRequest, newExpense);
        }
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

        List<Long> originalExpenseIds = iterationService.getIterationAccountBookIds(user, accountBookType);
        return accountBookService.getIterationDetails(user, originalExpenseIds, accountBookType);
    }
}
