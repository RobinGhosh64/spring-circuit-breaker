package com.ffx.rates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffx.rates.entities.Rate;
import com.ffx.rates.services.RateService;

@RestController
@RequestMapping("api")
public class RateController {

    @Autowired
    private RateService rateService;

    @GetMapping(path = "/rates/{type}")
    public ResponseEntity<Rate> getRateByType(@PathVariable("type") String type) {
        return ResponseEntity.ok().body(rateService.getRateByType(type));
    }
}
