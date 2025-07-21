package com.poortorich.category.repository;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndUser(Long id, User user);

    Optional<Category> findByIdAndUserAndIsDeletedFalse(Long id, User user);

    Optional<Category> findByNameAndUser(String name, User user);

    Optional<Category> findByUserAndNameAndTypeIn(User user, String name, List<CategoryType> sameGroupTypes);

    Optional<Category> findByUserAndNameAndTypeInAndIsDeletedFalse(User user, String name, List<CategoryType> type);

    @Query("""
        SELECT c
         FROM Category c
        WHERE c.user = :user
          AND c.name = :name
          AND c.type IN :type
          AND c.isDeleted = false
          AND c.id <> :id
    """)
    Optional<Category> findByNameExcludingId(User user, String name, List<CategoryType> type, Long id);

    List<Category> findByUserAndTypeAndIsDeletedFalse(User user, CategoryType type);

    List<Category> findByUserAndTypeInAndIsDeletedFalse(User user, List<CategoryType> types);

    void deleteByUserAndType(User user, CategoryType type);

    int countByUserAndTypeAndIsDeletedFalse(User user, CategoryType type);
}
