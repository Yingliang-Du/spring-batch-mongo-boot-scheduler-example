package com.ydu.springbatch.mongo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	private static Logger log = LoggerFactory.getLogger(BatchConfig.class);
	
	@Value("${mongodb.url}")
	private String mongoUrl;
	
	@Value("${mongodb.name}")
	private String mongoDBName;
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;
	
	@Autowired
	private JobRegistry jobRegistry;
	
	/** register jobs */
//	@Bean
//	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
//	    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
//	    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
//	    return jobRegistryBeanPostProcessor;
//	}

//	@Bean
//	protected Tasklet tasklet() {
//		return new Tasklet() {
//			@Override
//			public RepeatStatus execute(StepContribution contribution,
//					ChunkContext context) {
//				return RepeatStatus.FINISHED;
//			}
//		};
//	}

//	@Bean
//	public Job job() throws Exception {
//		return this.jobs.get("job").start(step1()).build();
//	}
//
//	@Bean
//	protected Step step1() throws Exception {
//		return this.steps.get("step1").tasklet(tasklet()).build();
//	}
	
//	@Bean
	public Job bookJob() {
		log.info("-------Starting bookJob now " + new Date());
//		return jobs.get("bookJob")	// this name could be anything??
//				.start(bookStep())
//				.build();
		return jobs.get("bookJob")
				.incrementer(new RunIdIncrementer())
				.preventRestart()
//                .listener(listener)
                .flow(bookStep())
                .end()
                .build();
	}
	
//	@Bean
	protected Step bookStep() {
		log.info("-------Starting bookStep-------");
		return steps.get("bookJobStep")
				.<BasicDBObject, BasicDBObject>chunk(1)
				.reader(reader())
				.processor(processor())
				.writer(writer())
//				.listener(listener)
				.allowStartIfComplete(true)
				.build();
	}

//	@Bean
	public BookProcessor processor() {
		log.debug("-------------Into the processor-------");
		return new BookProcessor();
	}
	
//	@Bean
//	@StepScope
    public ItemReader<BasicDBObject> reader() {
		log.debug("-------------Into the reader-------");
		String query = "{}";	//{'name' : 'BOOK1'}";
		MongoItemReader<BasicDBObject> reader = new MongoItemReader<BasicDBObject>();
		try {
			reader.setTemplate(mongoTemplate());
		} 
        catch (Exception e) {
            log.error(e.toString());
        }
        reader.setCollection("book");
        reader.setTargetType((Class<? extends BasicDBObject>) BasicDBObject.class);
        reader.setQuery(query);
        Map<String, Direction> sorts = new HashMap<>(1);
        sorts.put("name", Sort.Direction.ASC);
        reader.setSort(sorts);
        
        return reader;
    }
	
//	@Bean
    public ItemWriter<BasicDBObject> writer() {
		log.debug("-------------Into the writer-------");
        MongoItemWriter<BasicDBObject> writer = new MongoItemWriter<BasicDBObject>();
        try {
            writer.setTemplate(mongoTemplate());
        } 
        catch (Exception e) {
            log.error(e.toString());
        }
        writer.setCollection("book");
        return writer;
    }
	
	@Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongoUrl), mongoDBName);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}
