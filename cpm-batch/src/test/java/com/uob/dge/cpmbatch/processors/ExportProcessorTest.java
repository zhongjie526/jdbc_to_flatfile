package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.CpmBatchTestConfiguration;
import com.uob.dge.cpmbatch.models.InputOutputColumn;
import oracle.sql.TIMESTAMP;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmBatchTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test,base,export")
public class ExportProcessorTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    private ExportProcessor exportProcessor;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private ResultSet resultSet;

    @MockBean
    private ResultSetMetaData resultSetMetaData;

    private String[] columnNames = new String[]{"CIF_NUMBER", "CPM_CODE", "CPM_INCENTIVE_VALUE", "CPM_INCENTIVE_TYPE", "CPM_START", "CPM_END", "CPM_DELIVERED", "CPM_CLICK", "CPM_CLAIM", "CPM_NOTIFIED"};
    private Object[] columnValues = new Object[]{"CIF_NUMBER", "CPM_CODE", 5.0, "AMOUNT", new TIMESTAMP(new java.sql.Date(120, 02, 02)), new TIMESTAMP(new java.sql.Date(120, 03, 02)), 1, 1, 1, 1};

    @Test
    public void testExportNoQueries() throws IOException {
        List<String> queries = exportProcessor.configuration.getQueries();
        exportProcessor.configuration.setQueries(new ArrayList<String>());
        int returnCode = exportProcessor.processImpl();
        exportProcessor.configuration.setQueries(queries);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No queries detected"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testExportNoFile() throws IOException {
        String fileName = exportProcessor.configuration.getFileName();
        exportProcessor.configuration.setFileName("");
        int returnCode = exportProcessor.processImpl();
        exportProcessor.configuration.setFileName(fileName);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No Filename defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testExportNoHeaders() throws IOException {
        List<InputOutputColumn> columns = exportProcessor.configuration.getHeaders();
        exportProcessor.configuration.setHeaders(new ArrayList<InputOutputColumn>());
        int returnCode = exportProcessor.processImpl();
        exportProcessor.configuration.setHeaders(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - headers defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testExportNoBodies() throws IOException {
        List<InputOutputColumn> columns = exportProcessor.configuration.getBodies();
        exportProcessor.configuration.setBodies(new ArrayList<InputOutputColumn>());
        int returnCode = exportProcessor.processImpl();
        exportProcessor.configuration.setBodies(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - bodies defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testExportNoFooters() throws IOException {
        List<InputOutputColumn> columns = exportProcessor.configuration.getFooters();
        exportProcessor.configuration.setFooters(new ArrayList<InputOutputColumn>());
        int returnCode = exportProcessor.processImpl();
        exportProcessor.configuration.setFooters(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - footers defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testExport() throws IOException, SQLException {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((RowCallbackHandler) invocationOnMock.getArguments()[2]).processRow(resultSet);
                return null;
            }
        }).when(namedParameterJdbcTemplate).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));

        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(columnNames.length);
        for (int i = 0; i < columnNames.length; i++) {
            int pos = i + 1;
            Mockito.when(resultSetMetaData.getColumnName(pos)).thenReturn(columnNames[i]);
            Mockito.when(resultSet.getObject(pos)).thenReturn(columnValues[i]);
        }
        int returnCode = exportProcessor.processImpl();
        Assert.assertEquals(0, returnCode);

        File outputFile = new File(exportProcessor.configuration.getOutputFolder(), exportProcessor.configuration.getFileName());
        File fullOutput = new File(outputFile.getAbsolutePath());
        Assert.assertTrue(fullOutput.exists());

        try (BufferedReader output = new BufferedReader(new FileReader(fullOutput.getAbsolutePath()))) {
            String line;
            int lineCount = 0;
            while ((line = output.readLine()) != null) {
                if (lineCount == 0) {
                    Assert.assertTrue(line.startsWith("H\u0007ICV\u0007TH\u0007"));
                } else if (lineCount == 1) {
                    Assert.assertTrue(line.equalsIgnoreCase("D\u0007CIF_NUMBER\u0007CPM_CODE\u00075.00000\u0007AMOUNT\u000720200302\u000720200402\u00071\u00071\u00071\u00071"));
                } else if (lineCount == 2) {
                    Assert.assertTrue(line.startsWith("T\u00071\u00071"));
                }
                lineCount++;

            }
        }
    }
}