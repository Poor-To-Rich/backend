package com.poortorich.expense.request;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.expense.constants.ExpenseValidationConstraints;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = ExpenseResponseMessages.DATE_REQUIRED)
    private Date date;

    @NotBlank(message = ExpenseResponseMessages.CATEGORY_NAME_REQUIRED)
    private String categoryName;

    @Size(max = ExpenseValidationConstraints.TITLE_MAX_SIZE,
            message = ExpenseResponseMessages.TITLE_TOO_LONG)
    private String title;

    @NotNull(message = ExpenseResponseMessages.COST_REQUIRED)
    @Positive(message = ExpenseResponseMessages.COST_NEGATIVE)
    private Long cost;

    @NotBlank(message = ExpenseResponseMessages.PAYMENT_METHOD_REQUIRED)
    private String paymentMethod;

    @Size(max = ExpenseValidationConstraints.MEMO_MAX_SIZE,
            message = ExpenseResponseMessages.MEMO_TOO_LONG)
    private String memo;

    private String iterationType;

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
