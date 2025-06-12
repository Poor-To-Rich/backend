package com.poortorich.expense.controller;

import com.poortorich.expense.facade.ExpenseFacade;
import com.poortorich.accountbook.request.AccountBookDeleteRequest;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseFacade expenseFacade;

    @PostMapping
    public ResponseEntity<BaseResponse> createExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ExpenseRequest expenseRequest) {
        return DataResponse.toResponseEntity(
                ExpenseResponse.CREATE_EXPENSE_SUCCESS,
                expenseFacade.createExpense(expenseRequest, userDetails.getUsername())
        );
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<BaseResponse> getExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long expenseId) {
        return DataResponse.toResponseEntity(
                ExpenseResponse.GET_EXPENSE_SUCCESS,
                expenseFacade.getExpense(expenseId, userDetails.getUsername())
        );
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<BaseResponse> modifyExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long expenseId,
            @RequestBody @Valid ExpenseRequest expenseRequest) {
        return BaseResponse.toResponseEntity(expenseFacade.modifyExpense(userDetails.getUsername(), expenseId, expenseRequest));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<BaseResponse> deleteExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long expenseId,
            @RequestBody @Valid AccountBookDeleteRequest accountBookDeleteRequest) {
        return BaseResponse.toResponseEntity(expenseFacade.deleteExpense(expenseId, accountBookDeleteRequest, userDetails.getUsername()));
    }

    @GetMapping("/iteration/details")
    public ResponseEntity<BaseResponse> getExpenseIterationDetails(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return DataResponse.toResponseEntity(
                ExpenseResponse.GET_EXPENSE_ITERATION_DETAILS_SUCCESS,
                expenseFacade.getExpenseIterationDetails(userDetails.getUsername())
        );
    }
}
