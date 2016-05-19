package com.ydu.springbatch;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;

public class DemoBatchApplicationTests {

	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	@Test
	public void testDefaultSettings() throws Exception {
		String[] strs = {"blah", "hey", "yo"};
//		assertThat(SpringApplication
//				.exit(SpringApplication.run(DemoBatchApplication.class))).isEqualTo(0);
		assertThat(SpringApplication.run(DemoBatchApplication.class, strs)).isEqualTo(0);
		String output = this.outputCapture.toString();
		assertThat(output).contains("completed with the following parameters");
	}

}
