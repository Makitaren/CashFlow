package com.cashFlow.cash.repository;

import com.cashFlow.cash.model.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {

    List<CashFlow> findAllByDateBetween(
            Date startDate,
            Date endDate);

}
