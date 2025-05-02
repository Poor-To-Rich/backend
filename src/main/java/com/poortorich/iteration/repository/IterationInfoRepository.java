package com.poortorich.iteration.repository;

import com.poortorich.iteration.entity.info.IterationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationInfoRepository extends JpaRepository<IterationInfo, Long> {
}
