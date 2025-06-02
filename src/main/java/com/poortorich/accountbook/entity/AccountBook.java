package com.poortorich.accountbook.entity;

import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.category.entity.Category;
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
}
