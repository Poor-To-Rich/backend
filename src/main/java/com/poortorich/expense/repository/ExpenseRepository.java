package com.poortorich.expense.repository;

import com.poortorich.expense.entity.Expense;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndUser(Long id, User user);

    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
