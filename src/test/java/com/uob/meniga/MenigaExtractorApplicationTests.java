package com.uob.meniga;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tables,transactions")
public class MenigaExtractorApplicationTests {
	
	//@Rule
    //public OutputCapture outputCapture = new OutputCapture();

	@Test
	public void contextLoads() {}
	

	

}
