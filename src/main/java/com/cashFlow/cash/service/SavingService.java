package com.cashFlow.cash.service;

import com.cashFlow.cash.exception.ResourceNotFoundException;
import com.cashFlow.cash.model.Saving;
import com.cashFlow.cash.repository.SavingRepository;
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
public class SavingService {
    @Autowired
    SavingRepository savingRepository;

    public ResponseEntity<?> createSaving(Saving saving) {

        savingRepository.save(saving);

        List<Saving> allSaving = savingRepository.findAll();
        int count = allSaving.size() - 1;

        int id = allSaving.get(count).getId().intValue();

        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(9000)
                .path("/api/saving/" + id).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Saving>> getAllSaving() {
        List<Saving> allSaving = savingRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.getConnection();

        return new ResponseEntity<List<Saving>>(allSaving, headers, HttpStatus.OK);
    }

    public Saving getSavingById(Long savingId) {
        return savingRepository.findById(savingId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving", "id", savingId));
    }

    public Saving updateSaving(Long savingId, Saving savingDetails) {

        Saving saving = savingRepository.findById(savingId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving", "id", savingId));

        saving.setName(savingDetails.getName());
        saving.setAmount(savingDetails.getAmount());

        Saving updatedSaving = savingRepository.save(saving);
        return updatedSaving;
    }

    public ResponseEntity<?> deleteSaving(Long savingId) {
        Saving saving = savingRepository.findById(savingId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving", "id", savingId));

        savingRepository.delete(saving);

        return ResponseEntity.ok().build();
    }
}
