package com.poortorich.income.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.AccountBookDeleteRequest;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.accountbook.response.AccountBookActionResponse;
import com.poortorich.accountbook.response.AccountBookCreateResponse;
import com.poortorich.accountbook.response.InfoResponse;
import com.poortorich.accountbook.response.IterationDetailsResponse;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.service.CategoryService;
import com.poortorich.income.entity.Income;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.enums.IncomeResponse;
import com.poortorich.income.service.IncomeService;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.iteration.service.IterationService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeFacade {

    private static final AccountBookType accountBookType = AccountBookType.INCOME;
    private static final CategoryType categoryType = CategoryType.DEFAULT_INCOME;

    private final UserService userService;
    private final CategoryService categoryService;
    private final AccountBookService accountBookService;
    private final IterationService iterationService;
    private final IncomeService incomeService;

    @Transactional
    public AccountBookCreateResponse createIncome(String username, IncomeRequest incomeRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(user, incomeRequest.getCategoryName(), categoryType);
        AccountBook income = accountBookService.create(user, category, incomeRequest, accountBookType);

        if (income.getIterationType() != IterationType.DEFAULT) {
            createIterationIncome(user, incomeRequest, income);
        }

        return AccountBookCreateResponse.builder()
                .id(income.getId())
                .categoryId(category.getId())
                .build();
    }

    public void createIterationIncome(User user, IncomeRequest incomeRequest, AccountBook income) {
        List<AccountBook> iterationIncomes
                = iterationService.createIterations(user, incomeRequest.getCustomIteration(), income, accountBookType);
        List<AccountBook> savedIncomes = accountBookService.createAccountBookAll(iterationIncomes, accountBookType);
        iterationService.createIterationInfo(user, incomeRequest, income, savedIncomes, accountBookType);
    }

    @Transactional
    public InfoResponse getIncome(String username, Long id) {
        User user = userService.findUserByUsername(username);
        Iteration iterationIncomes = accountBookService.getIteration(user, id, accountBookType);

        CustomIterationInfoResponse customIteration = null;
        if (iterationIncomes != null) {
            customIteration = iterationService.getCustomIteration(iterationIncomes);
        }

        return accountBookService.getInfoResponse(user, id, customIteration, accountBookType);
    }

    @Transactional
    public AccountBookActionResponse deleteIncome(String username, Long incomeId, AccountBookDeleteRequest accountBookDeleteRequest) {
        User user = userService.findUserByUsername(username);
        AccountBook income = accountBookService.getAccountBookOrThrow(incomeId, user, accountBookType);
        Long categoryId = income.getCategory().getId();

        if (accountBookDeleteRequest.parseIterationAction() == IterationAction.NONE) {
            accountBookService.deleteAccountBook(incomeId, user, accountBookType);
        }

        if (accountBookDeleteRequest.parseIterationAction() != IterationAction.NONE) {
            AccountBook incomeToDelete = accountBookService.getAccountBookOrThrow(incomeId, user, accountBookType);
            accountBookService.deleteAccountBookAll(
                    iterationService.deleteIterations(
                            incomeToDelete,
                            user,
                            accountBookDeleteRequest.parseIterationAction(),
                            accountBookType),
                    accountBookType
            );
        }

        return AccountBookActionResponse.builder()
                .categoryId(categoryId)
                .build();
    }

    @Transactional
    public AccountBookActionResponse modifyIncome(String username, Long incomeId, IncomeRequest incomeRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(user, incomeRequest.getCategoryName(), categoryType);
        AccountBook income = accountBookService.modifyAccountBook(user, category, incomeId, incomeRequest, accountBookType);

        IterationAction iterationAction = incomeRequest.parseIterationAction();
        if (iterationAction == IterationAction.NONE) {
            modifySingleIncome(income, incomeRequest, user);
        }

        if (iterationAction != IterationAction.NONE) {
            modifyIterationIncomes(income, incomeRequest, iterationAction, category, user);
        }

        return AccountBookActionResponse.builder()
                .categoryId(category.getId())
                .build();
    }

    private void modifySingleIncome(AccountBook income, IncomeRequest incomeRequest, User user) {
        incomeService.modifyIncomeDate((Income) income, incomeRequest.parseDate());

        if (incomeRequest.getIsIterationModified()) {
            createIterationIncome(user, incomeRequest, income);
        }
    }

    private void modifyIterationIncomes(
            AccountBook income,
            IncomeRequest incomeRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        if (incomeRequest.getIsIterationModified()) {
            handleModifiedIteration(income, incomeRequest, iterationAction, category, user);
            return;
        }

        handleUnmodifiedIteration(income, incomeRequest, iterationAction, category, user);
    }

    private void handleModifiedIteration(
            AccountBook income,
            IncomeRequest incomeRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        accountBookService.deleteAccountBookAll(
                iterationService.deleteIterations(income, user, iterationAction, accountBookType),
                accountBookType
        );
        AccountBook newIncome = accountBookService.create(user, category, incomeRequest, accountBookType);
        createIterationIncome(user, incomeRequest, newIncome);
    }

    private void handleUnmodifiedIteration(
            AccountBook income,
            IncomeRequest incomeRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        LocalDate incomeDate = income.getAccountBookDate();
        LocalDate requestDate = incomeRequest.parseDate();

        if (iterationAction == IterationAction.THIS_ONLY && !incomeDate.equals(requestDate)) {
            incomeService.modifyIncomeDate((Income) income, requestDate);
            return;
        }

        modifyIterationGeneratedIncomes(income, incomeRequest, iterationAction, category, user);
    }

    private void modifyIterationGeneratedIncomes(
            AccountBook income,
            IncomeRequest incomeRequest,
            IterationAction iterationAction,
            Category category,
            User user
    ) {
        List<Iteration> modifyIterationIncomes
                = iterationService.getIterationByIterationAction(income, user, iterationAction, accountBookType);
        for (Iteration iteration : modifyIterationIncomes) {
            accountBookService.modifyAccountBook(iteration.getGeneratedAccountBook(), incomeRequest, category);
        }
    }

    public IterationDetailsResponse getIncomeIterationDetails(String username) {
        User user = userService.findUserByUsername(username);

        List<Long> originalIncomeIds = iterationService.getIterationAccountBookIds(user, accountBookType);
        return accountBookService.getIterationDetails(user, originalIncomeIds, accountBookType);
    }
}
