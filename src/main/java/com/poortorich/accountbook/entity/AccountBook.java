package com.poortorich.accountbook.entity;

import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.category.entity.Category;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.user.entity.User;
import java.time.LocalDate;

public interface AccountBook {

    Long getId();

    LocalDate getAccountBookDate();

    String getTitle();

    Long getCost();

    String getMemo();

    IterationType getIterationType();

    Category getCategory();
    
    User getUser();

    Iteration getGeneratedIteration();

    void updateAccountBook(String title, Long cost, String memo, IterationType iterationType, Category category);

    void updateAccountBookDate(LocalDate accountBookDate);
}
