package com.ydu.springbatch.batch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.ydu.springbatch.config.DatabaseConfig;

@Component
public class DemoBatchJob {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DatabaseConfig databaseConfig;
	
	@Autowired
	private JobBuilderFactory jobs;
	
	@Autowired
	private StepBuilderFactory steps;

	public Job demoJob() {
		log.info("--------Starting demoJob now-->" + new Date());
		return jobs.get("demoJob")
				.incrementer(new RunIdIncrementer())
				.preventRestart()
//				.listener(listener)
				.flow(demoStep())
				.end()
				.build();
	}
	
	protected Step demoStep() {
		log.info("-------Starting demoStep-------");
		return steps.get("demoStep")
				.<BasicDBObject, BasicDBObject>chunk(1)
				.reader(reader())
				.processor(processor())
				.writer(writer())
//				.listener(listener)
				.allowStartIfComplete(true)
				.build();
	}
	
	public DemoProcessor processor() {
		log.debug("-------------Into the processor-------");
		return new DemoProcessor();
	}
	
	public ItemReader<BasicDBObject> reader() {
		log.debug("-------------Into the reader-------");
		// -------query base on date time range
		// start of today
		LocalDate today = LocalDate.now();
		Date startToday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
		log.info("start of today --> " + startToday);
		// start of tomorrow
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		Date startTomorrow = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());
		log.info("start of tomorrow --> " + startTomorrow);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		String q = "{'d': {'$gt': ISODate('" + 
				format.format(startToday) +
				"'), '$lt': ISODate('" +
				format.format(startTomorrow) + "')}}";
		Query qu = new Query().addCriteria(Criteria.where("d").gt(startToday).lt(startTomorrow));
		// build query for mongo java driver
		QueryBuilder qb = new QueryBuilder();
		String que = qb.put("d")
				.lessThan(startTomorrow)
				.put("d")
				.greaterThan(startToday)
				.get()
				.toString();
		log.info("query for java mongo driver --> " + que);
		// -------end query base on date time range
		String query = "{}";	//{'name' : 'BOOK1'}";
		MongoItemReader<BasicDBObject> reader = new MongoItemReader<BasicDBObject>();
		try {
			reader.setTemplate(databaseConfig.mongoTemplate());
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
	
	public ItemWriter<BasicDBObject> writer() {
		log.debug("-------------Into the writer-------");
        MongoItemWriter<BasicDBObject> writer = new MongoItemWriter<BasicDBObject>();
        try {
            writer.setTemplate(databaseConfig.mongoTemplate());
        } 
        catch (Exception e) {
            log.error(e.toString());
        }
        writer.setCollection("book");
        return writer;
    }
}
