package com.ydu.springbatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mongodb.BasicDBObject;
import com.ydu.springbatch.mongo.BatchConfig;

@SpringBootApplication
@EnableScheduling
public class DemoBatchApplication {
	private static Logger log = LoggerFactory.getLogger(DemoBatchApplication.class);
	
	@Autowired
	private BatchConfig batchConfig;
	
	@Autowired
	private JobLauncher jobLauncher;	
	
	@Autowired
	private JobOperator jobOperator;
	
	@Scheduled(cron="0/15 * * * * *")	//fixedRate = 30000)
	public void runJob() {
//		try {
//			jobOperator.startNextInstance("bookJob");
//		} catch (NoSuchJobException | JobParametersNotFoundException | JobRestartException
//				| JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
//				| UnexpectedJobExecutionException | JobParametersInvalidException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// make uniq JobParameters so now instance of job can be started
		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			JobExecution ex = jobLauncher.run(batchConfig.bookJob(), jobParameters);
			log.info("Execution status----->" + ex.getStatus());
		} 
		catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// 
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		log.info("-------Starting DemoBatchApplication-------");
		// System.exit is common for Batch applications since the exit code can be used to
		// drive a workflow
//		System.exit(SpringApplication
//				.exit(SpringApplication.run(DemoBatchApplication.class, args)));
		SpringApplication.run(DemoBatchApplication.class, args);
	}

}
