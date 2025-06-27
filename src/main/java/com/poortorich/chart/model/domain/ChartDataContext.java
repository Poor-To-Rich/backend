package com.poortorich.chart.model.domain;

import com.poortorich.category.entity.Category;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChartDataContext {

    private User user;
    private Category category;
    private DateInfo dateInfo;
}
