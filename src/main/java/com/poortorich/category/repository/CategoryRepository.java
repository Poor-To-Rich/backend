package com.poortorich.category.repository;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndUser(Long id, User user);

    Optional<Category> findByIdAndUserAndIsDeletedFalse(Long id, User user);

    Optional<Category> findByNameAndUser(String name, User user);

    Optional<Category> findByUserAndNameAndTypeInAndIsDeletedFalse(User user, String name, List<CategoryType> type);

    List<Category> findByUserAndTypeAndIsDeletedFalse(User user, CategoryType type);

    List<Category> findByUserAndTypeInAndIsDeletedFalse(User user, List<CategoryType> types);

    void deleteByUserAndType(User user, CategoryType type);
}
