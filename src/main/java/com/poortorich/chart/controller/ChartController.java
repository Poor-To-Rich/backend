package com.poortorich.chart.controller;

import com.poortorich.chart.facade.ChartFacade;
import com.poortorich.chart.response.enums.ChartResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chart")
public class ChartController {

    private final ChartFacade chartFacade;

    @GetMapping("/expense/total")
    public ResponseEntity<BaseResponse> getTotalExpenseAmountAndSaving(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_TOTAL_EXPENSE_AND_SAVINGS_SUCCESS,
                chartFacade.getTotalExpenseAmountAndSaving(userDetails.getUsername(), date)
        );
    }

    @GetMapping("/{categoryId}/section")
    public ResponseEntity<BaseResponse> getCategorySection(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("date") @Nullable String date,
            @RequestParam("cursor") @Nullable String cursor
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_CATEGORY_SECTION_SUCCESS,
                chartFacade.getCategorySection(userDetails.getUsername(), categoryId, date, cursor)
        );
    }
}
