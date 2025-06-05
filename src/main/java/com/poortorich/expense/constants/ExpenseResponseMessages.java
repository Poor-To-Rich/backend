package com.poortorich.expense.constants;

public class ExpenseResponseMessages {

    public static final String CREATE_EXPENSE_SUCCESS = "지출 가계부를 성공적으로 등록하였습니다.";
    public static final String GET_EXPENSE_SUCCESS = "지출 가계부를 성공적으로 조회하였습니다.";
    public static final String MODIFY_EXPENSE_SUCCESS = "지출 가계부를 성공적으로 편집하였습니다.";
    public static final String DELETE_EXPENSE_SUCCESS = "지출 가계부를 성공적으로 삭제하였습니다.";

    public static final String GET_EXPENSE_ITERATION_DETAILS_SUCCESS = "지출 반복 데이터들을 성공적으로 조회하였습니다.";

    public static final String PAYMENT_METHOD_REQUIRED = "지출수단은 필수로 선택해야 합니다.";
    public static final String PAYMENT_METHOD_INVALID = "지출수단이 적절하지 않습니다.";

    public static final String EXPENSE_NON_EXISTENT = "존재하지 않는 지출입니다.";

    private ExpenseResponseMessages() {}
}
