package com.poortorich.expense.request;

import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.expense.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ExpenseRequest extends AccountBookRequest {

    @NotBlank(message = ExpenseResponseMessages.PAYMENT_METHOD_REQUIRED)
    private String paymentMethod;

    public PaymentMethod parsePaymentMethod() {
        return PaymentMethod.from(paymentMethod);
    }
}
