package com.poortorich.income.service;

import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.income.entity.Income;
import com.poortorich.income.repository.IncomeRepository;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService extends AccountBookService<IncomeRequest, Income> {

    private final IncomeRepository incomeRepository;

    @Override
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

    @Override
    protected JpaRepository<Income, Long> getRepository() {
        return incomeRepository;
    }
}
