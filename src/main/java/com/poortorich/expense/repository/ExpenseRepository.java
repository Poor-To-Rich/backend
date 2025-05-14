package com.poortorich.expense.repository;

import com.poortorich.expense.entity.Expense;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);
}
