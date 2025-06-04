package com.poortorich.iteration.repository;

import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.entity.IterationIncomes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IterationRepository {

    private final IterationExpensesRepository iterationExpensesRepository;
    private final IterationIncomesRepository iterationIncomesRepository;

    public void save(Iteration iteration, AccountBookType type) {
        switch (type) {
            case EXPENSE -> iterationExpensesRepository.save((IterationExpenses) iteration);
            case INCOME -> iterationIncomesRepository.save((IterationIncomes) iteration);
        }
    }

    public List<Long> originalAccountBookIds(AccountBookType type) {
        return switch (type) {
            case EXPENSE -> iterationExpensesRepository.getOriginalExpenseIds();
            case INCOME -> iterationIncomesRepository.getOriginalIncomeIds();
        };
    }
}
