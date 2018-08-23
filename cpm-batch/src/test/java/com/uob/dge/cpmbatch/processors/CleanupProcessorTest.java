package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.CpmBatchTestConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmBatchTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test,base,cleanup")
public class CleanupProcessorTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    private CleanupProcessor cleanupProcessor;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testCleanup() {
        int returnCode = cleanupProcessor.processImpl();
        Assert.assertEquals(0, returnCode);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(cleanupProcessor.configuration.getQueries().size())).update(argument.capture(), Mockito.any(Map.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(cleanupProcessor.configuration.getQueries().get(i), calls.get(i));
        }
    }

    @Test
    public void testCleanupNoQueries() {
        List<String> queries = cleanupProcessor.configuration.getQueries();
        cleanupProcessor.configuration.setQueries(new ArrayList<String>());
        int returnCode = cleanupProcessor.processImpl();
        cleanupProcessor.configuration.setQueries(queries);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No queries detected"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).update(Mockito.any(String.class), Mockito.any(Map.class));
    }

}