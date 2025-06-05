package com.poortorich.report.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.report.response.DailyDetailsResponse;
import com.poortorich.report.service.ReportService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final UserService userService;
    private final ReportService reportService;
    private final AccountBookService accountBookService;

    public DailyDetailsResponse getDailyDetailsReport(String username, String inputDate) {
        User user = userService.findUserByUsername(username);
        LocalDate date = DateParser.parseDate(inputDate);

        List<AccountBook> dailyIncomes
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.INCOME);
        List<AccountBook> dailyExpenses
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.EXPENSE);

        return reportService.getDailyDetailsReport(dailyIncomes, dailyExpenses);
    }
}
