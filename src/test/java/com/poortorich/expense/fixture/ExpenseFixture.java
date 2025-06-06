package com.poortorich.expense.fixture;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryFixture;
import com.poortorich.expense.entity.Expense;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import java.time.LocalDate;
import java.util.List;

public class ExpenseFixture {

    public static final String VALID_DATE = "2025-01-01";

    public static final String VALID_CATEGORY_NAME = "주거비";

    public static final String VALID_TITLE = "월세";
    public static final String SPACED_TITLE = "  월세 ";

    public static final Long VALID_COST = 700000L;

    public static final String VALID_PAYMENT_METHOD_STRING = "계좌이체";

    public static final String VALID_MEMO = "월세 너무 비싸다";

    public static final String VALID_ITERATION_TYPE_STRING = "monthly";

    public static final CustomIteration VALID_CUSTOM_ITERATION = null;
    private static final User MOCK_USER = UserFixture.createDefaultUser();

    private ExpenseFixture() {
    }

    public static Expense defaultExpense(LocalDate date) {
        return Expense.builder()
                .expenseDate(date)
                .title("회비")
                .cost(10000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .iterationType(IterationType.CUSTOM)
                .category(Category.builder().name("회비").build())
                .build();
    }

    public static Expense HOUSING_EXPENSE_1() {
        return Expense.builder()
                .id(1L)
                .expenseDate(LocalDate.of(2025, 4, 27))
                .title("월세")
                .cost(1000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .memo("관리비 포함")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.HOUSING)
                .user(MOCK_USER)
                .build();
    }

    public static Expense FOOD_EXPENSE_1() {
        return Expense.builder()
                .id(2L)
                .expenseDate(LocalDate.of(2025, 4, 30))
                .title("점심")
                .cost(2000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("맥도날드")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.FOOD)
                .user(MOCK_USER)
                .build();
    }

    public static Expense FOOD_EXPENSE_2() {
        return Expense.builder()
                .id(16L)
                .expenseDate(LocalDate.of(2025, 5, 4))
                .title("저녁")
                .cost(16000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("감자탕")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.FOOD)
                .user(MOCK_USER)
                .build();
    }

    public static Expense FOOD_EXPENSE_3() {
        return Expense.builder()
                .id(17L)
                .expenseDate(LocalDate.of(2025, 5, 16))
                .title("야식")
                .cost(17000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .memo("치킨")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.FOOD)
                .user(MOCK_USER)
                .build();
    }

    public static Expense TRANSPORTATION_EXPENSE_1() {
        return Expense.builder()
                .id(3L)
                .expenseDate(LocalDate.of(2025, 5, 2))
                .title("지하철")
                .cost(3000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("출근")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.TRANSPORTATION)
                .user(MOCK_USER)
                .build();
    }

    public static Expense SHOPPING_EXPENSE_1() {
        return Expense.builder()
                .id(4L)
                .expenseDate(LocalDate.of(2025, 5, 5))
                .title("어린이날 선물")
                .cost(4000L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .memo("나를 위한 선물")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.SHOPPING)
                .user(MOCK_USER)
                .build();
    }

    public static Expense HEALTH_MEDICAL_EXPENSE_1() {
        return Expense.builder()
                .id(5L)
                .expenseDate(LocalDate.of(2025, 5, 7))
                .title("건강 검진")
                .cost(5000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("아프지 말자")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.HEALTH_MEDICAL)
                .user(MOCK_USER)
                .build();
    }

    public static Expense EDUCATION_EXPENSE_1() {
        return Expense.builder()
                .id(6L)
                .expenseDate(LocalDate.of(2025, 5, 9))
                .title("자기 계발 도서 구매")
                .cost(6000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("취업해보자")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.EDUCATION)
                .user(MOCK_USER)
                .build();
    }

    public static Expense CULTURE_HOBBY_EXPENSE_1() {
        return Expense.builder()
                .id(7L)
                .expenseDate(LocalDate.of(2025, 5, 14))
                .title("피규어 구매")
                .cost(7000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("나니가 스키~")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.CULTURE_HOBBY)
                .user(MOCK_USER)
                .build();
    }

    public static Expense GIFTS_EVENTS_EXPENSE_1() {
        return Expense.builder()
                .id(8L)
                .expenseDate(LocalDate.of(2025, 5, 17))
                .title("생일 선물")
                .cost(8000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("생일 축하해!")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.GIFTS_EVENTS)
                .user(MOCK_USER)
                .build();
    }

    public static Expense BEAUTY_EXPENSE_1() {
        return Expense.builder()
                .id(9L)
                .expenseDate(LocalDate.of(2025, 5, 20))
                .title("선크림 구매")
                .cost(9000L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .memo("자외선은 무서워")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.BEAUTY)
                .user(MOCK_USER)
                .build();
    }

    public static Expense CAFE_EXPENSE_1() {
        return Expense.builder()
                .id(10L)
                .expenseDate(LocalDate.of(2025, 5, 21))
                .title("아메리카노")
                .cost(10000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .memo("카페인 수혈 ㅠ")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.CAFE)
                .user(MOCK_USER)
                .build();
    }

    public static Expense PET_CATE_EXPENSE_1() {
        return Expense.builder()
                .id(11L)
                .expenseDate(LocalDate.of(2025, 5, 22))
                .title("심장사상충 약")
                .cost(11000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("나쁜 개는 없다")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.PET_CARE)
                .user(MOCK_USER)
                .build();
    }

    public static Expense OTHER_EXPENSE_1() {
        return Expense.builder()
                .id(12L)
                .expenseDate(LocalDate.of(2025, 5, 25))
                .title("빌린 돈 갚기")
                .cost(12000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .memo("빌려줘서 고마워")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.EXPENSE_OTHER)
                .user(MOCK_USER)
                .build();
    }

    public static Expense ALCOHOL_ENTERTAINMENT_EXPENSE_1() {
        return Expense.builder()
                .id(13L)
                .expenseDate(LocalDate.of(2025, 5, 27))
                .title("고등학교 친구 모임")
                .cost(13000L)
                .paymentMethod(PaymentMethod.CASH)
                .memo("먹고 죽어보자~")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.ALCOHOL_ENTERTAINMENT)
                .user(MOCK_USER)
                .build();
    }

