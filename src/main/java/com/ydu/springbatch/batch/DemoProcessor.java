package com.ydu.springbatch.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.mongodb.BasicDBObject;

public class DemoProcessor implements ItemProcessor<BasicDBObject, BasicDBObject> {
	private static Logger log = LoggerFactory.getLogger(DemoProcessor.class);

	@Override
	public BasicDBObject process(BasicDBObject item) throws Exception {
		// Do something about data field
		String name = item.getString("name");
		String updatedName = name.toLowerCase();
		if (!name.equals(name.toUpperCase())) {
			// contains lower case convert to upper case
			updatedName = name.toUpperCase();
		}
		item.put("name", updatedName);
		log.info("-----------------Converting (" + name + ") into (" + updatedName + ")");
		return item;
	}

}
