package com.poortorich.chart.aggregator;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.chart.constants.ChartConstants;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.util.AmountFormatter;
import com.poortorich.global.date.domain.DateInfo;
import io.jsonwebtoken.lang.Objects;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChartDataAggregator {

    public Map<String, BigDecimal> getAggregatedDataFromCategoryChart(List<CategoryChart> categoryCharts) {
        if (Objects.isEmpty(categoryCharts)) {
            return Map.of(ChartConstants.DUMMY_CATEGORY, ChartConstants.DUMMY_RATE);
        }

        return categoryCharts.stream()
                .collect(Collectors.toMap(
                        CategoryChart::getName,
                        categoryChart -> categoryChart.getRate().max(ChartConstants.AGGREGATED_DATE_MIN_RATE),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, String> getCategoryColorsFromCategoryChart(List<CategoryChart> categoryCharts) {
        if (Objects.isEmpty(categoryCharts)) {
            return Map.of(ChartConstants.DUMMY_CATEGORY, ChartConstants.DUMMY_COLOR);
        }

        return categoryCharts.stream()
                .collect(Collectors.toMap(
                        CategoryChart::getName,
                        CategoryChart::getColor,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public String calculateDifferenceAmount(Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo) {
        List<DateInfo> dateInfos = new ArrayList<>(accountBookGroupByDateInfo.keySet());

        Long current = AccountBookCalculator.sum(accountBookGroupByDateInfo.get(dateInfos.getLast()));
        Long previous = AccountBookCalculator.sum(accountBookGroupByDateInfo.get(dateInfos.get(dateInfos.size() - 2)));

        return AmountFormatter.compareAmount(current, previous);
    }

    public long calculateAverage(Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo) {
        return AccountBookCalculator.average(accountBookGroupByDateInfo.values()).longValue();
    }
}
