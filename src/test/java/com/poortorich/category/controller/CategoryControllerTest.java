package com.poortorich.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.fixture.CategoryApiFixture;
import com.poortorich.category.fixture.CategoryFixture;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.request.CategoryVisibilityRequest;
import com.poortorich.category.response.ActiveCategoriesResponse;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.response.enums.CategoryResponse;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("기본 지출 카테고리 목록 조회 성공")
    void getExpenseDefaultCategoriesSuccess() throws Exception {
        when(categoryService.getDefaultCategories(CategoryType.DEFAULT_EXPENSE, UserFixture.VALID_USERNAME_SAMPLE_1))
                .thenReturn(List.of(DefaultCategoryResponse.builder().build()));

        mockMvc.perform(get(CategoryApiFixture.GET_EXPENSE_DEFAULT_CATEGORIES_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("기본 수입 카테고리 목록 조회 성공")
    void getIncomeDefaultCategoriesSuccess() throws Exception {
        when(categoryService.getDefaultCategories(CategoryType.DEFAULT_INCOME, UserFixture.VALID_USERNAME_SAMPLE_1))
                .thenReturn(List.of(DefaultCategoryResponse.builder().build()));

        mockMvc.perform(get(CategoryApiFixture.GET_INCOME_DEFAULT_CATEGORIES_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_DEFAULT_INCOME_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 지출 카테고리 목록 조회 성공")
    void getExpenseCustomCategoriesSuccess() throws Exception {
        when(categoryService.getDefaultCategories(CategoryType.CUSTOM_EXPENSE, UserFixture.VALID_USERNAME_SAMPLE_1))
                .thenReturn(List.of(DefaultCategoryResponse.builder().build()));

        mockMvc.perform(get(CategoryApiFixture.GET_EXPENSE_CUSTOM_CATEGORIES_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 수입 카테고리 목록 조회 성공")
    void getIncomeCustomCategoriesSuccess() throws Exception {
        when(categoryService.getDefaultCategories(CategoryType.CUSTOM_INCOME, UserFixture.VALID_USERNAME_SAMPLE_1))
                .thenReturn(List.of(DefaultCategoryResponse.builder().build()));

        mockMvc.perform(get(CategoryApiFixture.GET_INCOME_CUSTOM_CATEGORIES_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_CUSTOM_INCOME_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("활성화된 기본 지출 카테고리 목록 조회 성공")
    void getActiveExpenseCategoriesSuccess() throws Exception {
        when(categoryService.getActiveCategories(
                CategoryFixture.CATEGORY_TYPE_EXPENSE,
                UserFixture.VALID_USERNAME_SAMPLE_1)
        ).thenReturn(ActiveCategoriesResponse.builder().build());

        mockMvc.perform(get(CategoryApiFixture.GET_ACTIVE_CATEGORIES_PATH)
                        .param("type", CategoryFixture.CATEGORY_TYPE_EXPENSE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_ACTIVE_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("활성화된 기본 수입 카테고리 목록 조회 성공")
    void getActiveIncomeCategoriesSuccess() throws Exception {
        when(categoryService.getActiveCategories(
                CategoryFixture.CATEGORY_TYPE_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1)
        ).thenReturn(ActiveCategoriesResponse.builder().build());

        mockMvc.perform(get(CategoryApiFixture.GET_ACTIVE_CATEGORIES_PATH)
                        .param("type", CategoryFixture.CATEGORY_TYPE_INCOME)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_ACTIVE_CATEGORIES_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("카테고리 활성화 상태 변경 성공")
    void updateActiveCategorySuccess() throws Exception {
        CategoryVisibilityRequest request = new CategoryVisibilityRequest(true);
        String requestJson = objectMapper.writeValueAsString(request);

        when(categoryService.updateActiveCategory(
                eq(CategoryFixture.VALID_CATEGORY_ID_1),
                any(CategoryVisibilityRequest.class),
                eq(UserFixture.VALID_USERNAME_SAMPLE_1))
        ).thenReturn(CategoryResponse.CATEGORY_VISIBILITY_TRUE_SUCCESS);

        mockMvc.perform(put(CategoryApiFixture.UPDATE_ACTIVE_CATEGORY_PATH + CategoryFixture.VALID_CATEGORY_ID_1)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.CATEGORY_VISIBILITY_TRUE_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 지출 카테고리 생성 성공")
    void createExpenseCategorySuccess() throws Exception {
        CategoryInfoRequest request
                = new CategoryInfoRequest(CategoryFixture.VALID_CATEGORY_NAME_1, CategoryFixture.VALID_CATEGORY_COLOR);
        String requestJson = objectMapper.writeValueAsString(request);

        when(categoryService.createCategory(
                any(CategoryInfoRequest.class),
                eq(CategoryType.CUSTOM_EXPENSE),
                eq(UserFixture.VALID_USERNAME_SAMPLE_1))
        ).thenReturn(CategoryResponse.CREATE_CATEGORY_SUCCESS);

        mockMvc.perform(post(CategoryApiFixture.CREATE_EXPENSE_PATH)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.CREATE_CATEGORY_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 수입 카테고리 생성 성공")
    void createIncomeCategorySuccess() throws Exception {
        CategoryInfoRequest request
                = new CategoryInfoRequest(CategoryFixture.VALID_CATEGORY_NAME_1, CategoryFixture.VALID_CATEGORY_COLOR);
        String requestJson = objectMapper.writeValueAsString(request);

        when(categoryService.createCategory(
                any(CategoryInfoRequest.class),
                eq(CategoryType.CUSTOM_INCOME),
                eq(UserFixture.VALID_USERNAME_SAMPLE_1))
        ).thenReturn(CategoryResponse.CREATE_CATEGORY_SUCCESS);

        mockMvc.perform(post(CategoryApiFixture.CREATE_INCOME_PATH)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.CREATE_CATEGORY_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 카테고리 조회 성공")
    void getCategorySuccess() throws Exception {
        when(categoryService.getCategory(CategoryFixture.VALID_CATEGORY_ID_1, UserFixture.VALID_USERNAME_SAMPLE_1))
                .thenReturn(CategoryInfoResponse.builder().build());

        mockMvc.perform(get(CategoryApiFixture.GET_CATEGORY_PATH + CategoryFixture.VALID_CATEGORY_ID_1)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.GET_CUSTOM_CATEGORY_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 카테고리 수정 성공")
    void modifyCategorySuccess() throws Exception {
        CategoryInfoRequest request
                = new CategoryInfoRequest(CategoryFixture.VALID_CATEGORY_NAME_1, CategoryFixture.VALID_CATEGORY_COLOR);
        String requestJson = objectMapper.writeValueAsString(request);

        when(categoryService.modifyCategory(
                eq(CategoryFixture.VALID_CATEGORY_ID_1),
                any(CategoryInfoRequest.class),
                eq(UserFixture.VALID_USERNAME_SAMPLE_1))
        ).thenReturn(CategoryResponse.MODIFY_CATEGORY_SUCCESS);

        mockMvc.perform(put(CategoryApiFixture.MODIFY_CATEGORY_PATH + CategoryFixture.VALID_CATEGORY_ID_1)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.MODIFY_CATEGORY_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    @DisplayName("사용자 지정 카테고리 삭제 성공")
    void deleteCategorySuccess() throws Exception {
        when(categoryService.deleteCategory(
                eq(CategoryFixture.VALID_CATEGORY_ID_1),
                eq(UserFixture.VALID_USERNAME_SAMPLE_1))
        ).thenReturn(CategoryResponse.DELETE_CATEGORY_SUCCESS);

        mockMvc.perform(delete(CategoryApiFixture.DELETE_CATEGORY_PATH + CategoryFixture.VALID_CATEGORY_ID_1)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(CategoryResponse.DELETE_CATEGORY_SUCCESS.getMessage())
                );
    }
}