package com.poortorich.expense.request;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.expense.constants.ExpenseValidationConstraints;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.expense.request.enums.IterationAction;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.request.CustomIteration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = ExpenseResponseMessages.DATE_REQUIRED)
    private String date;

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

    private String iterationAction;

    private Boolean isIterationModified;

    @Valid
    private CustomIteration customIteration;

    public LocalDate parseDate() {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_PATTERN));
        } catch (DateTimeException e) {
            throw new BadRequestException(ExpenseResponse.DATE_INVALID);
        }
    }

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

    public IterationAction parseIterationAction() {
        return IterationAction.from(iterationAction);
    }
}
