package com.cashFlow.cash.controller;

import com.cashFlow.cash.model.Saving;
import com.cashFlow.cash.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SavingController {

    @Autowired
    SavingService service;

    @PostMapping("/saving")
    public ResponseEntity<?> createSaving(@Valid @RequestBody Saving saving) {

        return service.createSaving(saving);
    }

    @GetMapping("/saving")
    public ResponseEntity<List<Saving>> getAllSaving() {
        return service.getAllSaving();
    }

    @GetMapping("/saving/{id}")
    public Saving getSavingById(@PathVariable(value = "id") Long savingId) {
        return service.getSavingById(savingId);
    }

    @PutMapping("/saving/{id}")
    public Saving updateSaving(@PathVariable(value = "id") Long savingdId,
                               @Valid @RequestBody Saving savingDetails) {
        return service.updateSaving(savingdId, savingDetails);
    }

    @DeleteMapping("/saving/{id}")
    public ResponseEntity<?> deleteSaving(@PathVariable(value = "id") Long savingdId) {
        return service.deleteSaving(savingdId);
    }
}
