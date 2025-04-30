package com.poortorich.category.constants;

public class CategoryResponseMessage {

    public static final String CREATE_CATEGORY_SUCCESS = "카테고리를 성공적으로 등록하였습니다.";
    public static final String MODIFY_CATEGORY_SUCCESS = "카테고리를 성공적으로 편집하였습니다.";
    public static final String DELETE_CATEGORY_SUCCESS = "카테고리를 성공적으로 삭제하였습니다.";

    public static final String CATEGORY_NON_EXISTENT = "존재하지 않는 카테고리입니다.";

    public static final String CATEGORY_TYPE_INVALID = "카테고리의 타입이 적절하지 않습니다.";

    public static final String CATEGORY_NAME_REQUIRED = "카테고리 이름은 필수값입니다.";
    public static final String CATEGORY_NAME_DUPLICATE = "이미 사용중인 카테고리 이름입니다.";

    public static final String CATEGORY_COLOR_REQUIRED = "카테고리 색상은 필수값입니다.";

    public static final String DEFAULT = "알 수 없는 에러 발생";

    private CategoryResponseMessage() {}
}
