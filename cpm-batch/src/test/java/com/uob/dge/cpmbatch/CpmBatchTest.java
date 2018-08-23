package com.uob.dge.cpmbatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CpmBatchTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testHouseKeepingWiring() {
        testImpl("test,base,cleanup", "CleanupProcessor");
    }

    @Test
    public void testImportWiring() {
        testImpl("test,base,import", "ImportProcessor");
    }

    @Test
    public void testExportWiring() {
        testImpl("test,base,export", "ExportProcessor");
    }

    @Test
    public void testPushWiring() {
        testImpl("test,base,push", "NotificationProcessor");
    }

    private void testImpl(String profiles, String expectedProcessor) {
        System.setProperty("spring.profiles.active", profiles);
        CpmBatch.main(new String[]{"Testing"});
        String output = this.outputCapture.toString();
        assertThat(output).contains(expectedProcessor);
    }
}