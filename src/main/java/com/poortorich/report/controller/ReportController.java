package com.poortorich.report.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.report.facade.ReportFacade;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.enums.ReportResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

    private final ReportFacade reportFacade;

    @PostMapping("/chatrooms/{chatroomId}/members/{userId}/reports")
    public ResponseEntity<BaseResponse> reportMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ReceiptReportRequest request
    ) {
        return DataResponse.toResponseEntity(
                ReportResponse.MEMBER_REPORT_SUCCESS,
                reportFacade.reportMember(userDetails.getUsername(), chatroomId, userId, request)
        );
    }
}
