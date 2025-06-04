package com.poortorich.income.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.income.facade.IncomeFacade;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.enums.IncomeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return BaseResponse.toResponseEntity(incomeFacade.createIncome(userDetails.getUsername(), incomeRequest));
    }

    @GetMapping("/{incomeId}")
    public ResponseEntity<BaseResponse> getIncome(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long incomeId
    ) {
        return DataResponse.toResponseEntity(
                IncomeResponse.GET_INCOME_SUCCESS,
                incomeFacade.getIncome(userDetails.getUsername(), incomeId)
        );
    }
}
