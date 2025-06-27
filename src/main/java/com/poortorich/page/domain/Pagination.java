package com.poortorich.page.domain;

import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PaginationProvider {

    public static final int CHART_PAGE_SIZE = 20;
    public static final int REPORT_PAGE_SIZE = 30;

    private final Pageable chartPageable = PageRequest.of(0, CHART_PAGE_SIZE);
    private final Pageable reportPageable = PageRequest.of(0, REPORT_PAGE_SIZE);

    public LocalDate getCurosr(String cursor, Direction direction) {
        if (Objects.isNull(cursor)) {

        }
    }

}
