package com.poortorich.expense.constants;

public class ExpenseResponseMessages {

    public static final String DATE_REQUIRED = "날짜는 필수로 입력해야 합니다.";
    public static final String DATE_INVALID = "날짜 형식이 적절하지 않습니다.";

    public static final String CATEGORY_NAME_REQUIRED = "카테고리는 필수로 선택해야 합니다.";

    public static final String COST_REQUIRED = "금액은 필수로 입력해야 합니다.";
    public static final String COST_NEGATIVE = "금액은 0보다 커야합니다.";
    public static final String COST_TOO_BIG = "금액은 " + ExpenseValidationConstraints.COST_LIMIT + "보다 작아야 합니다.";

    public static final String TITLE_TOO_SHORT = "지출명은 1자 이상 작성해야 합니다.";
    public static final String TITLE_TOO_LONG = "지출명은 " + ExpenseValidationConstraints.TITLE_MAX_SIZE + "자 이하로 작성해야 합니다.";
    public static final String MEMO_TOO_LONG = "메모는 " + ExpenseValidationConstraints.MEMO_MAX_SIZE + "자 이하로 작성해야 합니다.";

    public static final String PAYMENT_METHOD_REQUIRED = "지출수단은 필수로 선택해야 합니다.";
    public static final String PAYMENT_METHOD_INVALID = "지출수단이 적절하지 않습니다.";

    public static final String ITERATION_TYPE_INVALID = "반복 데이터 유형이 적절하지 않습니다.";

    public static final String CREATE_EXPENSE_SUCCESS = "가계부를 성공적으로 등록하였습니다.";

    private ExpenseResponseMessages() {}
}