    public static Expense SAVINGS_INVESTMENT_EXPENSE_1() {
        return Expense.builder()
                .id(14L)
                .expenseDate(LocalDate.of(2025, 5, 29))
                .title("적금")
                .cost(14000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .memo("미래 도약 가보자")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.SAVINGS_INVESTMENT)
                .user(MOCK_USER)
                .build();
    }

    public static Expense TRAVEL_ACCOMMODATION_EXPENSE_1() {
        return Expense.builder()
                .id(15L)
                .expenseDate(LocalDate.of(2025, 5, 31))
                .title("속초 여행")
                .cost(15000L)
                .paymentMethod(PaymentMethod.CHECK_CARD)
                .memo("바다 가자!")
                .iterationType(IterationType.DEFAULT)
                .category(CategoryFixture.TRAVEL_ACCOMMODATION)
                .user(MOCK_USER)
                .build();
    }

    public static List<Expense> getAllExpense() {
        return List.of(
                HOUSING_EXPENSE_1(),
                FOOD_EXPENSE_1(),
                TRANSPORTATION_EXPENSE_1(),
                SHOPPING_EXPENSE_1(),
                HEALTH_MEDICAL_EXPENSE_1(),
                EDUCATION_EXPENSE_1(),
                CULTURE_HOBBY_EXPENSE_1(),
                GIFTS_EVENTS_EXPENSE_1(),
                BEAUTY_EXPENSE_1(),
                CAFE_EXPENSE_1(),
                PET_CATE_EXPENSE_1(),
                OTHER_EXPENSE_1(),
                ALCOHOL_ENTERTAINMENT_EXPENSE_1(),
                SAVINGS_INVESTMENT_EXPENSE_1(),
                TRAVEL_ACCOMMODATION_EXPENSE_1()
        );
    }

    public static Long totalExpense() {
        return getAllExpense().stream()
                .map(Expense::getCost)
                .reduce(0L, Long::sum);
    }
}
