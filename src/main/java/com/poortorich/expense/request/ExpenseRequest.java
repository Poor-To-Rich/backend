package com.poortorich.expense.request;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.expense.constants.ExpenseValidationConstraints;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.constants.DatePattern;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.request.CustomIteration;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExpenseRequest {

    @DateTimeFormat(pattern = DatePattern.BASIC_PATTERN)
    @NotNull(message = ExpenseResponseMessages.DATE_REQUIRED)
    private LocalDate date;

    @NotBlank(message = ExpenseResponseMessages.CATEGORY_NAME_REQUIRED)
    private String categoryName;

    @Size(max = ExpenseValidationConstraints.TITLE_MAX_SIZE,
            message = ExpenseResponseMessages.TITLE_TOO_LONG)
    private String title;

    @NotNull(message = ExpenseResponseMessages.COST_REQUIRED)
    @Positive(message = ExpenseResponseMessages.COST_NEGATIVE)
    @Max(value = ExpenseValidationConstraints.COST_LIMIT,
            message = ExpenseResponseMessages.COST_TOO_BIG)
    private Long cost;

    @NotBlank(message = ExpenseResponseMessages.PAYMENT_METHOD_REQUIRED)
    private String paymentMethod;

    @Size(max = ExpenseValidationConstraints.MEMO_MAX_SIZE,
            message = ExpenseResponseMessages.MEMO_TOO_LONG)
    private String memo;

    private String iterationType;

    private CustomIteration customIteration;

    public PaymentMethod parsePaymentMethod() {
        return PaymentMethod.from(paymentMethod);
    }

    public String trimTitle() {
        if (title == null) {
            return null;
        }

        String trimTitle = title.trim();
        if (!trimTitle.isEmpty()) {
            return trimTitle;
        }

        throw new BadRequestException(ExpenseResponse.TITLE_TOO_SHORT);
    }

    public IterationType parseIterationType() {
        return IterationType.from(iterationType);
    }
}
