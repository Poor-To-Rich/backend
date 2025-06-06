package com.poortorich.report.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.report.facade.ReportFacade;
import com.poortorich.report.response.enums.ReportResponse;
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
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportFacade reportFacade;

    @GetMapping("/daily/details")
    public ResponseEntity<BaseResponse> getDailyDetailsReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ReportResponse.GET_DAILY_DETAILS_SUCCESS,
                reportFacade.getDailyDetailsReport(userDetails.getUsername(), date)
        );
    }

    @GetMapping("/weekly/details")
    public ResponseEntity<BaseResponse> getWeeklyDetailsReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date,
            @RequestParam("week") Long week,
            @RequestParam("cursor") @Nullable String cursor
    ) {
        return DataResponse.toResponseEntity(
                ReportResponse.GET_WEEKLY_DETAILS_SUCCESS,
                reportFacade.getWeeklyDetailsReport(userDetails.getUsername(), date, week, cursor)
        );
    }

    @GetMapping("/monthly/total")
    public ResponseEntity<BaseResponse> getMonthlyTotalReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") @Nullable String date
    ) {
        return DataResponse.toResponseEntity(
                ReportResponse.GET_MONTHLY_TOTAL_SUCCESS,
                reportFacade.getMonthlyTotalReport(userDetails.getUsername(), date)
        );
    }
}
