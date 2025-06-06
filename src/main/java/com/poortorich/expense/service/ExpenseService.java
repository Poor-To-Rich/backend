package com.poortorich.expense.service;

import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.entity.enums.PaymentMethod;
import java.beans.Transient;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Transient
    public void modifyPaymentMethod(Expense expense, PaymentMethod paymentMethod) {
        expense.updatePaymentMethod(paymentMethod);
    }

    @Transient
    public void modifyExpenseDate(Expense expense, LocalDate expenseDate) {
        expense.updateAccountBookDate(expenseDate);
    }
}
