package com.poortorich.accountbook.entity;

import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AccountBook {

    Long getId();

    LocalDate getAccountBookDate();

    String getTitle();

    Long getCost();

    PaymentMethod getPaymentMethod();

    String getMemo();

    IterationType getIterationType();

    Category getCategory();

    User getUser();

    Iteration getGeneratedIteration();

    LocalDateTime getCreatedDate();

    AccountBookType getType();

    void updateAccountBook(String title, Long cost, String memo, IterationType iterationType, Category category);

    void updateAccountBookDate(LocalDate accountBookDate);
}
