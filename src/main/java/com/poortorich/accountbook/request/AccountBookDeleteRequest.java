package com.poortorich.accountbook.request;

import com.poortorich.accountbook.request.enums.IterationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountBookDeleteRequest {

    private String iterationAction;

    public IterationAction parseIterationAction() {
        return IterationAction.from(iterationAction);
    }
}
