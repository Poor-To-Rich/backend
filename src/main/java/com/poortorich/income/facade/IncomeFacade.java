package com.poortorich.income.facade;

import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.Response;
import com.poortorich.income.entity.Income;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.enums.IncomeResponse;
import com.poortorich.income.service.IncomeService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IncomeFacade {

    private final UserService userService;
    private final CategoryService categoryService;
    private final IncomeService incomeService;

    @Transactional
    public Response createIncome(IncomeRequest incomeRequest, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.findCategoryByName(incomeRequest.getCategoryName(), user);
        Income income = incomeService.create(incomeRequest, category, user);

        return IncomeResponse.CREATE_INCOME_SUCCESS;
    }
}
