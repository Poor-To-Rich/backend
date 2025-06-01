package com.poortorich.accountbook.service;

import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.category.entity.Category;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AccountBookService<T extends AccountBookRequest, E> {

    public E create(T request, Category category, User user) {
        E entity = buildEntity(request, category, user);
        getRepository().save(entity);
        return entity;
    }

    protected abstract E buildEntity(T request, Category category, User user);

    protected abstract JpaRepository<E, Long> getRepository();
}
