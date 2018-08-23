package com.uob.meniga;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BatchTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes={ BatchConfiguration.class})
@ActiveProfiles("test,tables,transactions")
public class BatchConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJob() throws Exception {
        org.springframework.batch.core.JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals("COMPLETED", jobExecution.getStatus().name());
        File outputFile = new File ("target/MenigaExtractor/outputFiles", "transactions.csv");
        Assert.assertTrue(outputFile.exists());
        try (BufferedReader output = new BufferedReader(new FileReader(outputFile.getAbsolutePath()))) {
            String line;
            int lineCount = 0;
            while ((line = output.readLine()) != null) {
                if (lineCount == 0) {
                    Assert.assertTrue(line.startsWith("H\u0007ADV\u0007TH\u0007"));
                } else if (lineCount == 1) {
                    Assert.assertTrue(line.equalsIgnoreCase("D\u0007pid\u0007pname\u00072.3000\u0007pdesc"));
                } else if (lineCount == 2) {
                    Assert.assertTrue(line.equalsIgnoreCase("D\u0007pid1\u0007pname1\u00073.4000\u0007pdesc1"));
                } else if (lineCount == 3) {
                    Assert.assertTrue(line.startsWith("T\u00072\u00075.7000"));
                }
                lineCount++;

            }
        }
    }
}
