package com.poortorich.chart.handler;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.model.domain.PaginationResult;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.page.domain.Pagination;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChartPaginationHandler {

    private final Pagination pageProvider;
    private final AccountBookService accountBookService;

    public PaginationResult handlePagination(
            User user, Category category, DateInfo dateInfo, List<AccountBook> accountBooks, Direction direction
    ) {
        LocalDate nextCursor = pageProvider.getNextCursor(accountBooks, direction);
        Boolean hasNext = accountBookService.hasNextPage(
                user, category, dateInfo.getStartDate(), dateInfo.getEndDate(), nextCursor, direction
        );

        return PaginationResult.builder()
                .hasNext(hasNext)
                .nextCursor(nextCursor.toString())
                .build();
    }
}
