package com.poortorich.expense.request;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.util.ExpenseRequestTestBuilder;
import com.poortorich.global.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExpenseConverterTest {

    private ExpenseRequestTestBuilder requestTestBuilder;

    @BeforeEach
    void setUp() {
        requestTestBuilder = new ExpenseRequestTestBuilder();
    }

    @Nested
    @DisplayName("지출 제목 예외 처리 테스트")
    class TrimTitle {

        @Test
        @DisplayName("지출 제목이 null 인 경우 null 을 반환한다.")
        void trimTitle_whenNull_returnsNull() {
            ExpenseRequest request = requestTestBuilder
                    .title(null)
                    .build();

            assertThat(request.trimTitle()).isNull();
        }

        @Test
        @DisplayName("지출 제목의 앞 뒤 공백은 제거되어 저장된다.")
        void trimTitle_whenSpacedTitle_returnsTrimmedTitle() {
            ExpenseRequest request = requestTestBuilder
                    .title(ExpenseFixture.SPACED_TITLE)
                    .build();

            assertThat(request.trimTitle()).isEqualTo(ExpenseFixture.VALID_TITLE);
        }

        @Test
        @DisplayName("지출 제목이 빈 문자열일 경우 예외가 발생한다.")
        void trimTitle_whenEmptyString_throwsException() {
            ExpenseRequest request = requestTestBuilder
                    .title("")
                    .build();

            assertThatThrownBy(request::trimTitle)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ExpenseResponseMessages.TITLE_TOO_SHORT);
        }
    }

    @Nested
    @DisplayName("지출 수단 예외 처리 테스트")
    class ExceptionPaymentMethod {

        @Test
        @DisplayName("유효하지 않은 지출수단 입력 시 예외가 발생한다.")
        void whenInvalidPaymentMethod_throwsException() {
            ExpenseRequest request = requestTestBuilder
                    .paymentMethod("유효하지않은지출수단")
                    .build();

            assertThatThrownBy(request::parsePaymentMethod)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ExpenseResponseMessages.PAYMENT_METHOD_INVALID);
        }
    }

    @Nested
    @DisplayName("반복 데이터 예외 처리 테스트")
    class ExceptionIterationType {

        @Test
        @DisplayName("유효하지 않은 반복 데이터 입력 시 예외가 발생한다.")
        void whenInvalidIterationType_throwsException() {
            ExpenseRequest request = requestTestBuilder
                    .iterationType("유효하지않은반복데이터")
                    .build();

            assertThatThrownBy(request::parseIterationType)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ExpenseResponseMessages.ITERATION_TYPE_INVALID);
        }
    }
}
