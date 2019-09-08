package com.cashFlow.cash.controller;

import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.service.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CashFlowController {

    @Autowired
    CashFlowService service;

    @GetMapping({"/cashFlows", "/cashFlows/{period}"})
    public ResponseEntity<List<CashFlow>> getCashFlowsByDate(
            @PathVariable(value = "period", required = false) Long periodId,
            @RequestParam(required = false) String startDate, String endDate) throws ParseException {
        return service.getCashFlows(periodId, startDate, endDate);
    }
    @CrossOrigin
    @PostMapping("/cashFlow")
    public ResponseEntity<?> createCashFlow(@Valid @RequestBody CashFlow cashFlow) {
        return service.createCashFlow(cashFlow);
    }

    @CrossOrigin
    @GetMapping("/cashFlow/{id}")
    public CashFlow getCashFlowById(@PathVariable(value = "id") Long cashFlowId) {
        return service.getCashFlowById(cashFlowId);
    }

    @PutMapping("/cashFlow/{id}")
    public CashFlow updateCashFlow(@PathVariable(value = "id") Long cashFlowId,
                                   @Valid @RequestBody CashFlow cashFlowDetails) {
        return service.updateCashFlow(cashFlowId, cashFlowDetails);
    }

    @DeleteMapping("/cashFlow/{id}")
    public ResponseEntity<?> deleteCashFlow(@PathVariable(value = "id") Long cashFlowId) {
        return service.deleteCashFlow(cashFlowId);
    }

    @PostMapping("cashFlow/uploadCashFlow")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadFile(file);
    }
}
