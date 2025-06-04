package com.poortorich.iteration.repository;

import com.poortorich.iteration.entity.IterationIncomes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationIncomesRepository extends JpaRepository<IterationIncomes, Long> {
}
