package com.poortorich.income.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.Response;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.enums.IncomeResponse;
import com.poortorich.iteration.service.IterationService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeFacade {

    private static final AccountBookType accountBookType = AccountBookType.INCOME;

    private final UserService userService;
    private final CategoryService categoryService;
    private final AccountBookService accountBookService;
    private final IterationService iterationService;

    @Transactional
    public Response createIncome(String username, IncomeRequest incomeRequest) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(incomeRequest.getCategoryName(), user);
        AccountBook income = accountBookService.create(user, category, incomeRequest, accountBookType);

        if (income.getIterationType() != IterationType.DEFAULT) {
            createIteration(user, incomeRequest, income);
        }

        return IncomeResponse.CREATE_INCOME_SUCCESS;
    }

    public void createIteration(User user, IncomeRequest incomeRequest, AccountBook income) {
        List<AccountBook> iterationExpenses
                = iterationService.createIterations(user, incomeRequest.getCustomIteration(), income, accountBookType);
        List<AccountBook> savedExpenses = accountBookService.createAccountBookAll(iterationExpenses, accountBookType);
        iterationService.createIterationInfo(user, incomeRequest, income, savedExpenses, accountBookType);
    }
}
