package com.poortorich.category.constants;

public class CategoryResponseMessage {

    public static final String GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS = "기본 지출 카테고리 목록을 성공적으로 조회했습니다.";
    public static final String GET_DEFAULT_INCOME_CATEGORIES_SUCCESS = "기본 수입 카테고리 목록을 성공적으로 조회했습니다.";
    public static final String GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS = "사용자화 지출 카테고리 목록을 성공적으로 조회했습니다.";
    public static final String GET_CUSTOM_INCOME_CATEGORIES_SUCCESS = "사용자화 수입 카테고리 목록을 성공적으로 조회했습니다.";
    public static final String GET_CUSTOM_CATEGORY_SUCCESS = "사용자화 카테고리를 성공적으로 조회했습니다.";
    public static final String GET_ACTIVE_CATEGORIES_SUCCESS = "활성화된 카테고리 목록을 성공적으로 조회했습니다.";

    public static final String CATEGORY_VISIBILITY_TRUE_SUCCESS = "카테고리를 성공적으로 활성화하였습니다.";
    public static final String CATEGORY_VISIBILITY_FALSE_SUCCESS = "카테고리를 성공적으로 비활성화하였습니다.";

    public static final String CATEGORY_VISIBILITY_REQUIRED = "카테고리 활성화/비활성화 여부는 필수값입니다.";

    public static final String CREATE_CATEGORY_SUCCESS = "카테고리를 성공적으로 등록하였습니다.";
    public static final String MODIFY_CATEGORY_SUCCESS = "카테고리를 성공적으로 편집하였습니다.";
    public static final String DELETE_CATEGORY_SUCCESS = "카테고리를 성공적으로 삭제하였습니다.";

    public static final String NOT_DELETE_USED_CATEGORY = "사용중인 카테고리는 삭제할 수 없습니다.";

    public static final String CATEGORY_NON_EXISTENT = "존재하지 않는 카테고리입니다.";

    public static final String CATEGORY_TYPE_INVALID = "카테고리의 타입이 적절하지 않습니다.";

    public static final String CATEGORY_NAME_REQUIRED = "카테고리 이름은 필수값입니다.";
    public static final String CATEGORY_NAME_DUPLICATE = "이미 사용중인 카테고리 이름입니다.";

    public static final String CATEGORY_COLOR_REQUIRED = "카테고리 색상은 필수값입니다.";

    public static final String DEFAULT = "알 수 없는 에러 발생";

    private CategoryResponseMessage() {}
}
