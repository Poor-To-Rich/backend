package com.poortorich.income.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.income.facade.IncomeFacade;
import com.poortorich.income.request.IncomeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeFacade incomeFacade;

    @PostMapping
    public ResponseEntity<BaseResponse> createIncome(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid IncomeRequest incomeRequest) {
        return BaseResponse.toResponseEntity(incomeFacade.createIncome(incomeRequest, userDetails.getUsername()));
    }
}
