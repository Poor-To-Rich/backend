package com.poortorich.iteration.constants;

public class IterationResponseMessages {

    public static final String ITERATION_RULE_REQUIRED = "반복 데이터 정보는 필수로 선택해야 합니다.";
    public static final String ITERATION_RULE_TYPE_REQUIRED = "반복 데이터 타입은 필수로 선택해야 합니다.";
    public static final String ITERATION_RULE_TYPE_INVALID = "반복 타입이 적절하지 않습니다.";
    public static final String ITERATIONS_TOO_LONG = "반복 횟수가 허용 범위를 초과했습니다.";

    public static final String DAY_OF_WEEK_INVALID = "날짜가 적절하지 않습니다.";

    public static final String MONTHLY_MODE_INVALID = "매달 타입이 적절하지 않습니다.";

    public static final String CYCLE_REQUIRED = "반복 주기는 필수로 입력해야 합니다.";
    public static final String CYCLE_TOO_SMALL = "반복 주기는 " + IterationValidationConstraints.CYCLE_MIN + " 이상이어야 합니다.";
    public static final String CYCLE_TOO_BIG = "반복 주기는 " + IterationValidationConstraints.CYCLE_MAX + " 이하여야 합니다.";

    public static final String END_REQUIRED = "반복 종료는 필수로 선택해야 합니다.";
    public static final String END_TYPE_REQUIRED = "반복 종료 타입은 필수로 선택해야 합니다.";
    public static final String END_TYPE_INVALID = "반복 종료 타입이 적절하지 않습니다.";

    public static final String END_COUNT_TOO_SMALL = "반복 종료 최소 횟수는 " + IterationValidationConstraints.END_COUNT_MIN + "회 입니다.";
    public static final String END_COUNT_TOO_BIG = "반복 종료 최대 횟수는 " + IterationValidationConstraints.END_COUNT_MAX + "회 입니다.";
    public static final String END_DATE_INVALID = "반복 종료 날짜는 이전일 수 없습니다.";


    private IterationResponseMessages() {}
}
