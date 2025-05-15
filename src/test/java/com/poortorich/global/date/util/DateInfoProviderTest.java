package com.poortorich.global.date.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.fixture.DateTestFixture;
import java.time.Month;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateInfoProviderTest {

    @Test
    @DisplayName("해당 연도의 정보를 반환받을 때, 유효한 구조로 반환이 되는지 확인")
    void getYearInformation_whenGivenYear_shouldReturnValidStructure() {
        YearInformation result = DateInfoProvider.getYearInformation(DateTestFixture.TEST_YEAR);
        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(DateTestFixture.TEST_YEAR);
        assertThat(result.getStartDate()).isEqualTo(DateTestFixture.YEAR_START);
        assertThat(result.getEndDate()).isEqualTo(DateTestFixture.YEAR_END);

        Map<Month, MonthInformation> months = result.getMonths();
        assertThat(months).hasSize(12);
        assertThat(months.keySet()).containsExactlyInAnyOrder(Month.values());

        MonthInformation mayInfo = months.get(Month.MAY);
        assertThat(mayInfo.getYearMonth()).isEqualTo(DateTestFixture.MAY_2025);
        assertThat(mayInfo.getStartDate()).isEqualTo(DateTestFixture.MAY_START);
        assertThat(mayInfo.getEndDate()).isEqualTo(DateTestFixture.MAY_END);

        List<WeekInformation> weeksInMay = mayInfo.getWeeks();
        assertThat(weeksInMay).isNotEmpty();

        WeekInformation firstWeek = weeksInMay.getFirst();
        assertThat(firstWeek.getStartDate()).isEqualTo(DateTestFixture.MAY_2025_WEEK_FIRST_START);
        assertThat(firstWeek.getEndDate()).isEqualTo(DateTestFixture.MAY_2025_WEEK_FIRST_END);

        WeekInformation endWeek = weeksInMay.getLast();
        assertThat(endWeek.getStartDate()).isEqualTo(DateTestFixture.MAY_2025_WEEK_LAST_START);
        assertThat(endWeek.getEndDate()).isEqualTo(DateTestFixture.MAY_2025_WEEK_LAST_END);
    }
}
