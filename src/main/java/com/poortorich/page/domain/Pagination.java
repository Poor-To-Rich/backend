package com.poortorich.page.domain;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.domain.DateInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Pagination {

    public static final int CHART_PAGE_SIZE = 20;
    public static final int REPORT_PAGE_SIZE = 30;

    private final Pageable chartPageable = PageRequest.of(0, CHART_PAGE_SIZE);
    private final Pageable reportPageable = PageRequest.of(0, REPORT_PAGE_SIZE);

    public LocalDate getCursor(String cursor, DateInfo dateInfo, Direction direction) {
        if (Objects.isNull(cursor)) {
            return switch (direction) {
                case ASC -> dateInfo.getStartDate();
                case DESC -> dateInfo.getEndDate();
            };
        }
        return LocalDate.parse(cursor);
    }

    public LocalDate getNextCursor(List<AccountBook> accountBooks, Direction direction) {
        if (accountBooks.isEmpty()) {
            return LocalDate.now();
        }

        return switch (direction) {
            case ASC -> accountBooks.getLast().getAccountBookDate().plusDays(DateConstants.ONE_DAY);
            case DESC -> accountBooks.getFirst().getAccountBookDate().minusDays(DateConstants.ONE_DAY);
        };
    }
}
