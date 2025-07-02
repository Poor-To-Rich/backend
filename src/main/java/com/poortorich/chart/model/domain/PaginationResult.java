package com.poortorich.chart.model.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaginationResult {

    private Boolean hasNext;
    private String nextCursor;
}
