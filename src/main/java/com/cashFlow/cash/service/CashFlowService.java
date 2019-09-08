package com.cashFlow.cash.service;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.model.Period;
import com.cashFlow.cash.repository.CashFlowRepository;
import com.cashFlow.cash.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CashFlowService {

    @Autowired
    CashFlowRepository cashFlowRepository;
    @Autowired
    PeriodRepository periodRepository;


    public ResponseEntity<List<CashFlow>> getCashFlows(Long periodId, String startDate, String endDate) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        if (periodId == null & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAll();

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);

        } else if (periodId == null & startDate != null & endDate != null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(startDate),
                    new SimpleDateFormat("yyyy-MM-dd").parse(endDate));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);

        } else if (periodId != null & startDate == null & endDate == null) {
            Period period = periodRepository.findById(periodId)
                    .orElseThrow(() -> new ResourceNotFoundException("Period", "id", periodId));
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").parse(period.getEndDate()));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
        }
        return new ResponseEntity(headers, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> createCashFlow(CashFlow cashFlow) {

        cashFlowRepository.save(cashFlow);

        List<CashFlow> allCashFlows = cashFlowRepository.findAll();
        int count = allCashFlows.size() - 1;

        int id = allCashFlows.get(count).getId().intValue();

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(9000)
                .path("/api/cashFlow/" + id).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    public CashFlow getCashFlowById(Long cashFlowId) {
        return cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));
    }

    public CashFlow updateCashFlow(Long cashFlowId, CashFlow cashFlowDetails) {

        CashFlow cashFlow = cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));

        cashFlow.setAmount(cashFlowDetails.getAmount());
        cashFlow.setDescription(cashFlowDetails.getDescription());
        cashFlow.setDate(cashFlowDetails.getDate());

        CashFlow updatedCashFlow = cashFlowRepository.save(cashFlow);
        return updatedCashFlow;
    }

    public ResponseEntity<?> deleteCashFlow(Long cashFlowId) {
        CashFlow cashFlow = cashFlowRepository.findById(cashFlowId)
                .orElseThrow(() -> new ResourceNotFoundException("CashFlow", "id", cashFlowId));

        cashFlowRepository.delete(cashFlow);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> uploadFile(MultipartFile file) throws IOException {
        String line = "";
        String cvsSplitBy = ",";
        List<CashFlow> listCashFlow = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), "UTF-8"))) {

            while ((line = br.readLine()) != null) {

                String[] data = line.split(cvsSplitBy);

                CashFlow cashFlow = new CashFlow();
                cashFlow.setAmount(Double.parseDouble(data[0]));
                cashFlow.setDescription(data[1]);
                cashFlow.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(data[2]));
                cashFlow.setCash(Boolean.parseBoolean(data[3]));

                listCashFlow.add(cashFlow);
                cashFlowRepository.save(cashFlow);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long countAllCashFlow = cashFlowRepository.count() + 1;

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(9000)
                .path("/api/cashFlow/" + countAllCashFlow).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
