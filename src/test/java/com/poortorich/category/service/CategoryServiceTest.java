package com.poortorich.category.service;

import com.poortorich.category.constants.CategoryValidationConstraints;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.fixture.CategoryFixture;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.request.CategoryVisibilityRequest;
import com.poortorich.category.response.ActiveCategoriesResponse;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.response.enums.CategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(UserFixture.VALID_USERNAME_SAMPLE_1)
                .build();
    }

    @Test
    @DisplayName("기본 지출 카테고리 목록 조회 성공")
    void getDefaultExpenseCategoriesSuccess() {
        Category category1 = CategoryFixture.createDefaultExpenseCategory1(user);
        Category category2 = CategoryFixture.createDefaultExpenseCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeAndIsDeletedFalse(user, CategoryType.DEFAULT_EXPENSE))
                .thenReturn(List.of(category1, category2));

        List<DefaultCategoryResponse> responses = categoryService.getDefaultCategories(
                CategoryType.DEFAULT_EXPENSE,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeAndIsDeletedFalse(user, CategoryType.DEFAULT_EXPENSE);
        assertThat(responses.get(0).getName()).isEqualTo(category1.getName());
        assertThat(responses.get(1).getName()).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("기본 수입 카테고리 목록 조회 성공")
    void getDefaultIncomeCategoriesSuccess() {
        Category category1 = CategoryFixture.createDefaultIncomeCategory1(user);
        Category category2 = CategoryFixture.createDefaultIncomeCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeAndIsDeletedFalse(user, CategoryType.DEFAULT_INCOME))
                .thenReturn(List.of(category1, category2));

        List<DefaultCategoryResponse> responses = categoryService.getDefaultCategories(
                CategoryType.DEFAULT_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeAndIsDeletedFalse(user, CategoryType.DEFAULT_INCOME);
        assertThat(responses.get(0).getName()).isEqualTo(category1.getName());
        assertThat(responses.get(1).getName()).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("사용자 지정 지출 카테고리 목록 조회 성공")
    void getCustomExpenseCategoriesSuccess() {
        Category category1 = CategoryFixture.createCustomExpenseCategory1(user);
        Category category2 = CategoryFixture.createCustomExpenseCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_EXPENSE))
                .thenReturn(List.of(category1, category2));

        List<CustomCategoryResponse> responses = categoryService.getCustomCategories(
                CategoryType.CUSTOM_EXPENSE,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_EXPENSE);
        assertThat(responses.get(0).getName()).isEqualTo(category1.getName());
        assertThat(responses.get(1).getName()).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("사용자 지정 수입 카테고리 목록 조회 성공")
    void getCustomIncomeCategoriesSuccess() {
        Category category1 = CategoryFixture.createCustomIncomeCategory1(user);
        Category category2 = CategoryFixture.createCustomIncomeCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_INCOME))
                .thenReturn(List.of(category1, category2));

        List<CustomCategoryResponse> responses = categoryService.getCustomCategories(
                CategoryType.CUSTOM_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_INCOME);
        assertThat(responses.get(0).getName()).isEqualTo(category1.getName());
        assertThat(responses.get(1).getName()).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("사용자 지정 카테고리에 조회할 카테고리가 없는 경우 빈 리스트 반환")
    void getEmptyCustomCategories() {
        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_INCOME))
                .thenReturn(List.of());

        List<CustomCategoryResponse> responses = categoryService.getCustomCategories(
                CategoryType.CUSTOM_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeAndIsDeletedFalse(user, CategoryType.CUSTOM_INCOME);
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("활성화된 지출 카테고리 목록 조회 성공")
    void getActiveExpenseCategoriesSuccess() {
        Category category1 = CategoryFixture.createDefaultExpenseCategory1(user);
        Category category2 = CategoryFixture.createDefaultExpenseCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.CUSTOM_EXPENSE.getSameGroupTypes())
        ).thenReturn(List.of(category1, category2));

        ActiveCategoriesResponse response = categoryService.getActiveCategories(
                CategoryFixture.CATEGORY_TYPE_EXPENSE,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.DEFAULT_EXPENSE.getSameGroupTypes()
        );
        assertThat(response).isNotNull();
        assertThat(response.getActiveCategories().get(0)).isEqualTo(category1.getName());
        assertThat(response.getActiveCategories().get(1)).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("활성화된 수입 카테고리 목록 조회 성공")
    void getActiveIncomeCategoriesSuccess() {
        Category category1 = CategoryFixture.createDefaultIncomeCategory1(user);
        Category category2 = CategoryFixture.createDefaultIncomeCategory2(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.DEFAULT_INCOME.getSameGroupTypes())
        ).thenReturn(List.of(category1, category2));

        ActiveCategoriesResponse response = categoryService.getActiveCategories(
                CategoryFixture.CATEGORY_TYPE_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.CUSTOM_INCOME.getSameGroupTypes()
        );
        assertThat(response).isNotNull();
        assertThat(response.getActiveCategories().get(0)).isEqualTo(category1.getName());
        assertThat(response.getActiveCategories().get(1)).isEqualTo(category2.getName());
    }

    @Test
    @DisplayName("활성화된 카테고리에 조회할 카테고리가 없는 경우 빈 리스트 반환")
    void getEmptyActiveCategories() {
        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.DEFAULT_INCOME.getSameGroupTypes())
        ).thenReturn(List.of());

        ActiveCategoriesResponse response = categoryService.getActiveCategories(
                CategoryFixture.CATEGORY_TYPE_INCOME,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByUserAndTypeInAndIsDeletedFalse(
                user,
                CategoryType.CUSTOM_INCOME.getSameGroupTypes()
        );
        assertThat(response).isNotNull();
        assertThat(response.getActiveCategories()).isEmpty();
    }

    @Test
    @DisplayName("카테고리 활성화 상태 변경 성공")
    void updateActiveCategorySuccess() {
        CategoryVisibilityRequest request = new CategoryVisibilityRequest(CategoryFixture.CATEGORY_ACTIVE);
        Category category = CategoryFixture.createDefaultCategory(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.of(category));

        Response response = categoryService.updateActiveCategory(
                CategoryFixture.VALID_CATEGORY_ID_1,
                request,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(CategoryResponse.CATEGORY_VISIBILITY_TRUE_SUCCESS);
    }

    @Test
    @DisplayName("카테고리 비활성화 상태로 변경 성공")
    void updateInactiveCategorySuccess() {
        CategoryVisibilityRequest request = new CategoryVisibilityRequest(CategoryFixture.CATEGORY_INACTIVE);
        Category category = CategoryFixture.createInactiveCategory(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.of(category));

        Response response = categoryService.updateActiveCategory(
                CategoryFixture.VALID_CATEGORY_ID_1,
                request,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(CategoryResponse.CATEGORY_VISIBILITY_FALSE_SUCCESS);
    }

    @Test
    @DisplayName("카테고리 활성화 상태 변경 시 카테고리가 존재하지 않으면 예외 발생")
    void updateActiveCategoryNotFoundException() {
        CategoryVisibilityRequest request = new CategoryVisibilityRequest(CategoryFixture.CATEGORY_ACTIVE);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.updateActiveCategory(
                        CategoryFixture.VALID_CATEGORY_ID_1,
                        request,
                        UserFixture.VALID_USERNAME_SAMPLE_1
                )).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NON_EXISTENT.getMessage());
    }

    @Test
    @DisplayName("사용자 지정 카테고리 생성 성공")
    void createCategorySuccess() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        categoryService.createCategory(request, CategoryType.CUSTOM_EXPENSE, UserFixture.VALID_USERNAME_SAMPLE_1);

        verify(categoryRepository).save(categoryCaptor.capture());
        Category savedCategory = categoryCaptor.getValue();

        assertThat(savedCategory.getName()).isEqualTo(request.getName());
        assertThat(savedCategory.getColor()).isEqualTo(request.getColor());
    }

    @Test
    @DisplayName("중복된 이름의 카테고리가 존재하는 경우 예외 발생")
    void createCategoryNameDuplicationException() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );
        CategoryType type = CategoryType.CUSTOM_EXPENSE;

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndNameAndTypeInAndIsDeletedFalse(
                user,
                request.getName(),
                type.getSameGroupTypes())
        ).thenReturn(Optional.of(CategoryFixture.createCustomExpenseCategory1(user)));

        assertThatThrownBy(() -> categoryService.createCategory(request, type, UserFixture.VALID_USERNAME_SAMPLE_1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("최대 개수 이상의 카테고리를 생성하려는 경우 예외 발생")
    void createCategoryCountLimitException() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );
        CategoryType type = CategoryType.CUSTOM_EXPENSE;

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUserAndNameAndTypeInAndIsDeletedFalse(
                user,
                request.getName(),
                type.getSameGroupTypes())
        ).thenReturn(Optional.empty());

        when(categoryRepository.countByUserAndTypeAndIsDeletedFalse(user, type))
                .thenReturn(CategoryValidationConstraints.CATEGORY_LIMIT_COUNT);

        assertThatThrownBy(() -> categoryService.createCategory(request, type, UserFixture.VALID_USERNAME_SAMPLE_1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_COUNT_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("카테고리 조회 성공")
    void getCategorySuccess() {
        Category category = CategoryFixture.createCustomExpenseCategory1(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(category.getId(), user))
                .thenReturn(Optional.of(category));

        CategoryInfoResponse response = categoryService.getCategory(
                category.getId(),
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByIdAndUserAndIsDeletedFalse(category.getId(), user);
        assertThat(response.getName()).isEqualTo(category.getName());
        assertThat(response.getColor()).isEqualTo(category.getColor());
    }

    @Test
    @DisplayName("카테고리 조회 시 존재하지 않으면 예외 발생")
    void getCategoryNotFoundException() {
        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.getCategory(
                        CategoryFixture.VALID_CATEGORY_ID_1,
                        UserFixture.VALID_USERNAME_SAMPLE_1
                )).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NON_EXISTENT.getMessage());
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void modifyCategorySuccess() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );
        Category category = CategoryFixture.createCustomExpenseCategory1(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.of(category));

        Response response = categoryService.modifyCategory(
                CategoryFixture.VALID_CATEGORY_ID_1,
                request,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user);
        assertThat(response).isEqualTo(CategoryResponse.MODIFY_CATEGORY_SUCCESS);
    }

    @Test
    @DisplayName("카테고리가 존재하지 않으면 예외 발생")
    void modifyCategoryNotFoundException() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.modifyCategory(
                        CategoryFixture.VALID_CATEGORY_ID_1,
                        request,
                        UserFixture.VALID_USERNAME_SAMPLE_1
                )).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NON_EXISTENT.getMessage());
    }

    @Test
    @DisplayName("카테고리 수정 시 이름이 중복되면 예외 발생")
    void modifyCategoryNameDuplicationException() {
        CategoryInfoRequest request = new CategoryInfoRequest(
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryFixture.VALID_CATEGORY_COLOR
        );
        Category category = CategoryFixture.createCustomExpenseCategory1(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.of(category));

        when(categoryRepository.findByNameExcludingId(
                user,
                request.getName(),
                category.getType().getSameGroupTypes(),
                CategoryFixture.VALID_CATEGORY_ID_1)
        ).thenReturn(Optional.of(category));

        assertThatThrownBy(() ->
                categoryService.modifyCategory(
                        CategoryFixture.VALID_CATEGORY_ID_1,
                        request,
                        UserFixture.VALID_USERNAME_SAMPLE_1
                )).isInstanceOf(BadRequestException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategorySuccess() {
        Category category = CategoryFixture.createDeletedCategory(user);

        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.of(category));

        Response response = categoryService.deleteCategory(
                CategoryFixture.VALID_CATEGORY_ID_1,
                UserFixture.VALID_USERNAME_SAMPLE_1
        );

        verify(userRepository).findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
        verify(categoryRepository).findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user);
        assertThat(response).isEqualTo(CategoryResponse.DELETE_CATEGORY_SUCCESS);
        assertThat(category.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("카테고리 삭제 시 존재하지 않으면 예외 발생")
    void deleteCategoryNotFoundException() {
        when(userRepository.findByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserAndIsDeletedFalse(CategoryFixture.VALID_CATEGORY_ID_1, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.deleteCategory(
                        CategoryFixture.VALID_CATEGORY_ID_1,
                        UserFixture.VALID_USERNAME_SAMPLE_1
                )).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CategoryResponse.CATEGORY_NON_EXISTENT.getMessage());
    }

    @Test
    @DisplayName("삭제되지 않은 카테고리 이름으로 조회 성공")
    void findCategoryByNameNotDeletedSuccess() {
        Category category = CategoryFixture.createDefaultCategory(user);

        when(categoryRepository.findByUserAndNameAndTypeInAndIsDeletedFalse(
                user,
                category.getName(),
                category.getType().getSameGroupTypes())
        ).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryByNameNotDeleted(
                user,
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryType.CUSTOM_EXPENSE
        );

        assertThat(result).isEqualTo(category);
    }

    @Test
    @DisplayName("카테고리 이름과 카테고리 타입으로 카테고리 조회 성공")
    void findCategoryByNameAndTypeInSuccess() {
        Category category = CategoryFixture.createDefaultCategory(user);

        when(categoryRepository.findByUserAndNameAndTypeIn(
                user,
                category.getName(),
                category.getType().getSameGroupTypes())
        ).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryByName(
                user,
                CategoryFixture.VALID_CATEGORY_NAME_1,
                CategoryType.CUSTOM_EXPENSE
        );

        assertThat(result).isEqualTo(category);
    }

    @Test
    @DisplayName("카테고리 이름으로 카테고리 조회 성공")
    void findCategoryByNameSuccess() {
        Category category = CategoryFixture.createDefaultCategory(user);

        when(categoryRepository.findByNameAndUser(category.getName(), user)).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryByName(CategoryFixture.VALID_CATEGORY_NAME_1, user);

        assertThat(result).isEqualTo(category);
    }
}