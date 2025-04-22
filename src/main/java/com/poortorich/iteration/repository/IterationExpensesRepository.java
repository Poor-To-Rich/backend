package com.poortorich.iteration.repository;

import com.poortorich.iteration.entity.IterationExpenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationExpensesRepository extends JpaRepository<IterationExpenses, Long> {
}
