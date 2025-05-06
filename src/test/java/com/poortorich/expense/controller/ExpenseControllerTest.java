package com.poortorich.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poortorich.expense.facade.ExpenseFacade;
import com.poortorich.expense.fixture.ExpenseApiFixture;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(ExpenseController.class)
@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @MockitoBean
    private ExpenseFacade expenseFacade;

    private ExpenseRequest expenseRequest;
    private ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequestTestBuilder().build();
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        user = User.builder()
                .id(1L)
                .username("test")
                .password("Asdf1234!")
                .build();
    }

    @Test
    @DisplayName("유효한 지출 가계부 데이터로 createExpense 호출 시 기대했던 응답이 반환된다.")
    void createExpense_whenValidInput_thenNoException() throws Exception {
        String requestJson = objectMapper.writeValueAsString(expenseRequest);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        when(expenseFacade.createExpense(any(ExpenseRequest.class), any(User.class)))
                .thenReturn(ExpenseResponse.CREATE_EXPENSE_SUCCESS);

        mockMvc.perform(post(ExpenseApiFixture.CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(authentication(authentication))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultMessage").value(ExpenseResponse.CREATE_EXPENSE_SUCCESS.getMessage()));

        verify(expenseFacade, times(1)).createExpense(any(ExpenseRequest.class), any(User.class));
    }
}