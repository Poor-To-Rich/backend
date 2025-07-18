package com.poortorich.expense.entity.enums;

import com.poortorich.expense.response.enums.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum PaymentMethod {

    CASH("현금"),
    CREDIT_CARD("신용카드"),
    CHECK_CARD("체크카드"),
    BANK_TRANSFER("계좌이체");

    public final String type;

    public static PaymentMethod from(String type) {
        return Arrays.stream(PaymentMethod.values())
                .filter(paymentMethod -> Objects.equals(paymentMethod.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ExpenseResponse.PAYMENT_METHOD_INVALID));
    }

    @Override
    public String toString() {
        return type;
    }
}
