package com.cashFlow.cash.repository;

import com.cashFlow.cash.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {

}

