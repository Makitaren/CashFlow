package com.cashFlow.cash.controller;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.repository.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CashFlowController {

    @Autowired
    CashFlowRepository cashFlowRepository;

    @GetMapping("/cashFlow")
    public List<CashFlow> getAllCashFlow() {
        return cashFlowRepository.findAll();
    }

    @PostMapping("/cashFlow")
    public CashFlow createCashFlow(@Valid @RequestBody CashFlow cashFlow) {
        return cashFlowRepository.save(cashFlow);
    }

    @GetMapping("/cashFlow/{id}")
    public CashFlow getCashFlowById(@PathVariable(value = "id") Long cashFlowId) {
        return cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));
    }

    @PutMapping("/cashFlow/{id}")
    public CashFlow updateCashFlow(@PathVariable(value = "id") Long cashFlowId,
                                           @Valid @RequestBody CashFlow cashFlowDetails) {

        CashFlow cashFlow = cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));

        cashFlow.setAmount(cashFlowDetails.getAmount());
        cashFlow.setDescription(cashFlowDetails.getDescription());

        CashFlow updatedCashFlow = cashFlowRepository.save(cashFlow);
        return updatedCashFlow;
    }

    @DeleteMapping("/cashFlow/{id}")
    public ResponseEntity<?> deleteCashFlow(@PathVariable(value = "id") Long cashFlowId) {
        CashFlow cashFlow = cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));

        cashFlowRepository.delete(cashFlow);

        return ResponseEntity.ok().build();
    }
}
