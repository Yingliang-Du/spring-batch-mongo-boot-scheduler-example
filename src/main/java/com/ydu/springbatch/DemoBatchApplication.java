package com.ydu.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class DemoBatchApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoBatchApplication.class, args);
	}

}
