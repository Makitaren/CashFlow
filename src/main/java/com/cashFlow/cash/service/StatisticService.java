package com.cashFlow.cash.service;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.model.Period;
import com.cashFlow.cash.model.Saving;
import com.cashFlow.cash.repository.CashFlowRepository;
import com.cashFlow.cash.repository.PeriodRepository;
import com.cashFlow.cash.repository.SavingRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatisticService {
    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    CashFlowRepository cashFlowRepository;

    @Autowired
    SavingRepository savingRepository;

    public List<Map<String, Double>> getRaport() throws ParseException {
        List<Map<String, Double>> raports = new ArrayList<>();

        List<Period> allPeriods = periodRepository.findAll();

        for (Period p : allPeriods) {
            Period period = periodRepository.findById(p.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Period", "id", p.getId()));

            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getEndDate()));

            Map<String, Double> cashFlowByDescription = allCashFlow.stream()
                    .collect(
                            Collectors.groupingBy(CashFlow::getDescription, Collectors.summingDouble(CashFlow::getAmount)));

            raports.add(cashFlowByDescription);
        }
        return raports;
    }

    public List<Double> getCashToSpend() throws ParseException {
        List<Double> sums = new ArrayList<>();

        List<Period> allPeriods = periodRepository.findAll();

        for (Period p : allPeriods) {
            Period period = periodRepository.findById(p.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Period", "id", p.getId()));

            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getEndDate()));

            Double sum = allCashFlow.stream()
                    .filter(cashFlow -> cashFlow.isCash() == true)
                    .filter(cashFlow -> cashFlow.getDescription() != "CASH")
                    .collect(Collectors.summingDouble(CashFlow::getAmount));
            sums.add(sum);
        }
        return sums;
    }

    public List<Double> getDigitalToSpend() throws ParseException {
        List<Double> sums = new ArrayList<>();

        List<Period> allPeriods = periodRepository.findAll();

        for (Period p : allPeriods) {
            Period period = periodRepository.findById(p.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Period", "id", p.getId()));

            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getEndDate()));

            Double sum = allCashFlow.stream()
                    .filter(cashFlow -> cashFlow.isCash() == false)
//                    .filter(cashFlow -> cashFlow.getDescription() != "DIGITAL")
                    .collect(Collectors.summingDouble(CashFlow::getAmount));
            sums.add(sum);
        }
        return sums;
    }

    public List<Map<String, Double>> spendCash() throws ParseException {
        List<Map<String, Double>> raport = getRaport();
        List<Double> spendCash = getCashToSpend();
        List<Double> spendDigital = getDigitalToSpend();

        for (int i = 0; i < raport.size(); i++) {
           double cash = raport.get(i).get("CASH");
           double digital = raport.get(i).get("DIGITAL");

           double otherCash = cash - spendCash.get(i);
           double otherDigital = digital - spendDigital.get(i);

           raport.get(i).put("CASH", otherCash);
           raport.get(i).put("DIGITAL", otherDigital);

        }

        return raport;
    }


    public ResponseEntity<List<Map<String, Double>>> getRaportCashFlows() throws ParseException {
        List<Map<String, Double>> raports = getRaport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        return new ResponseEntity<List<Map<String, Double>>>(raports, headers, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Double>> getStatistic() throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        List<Period> allPeriods = periodRepository.findAll();

        int size = allPeriods.size();

        List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                new SimpleDateFormat("yyyy-MM-dd").parse(allPeriods.get(0).getStartDate()),
                new SimpleDateFormat("yyyy-MM-dd").parse(allPeriods.get(size - 1).getEndDate()));

        Map<String, Double> cashFlows = allCashFlow.stream().collect(
                Collectors.groupingBy(CashFlow::getDescription, Collectors.summingDouble((CashFlow::getAmount))));

        Map<String, Double> statistic = cashFlows
                .entrySet()
                .stream()
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() / (size)));
        return new ResponseEntity<Map<String, Double>>(statistic, headers, HttpStatus.OK);
    }

    public ResponseEntity<Pair<String, Double>> getRaportSaving() throws ParseException {
        List<Saving> allSaving = savingRepository.findAll();

        Double count = allSaving.stream().collect(Collectors.summingDouble(Saving::getAmount));
        Pair<String, Double> raport = new Pair<>("Saving", count);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        return new ResponseEntity<Pair<String, Double>>(raport, headers, HttpStatus.OK);
    }

    public List<Map<String, Double>> settleCash(List<Map<String, Double>> map1, List<Map<String, Double>> map2) {
        List<Map<String, Double>> settled = new ArrayList<>();

        for (int i = 0; i < map1.size(); i++) {
            Map<String, Double> add = Stream.of(map1.get(i), map2.get(i)).map(Map::entrySet).flatMap(Collection::stream)
                    .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
            settled.add(add);
        }
        return settled;
    }
}
