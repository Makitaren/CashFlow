package com.cashFlow.cash.controller;

import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.model.Period;
import com.cashFlow.cash.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CashFlowController {

    @Autowired
    Service service;

    @GetMapping({"/cashFlows", "/cashFlows/{period}"})
    public ResponseEntity<List<CashFlow>> getCashFlowsByDate(
            @PathVariable(value = "period", required = false) Long periodId,
            @RequestParam(required = false) String startDate, String endDate) throws ParseException {
        return service.getCashFlows(periodId, startDate, endDate);
    }

    @GetMapping("/periods")
    public ResponseEntity<List<Period>> getAllPeriods() {
        return service.getPeriods();
    }

    @PostMapping("/cashFlow")
    public ResponseEntity<?> createCashFlow(@Valid @RequestBody CashFlow cashFlow) {
        return service.createCashFlow(cashFlow);
    }

    @PostMapping("/period")
    public ResponseEntity<?> createPeriod(@Valid @RequestBody Period period) {

        return service.createPeriod(period);
    }

    @GetMapping("/cashFlow/{id}")
    public CashFlow getCashFlowById(@PathVariable(value = "id") Long cashFlowId) {
        return service.getCashFlowById(cashFlowId);
    }

    @GetMapping("/period/{id}")
    public Period getPeriodById(@PathVariable(value = "id") Long periodId) {
        return service.getPeriodById(periodId);
    }

    @PutMapping("/cashFlow/{id}")
    public CashFlow updateCashFlow(@PathVariable(value = "id") Long cashFlowId,
                                   @Valid @RequestBody CashFlow cashFlowDetails) {
        return service.updateCashFlow(cashFlowId, cashFlowDetails);
    }

    @PutMapping("/period/{id}")
    public Period updatePeriod(@PathVariable(value = "id") Long periodId,
                               @Valid @RequestBody Period periodDetails) {
        return service.updatePeriod(periodId, periodDetails);
    }

    @DeleteMapping("/cashFlow/{id}")
    public ResponseEntity<?> deleteCashFlow(@PathVariable(value = "id") Long cashFlowId) {
        return service.deleteCashFlow(cashFlowId);
    }

    @DeleteMapping("/period/{id}")
    public ResponseEntity<?> deletePeriod(@PathVariable(value = "id") Long periodId) {
        return service.deletePeriod(periodId);
    }

    @GetMapping("/cashFlows/raport")
    public ResponseEntity<List<Map<String, Double>>> getRaportCashFlows() throws ParseException {
        return service.getRaportCashFlows();
    }

    @PostMapping("cashFlow/uploadCashFlow")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadFile(file);
    }

        @GetMapping("/cashFlow/statistic")
    public ResponseEntity<Map<String, Double>> getstatisticCashFlow() throws ParseException {
        return service.getStatistic();
    }
}
