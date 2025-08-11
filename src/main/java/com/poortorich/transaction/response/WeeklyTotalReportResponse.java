package com.poortorich.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyTotalReportResponse {

    private List<Logs> weeklyLogs;
}
