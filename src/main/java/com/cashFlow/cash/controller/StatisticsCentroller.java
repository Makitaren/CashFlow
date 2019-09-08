package com.cashFlow.cash.controller;

import com.cashFlow.cash.service.StatisticService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsCentroller {

    @Autowired
    StatisticService service;

    @GetMapping("/cashFlows/spend")
    public ResponseEntity<List<Map<String, Double>>> getSpendCashFlows() throws ParseException {
        return service.getRaportCashFlows();
    }

    @GetMapping("/cashFlows/available")
    public ResponseEntity<List<Map<String, Double>>> getAvailableCashFlows() throws ParseException {
        return service.getRaportCashFlows();
    }

    @GetMapping("/cashFlow/statistic")
    public ResponseEntity<Map<String, Double>> getstatisticCashFlow() throws ParseException {
        return service.getStatistic();
    }

    @GetMapping("/saving/raport")
    public ResponseEntity<Pair<String, Double>> getRaportSaving() throws ParseException {
        return service.getRaportSaving();
    }
}
