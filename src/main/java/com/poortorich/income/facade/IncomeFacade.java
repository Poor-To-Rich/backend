package com.poortorich.income.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.response.InfoResponse;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.Response;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.enums.IncomeResponse;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
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
            createIterationIncome(user, incomeRequest, income);
        }

        return IncomeResponse.CREATE_INCOME_SUCCESS;
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
}
