package com.poortorich.expense.request;

import com.poortorich.expense.request.enums.IterationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpenseDeleteRequest {

    private String iterationAction;

    public IterationAction parseIterationAction() {
        return IterationAction.from(iterationAction);
    }
}
