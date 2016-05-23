package com.ydu.springbatch.schedule;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ydu.springbatch.batch.DemoBatchJob;

@Component
public class ScheduleJob {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private DemoBatchJob demoBatchJob;

	@Scheduled(cron = "0/30 * * * * *")
	public void runJob() {
		// make unique JobParameters so now instance of job can be started
		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			JobExecution ex = jobLauncher.run(demoBatchJob.demoJob(), jobParameters);
			log.info("Execution status----->" + ex.getStatus());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			//
			e.printStackTrace();
		}
	}
}
