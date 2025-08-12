package com.poortorich.transaction.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.transaction.facade.TransactionFacade;
import com.poortorich.transaction.response.enums.TransactionResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionFacade transactionFacade;

    @GetMapping("/daily/details")
    public ResponseEntity<BaseResponse> getDailyDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                TransactionResponse.GET_DAILY_DETAILS_SUCCESS,
                transactionFacade.getDailyDetails(userDetails.getUsername(), date)
        );
    }

    @GetMapping("/weekly/details")
    public ResponseEntity<BaseResponse> getWeeklyDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date,
            @RequestParam("week") Long week,
            @RequestParam("cursor") @Nullable String cursor
    ) {
        return DataResponse.toResponseEntity(
                TransactionResponse.GET_WEEKLY_DETAILS_SUCCESS,
                transactionFacade.getWeeklyDetails(userDetails.getUsername(), date, week, cursor)
        );
    }

    @GetMapping("/monthly/total")
    public ResponseEntity<BaseResponse> getMonthlyTotal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                TransactionResponse.GET_MONTHLY_TOTAL_SUCCESS,
                transactionFacade.getMonthlyTotal(userDetails.getUsername(), date)
        );
    }

    @GetMapping("/yearly/total")
    public ResponseEntity<BaseResponse> getYearlyTotal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                TransactionResponse.GET_YEARLY_TOTAL_SUCCESS,
                transactionFacade.getYearlyTotal(userDetails.getUsername(), date)
        );
    }

    @GetMapping("/weekly/total")
    public ResponseEntity<BaseResponse> getWeeklyTotal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                TransactionResponse.GET_WEEKLY_TOTAL_SUCCESS,
                transactionFacade.getWeeklyTotal(userDetails.getUsername(), date)
        );
    }
}
