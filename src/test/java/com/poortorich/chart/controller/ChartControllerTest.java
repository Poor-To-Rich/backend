package com.poortorich.chart.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poortorich.chart.constants.ChartResponseMessage;
import com.poortorich.chart.facade.ChartFacade;
import com.poortorich.chart.response.ChartResponse;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.global.date.fixture.DateTestFixture;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ChartController.class)
@Import(SecurityConfig.class)
public class ChartControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChartFacade chartFacade;

    @Test
    @DisplayName("총 지출 금액과 저축 조회 테스트 성공")
    void getTotalExpenseAmountAndSaving_whenValidRequest_ShouldReturnSuccess() throws Exception {
        TotalAmountAndSavingResponse mockResponse = TotalAmountAndSavingResponse.builder()
                .savingCategoryId(1L)
                .totalAmount(50000L)
                .totalSaving(10000L)
                .build();

        when(chartFacade.getTotalExpenseAmountAndSaving(anyString(), anyString()))
                .thenReturn(mockResponse);

        ResultActions actions = mockMvc.perform(get("/chart/expense/total")
                .param("date", DateTestFixture.TEAR_YEAR_MONTH_MAY)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(UserFixture.VALID_USERNAME_SAMPLE_1))
                .with(csrf()));

        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(
                        ChartResponse.GET_TOTAL_EXPENSE_AND_SAVINGS_SUCCESS.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(ChartResponseMessage.GET_TOTAL_EXPENSE_AND_SAVINGS_SUCCESS))
                .andExpect(jsonPath("$.data.totalAmount").value(mockResponse.getTotalAmount()))
                .andExpect(jsonPath("$.data.totalSaving").value(mockResponse.getTotalSaving()));

        verify(chartFacade, times(1)).getTotalExpenseAmountAndSaving(anyString(), anyString());
    }
}
