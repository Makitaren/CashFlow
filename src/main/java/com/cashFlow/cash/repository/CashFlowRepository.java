package com.cashFlow.cash.repository;

import com.cashFlow.cash.model.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {

}
