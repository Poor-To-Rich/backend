package com.poortorich.income.service;

import com.poortorich.category.entity.Category;
import com.poortorich.income.entity.Income;
import com.poortorich.income.repository.IncomeRepository;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public Income create(IncomeRequest incomeRequest, Category category, User user) {
        Income income = buildEntity(incomeRequest, category, user);
        incomeRepository.save(income);
        return income;
    }

    protected Income buildEntity(IncomeRequest incomeRequest, Category category, User user) {
        return Income.builder()
                .incomeDate(incomeRequest.parseDate())
                .category(category)
                .title(incomeRequest.trimTitle())
                .cost(incomeRequest.getCost())
                .memo(incomeRequest.getMemo())
                .iterationType(incomeRequest.parseIterationType())
                .user(user)
                .build();
    }
}
