package com.poortorich.chart.controller;

import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.chart.facade.ChartFacade;
import com.poortorich.chart.response.enums.ChartResponse;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.constants.DateResponseMessage;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
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
                chartFacade.getTotalAccountBookAmountAndSaving(userDetails.getUsername(), date, AccountBookType.EXPENSE)
        );
    }

    @GetMapping("/income/total")
    public ResponseEntity<BaseResponse> getTotalIncomeAmountAndSaving(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_TOTAL_INCOME_AND_SAVINGS_SUCCESS,
                chartFacade.getTotalAccountBookAmountAndSaving(userDetails.getUsername(), date, AccountBookType.INCOME)
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

    @GetMapping("/category/expense")
    public ResponseEntity<BaseResponse> getExpenseCategoryChart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_CATEGORY_CHART_SUCCESS,
                chartFacade.getCategoryChart(userDetails.getUsername(), date, AccountBookType.EXPENSE)
        );
    }

    @GetMapping("/category/income")
    public ResponseEntity<BaseResponse> getIncomeCategoryChart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_CATEGORY_CHART_SUCCESS,
                chartFacade.getCategoryChart(userDetails.getUsername(), date, AccountBookType.INCOME)
        );
    }

    @GetMapping("/expense/bar")
    public ResponseEntity<BaseResponse> getExpenseBar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_EXPENSE_BAR_SUCCESS,
                chartFacade.getAccountBookBar(userDetails.getUsername(), date, AccountBookType.EXPENSE)
        );
    }

    @GetMapping("/income/bar")
    public ResponseEntity<BaseResponse> getIncomeBar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_INCOME_BAR_SUCCESS,
                chartFacade.getAccountBookBar(userDetails.getUsername(), date, AccountBookType.INCOME)
        );
    }

    @GetMapping("/{categoryId}/line")
    public ResponseEntity<BaseResponse> getCategoryLine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("date")
            @Pattern(regexp = DatePattern.YEAR_MONTH_REGEX, message = DateResponseMessage.UNSUPPORTED_DATE_FORMAT)
            String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_CATEGORY_LINE_SUCCESS,
                chartFacade.getCategoryLine(userDetails.getUsername(), categoryId, date)
        );
    }

    @GetMapping("/{categoryId}/vertical")
    public ResponseEntity<BaseResponse> getCategoryVertical(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("date")
            @Pattern(regexp = DatePattern.YEAR_REGEX, message = DateResponseMessage.UNSUPPORTED_DATE_FORMAT)
            String date
    ) {
        return DataResponse.toResponseEntity(
                ChartResponse.GET_CATEGORY_VERTICAL_SUCCESS,
                chartFacade.getCategoryVertical(userDetails.getUsername(), categoryId, date)
        );
    }
}
