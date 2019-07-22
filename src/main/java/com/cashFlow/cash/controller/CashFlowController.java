package com.cashFlow.cash.controller;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.CashFlow;
import com.cashFlow.cash.repository.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CashFlowController {

    @Autowired
    CashFlowRepository cashFlowRepository;

    @GetMapping("/cashFlows")
    public ResponseEntity<List<CashFlow>> getAllCashFlow() {
        List<CashFlow> allCashFlow = cashFlowRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();


        return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
    }

    @GetMapping({"/cashFlowsByDate", "/cashFlowsByDate/{period}"})
    public ResponseEntity<List<CashFlow>> getCashFlowsByDate(@PathVariable(value = "period", required = false) Integer period, @RequestParam(required = false) String startDate, String endDate) throws ParseException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        if (period == null & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAll();

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);

        } else if (period == null & startDate != null & endDate != null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse(startDate),
                    new SimpleDateFormat("yyyy-MM-dd").parse(endDate));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);

        } else if (period == 1 & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"),
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-31"));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
        } else if (period == 2 & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-02-01"),
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-02-28"));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
        } else if (period == 3 & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-01"),
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-30"));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
        } else if (period == 4 & startDate == null & endDate == null) {
            List<CashFlow> allCashFlow = cashFlowRepository.findAllByDateBetween(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-01"),
                    new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-31"));

            return new ResponseEntity<List<CashFlow>>(allCashFlow, headers, HttpStatus.OK);
        }
        return new ResponseEntity(headers, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/cashFlow")
    public ResponseEntity<?> createCashFlow(@Valid @RequestBody CashFlow cashFlow) {

        cashFlowRepository.save(cashFlow);

        List<CashFlow> allCashFlows = cashFlowRepository.findAll();
        int count = allCashFlows.size() - 1;

        int id = allCashFlows.get(count).getId().intValue();

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(8080)
                .path("/api/cashFlow/" + id).build(true);

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
        cashFlow.setDate(cashFlowDetails.getDate());

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

    @GetMapping("/cashFlow/raport")
    public ResponseEntity<Map<String, Double>> getRaportCashFlow() {
        List<CashFlow> allCashFlow = cashFlowRepository.findAll();

        Map<String, Double> cashFlowByDescription = allCashFlow.stream().collect(
                Collectors.groupingBy(CashFlow::getDescription, Collectors.summingDouble(CashFlow::getAmount)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        return new ResponseEntity<Map<String, Double>>(cashFlowByDescription, headers, HttpStatus.OK);
    }

    @PostMapping("cashFlow/uploadCashFlow")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
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
                cashFlow.setDate(new SimpleDateFormat("dd-mm-yyyy").parse(data[2]));

                listCashFlow.add(cashFlow);
                cashFlowRepository.save(cashFlow);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long countAllCashFlow = cashFlowRepository.count() + 1;

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(8080)
                .path("/api/cashFlow/" + countAllCashFlow).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }


}
