package com.cashFlow.cash.controller;

import com.cashFlow.cash.model.Period;
import com.cashFlow.cash.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PeriodsController {

    @Autowired
    PeriodService service;

    @PostMapping("/period")
    public ResponseEntity<?> createPeriod(@Valid @RequestBody Period period) {

        return service.createPeriod(period);
    }

    @GetMapping("/periods")
    public ResponseEntity<List<Period>> getAllPeriods() {
        return service.getPeriods();
    }

    @GetMapping("/period/{id}")
    public Period getPeriodById(@PathVariable(value = "id") Long periodId) {
        return service.getPeriodById(periodId);
    }

    @PutMapping("/period/{id}")
    public Period updatePeriod(@PathVariable(value = "id") Long periodId,
                               @Valid @RequestBody Period periodDetails) {
        return service.updatePeriod(periodId, periodDetails);
    }

    @DeleteMapping("/period/{id}")
    public ResponseEntity<?> deletePeriod(@PathVariable(value = "id") Long periodId) {
        return service.deletePeriod(periodId);
    }
}
