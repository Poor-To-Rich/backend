package com.poortorich.iteration.util.strategy;

import com.poortorich.accountbook.enums.AccountBookType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IterationStrategyFactory {

    private final IterationExpensesStrategy expensesStrategy;
    private final IterationIncomesStrategy incomesStrategy;

    public IterationStrategy getStrategy(AccountBookType type) {
        return switch (type) {
            case EXPENSE -> expensesStrategy;
            case INCOME -> incomesStrategy;
        };
    }
}
