package com.cashFlow.cash.service;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.Period;
import com.cashFlow.cash.repository.CashFlowRepository;
import com.cashFlow.cash.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class PeriodService {
    @Autowired
    CashFlowRepository cashFlowRepository;
    @Autowired
    PeriodRepository periodRepository;

    public ResponseEntity<List<Period>> getPeriods() {
        List<Period> allPeriods = periodRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        return new ResponseEntity<List<Period>>(allPeriods, headers, HttpStatus.OK);
    }

    public ResponseEntity<?> createPeriod(Period period) {

        periodRepository.save(period);

        List<Period> allPeriods = periodRepository.findAll();
        int count = allPeriods.size() - 1;

        int id = allPeriods.get(count).getId().intValue();

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(8080)
                .path("/api/period/" + id).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    public Period getPeriodById(Long periodId) {
        return periodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Period", "id", periodId));
    }

    public Period updatePeriod(Long periodId, Period periodDetails) {

        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Period", "id", periodId));

        period.setStartDate(periodDetails.getStartDate());
        period.setEndDate(periodDetails.getEndDate());

        Period updatedPeriod = periodRepository.save(period);
        return updatedPeriod;
    }

    public ResponseEntity<?> deletePeriod(Long periodId) {
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Period", "id", periodId));

        periodRepository.delete(period);

        return ResponseEntity.ok().build();
    }
}
