package com.cashFlow.cash.controller;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.repository.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<?> createCashFlow(@Valid @RequestBody CashFlow cashFlow) {

        cashFlowRepository.save(cashFlow);

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(8080)
                .path("/api/cashFlow").build(true);


        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
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
