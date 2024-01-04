package com.ffx.rates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ffx.rates.entities.Rate;
import com.ffx.rates.repositories.RateRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class RateServiceApplication {

	@Autowired
	private RateRepository rateRepository;

	public static void main(String[] args) {
		SpringApplication.run(RateServiceApplication.class, args);
	}

	@PostConstruct
	public void setupData() {
		rateRepository.saveAll(Arrays.asList(
			Rate.builder().id(1).type("PERSONAL").rateValue(10.0).build(),
			Rate.builder().id(2).type("HOUSING").rateValue(8.0).build()
		));
	}
}
