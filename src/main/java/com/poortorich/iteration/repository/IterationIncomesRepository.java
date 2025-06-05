package com.poortorich.iteration.repository;

import com.poortorich.iteration.entity.IterationIncomes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IterationIncomesRepository extends JpaRepository<IterationIncomes, Long> {

    @Query("""
        SELECT DISTINCT ii.originalIncome.id
        FROM IterationIncomes ii
        """)
    List<Long> getOriginalIncomeIds();
}
