package com.poortorich.iteration.entity;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.user.entity.User;

public interface Iteration {

    Long getId();

    User getUser();

    AccountBook getOriginalAccountBook();

    AccountBook getGeneratedAccountBook();

    IterationInfo getIterationInfo();
}
