package com.poortorich.expense.controller;

import com.poortorich.expense.facade.ExpenseFacade;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseFacade expenseFacade;

    @PostMapping
    public ResponseEntity<BaseResponse> createExpense(@RequestBody @Valid ExpenseRequest expenseRequest, @AuthenticationPrincipal User user) {
        return BaseResponse.toResponseEntity(expenseFacade.createExpense(expenseRequest, user));
    }
}
