package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.CpmBatchTestConfiguration;
import com.uob.dge.cpmbatch.models.InputOutputColumn;
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
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmBatchTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test,base,import")
public class ImportProcessorTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    private ImportProcessor importProcessor;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private String expectedInserts = "INSERT INTO TPIB_CAMPAIGN_TARGETTED (  ID  ,  CIF_NUMBER  ,  CPM_CODE  ,  CPM_INCENTIVE_VALUE  ,  CPM_INCENTIVE_TYPE  ,  CPM_START  ,  CPM_END  ) values ( SQ_PIB_CAMPAIGN_TARGETTED.nextval  ,  :CIF_NUMBER  ,  :CPM_CODE  ,  :CPM_INCENTIVE_VALUE  ,  :CPM_INCENTIVE_TYPE  ,  :CPM_START  ,  :CPM_END  )";
    private String expectedUpdates = "UPDATE TPIB_CAMPAIGN_TARGETTED SET   CPM_INCENTIVE_VALUE=:CPM_INCENTIVE_VALUE  ,  CPM_INCENTIVE_TYPE=:CPM_INCENTIVE_TYPE  ,  CPM_START=:CPM_START  ,  CPM_END=:CPM_END  WHERE  CIF_NUMBER=:CIF_NUMBER  AND  CPM_CODE=:CPM_CODE ";

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void testImportNoFile() throws IOException, ParseException {
        String fileName = importProcessor.configuration.getFileName();
        importProcessor.configuration.setFileName("");
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setFileName(fileName);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No Filename defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoTable() throws IOException, ParseException {
        String tableName = importProcessor.configuration.getTableName();
        importProcessor.configuration.setTableName("");
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setTableName(tableName);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No table defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoHeaders() throws IOException, ParseException {
        List<InputOutputColumn> columns = importProcessor.configuration.getHeaders();
        importProcessor.configuration.setHeaders(new ArrayList<InputOutputColumn>());
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setHeaders(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - headers defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoBodies() throws IOException, ParseException {
        List<InputOutputColumn> columns = importProcessor.configuration.getBodies();
        importProcessor.configuration.setBodies(new ArrayList<InputOutputColumn>());
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setBodies(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - bodies defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoFooters() throws IOException, ParseException {
        List<InputOutputColumn> columns = importProcessor.configuration.getFooters();
        importProcessor.configuration.setFooters(new ArrayList<InputOutputColumn>());
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setFooters(columns);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - footers defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoKeys() throws IOException, ParseException {
        List<String> keys = importProcessor.configuration.getKeys();
        importProcessor.configuration.setKeys(new ArrayList<String>());
        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setKeys(keys);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No File structure - keys defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportNoFileAvailable() throws IOException, ParseException {
        int returnCode = importProcessor.processImpl();
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains(" not available"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testImportInsert() throws IOException, ParseException {
        Mockito.when(namedParameterJdbcTemplate.queryForObject(Mockito.eq("SELECT count(*) FROM TPIB_CAMPAIGN_TARGETTED WHERE   CIF_NUMBER=:CIF_NUMBER  AND  CPM_CODE=:CPM_CODE "), Mockito.any(Map.class), Mockito.eq(Integer.class))).thenReturn(0);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> argumentQuery = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        String inputFolder = importProcessor.configuration.getInputFolder();
        importProcessor.configuration.setInputFolder("src/test/resources");

        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setInputFolder(inputFolder);
        Assert.assertEquals(0, returnCode);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(2)).update(argument.capture(), argumentMap.capture());
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(2)).queryForObject(Mockito.anyString(), argumentQuery.capture(), Mockito.eq(Integer.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(expectedInserts, calls.get(i));
        }

        List<Map> callsMap = argumentMap.getAllValues();
        checkValueMap(callsMap.get(0), "CIF111121", "C0003839", 1.0, "AMOUNT", "20180601", "20180729");
        checkValueMap(callsMap.get(1), "CIF111122", "C0003839", 2.0, "PERCENTAGE", "20180601", "20180829");

        List<Map> callsQuery = argumentQuery.getAllValues();
        checkValueMap(callsQuery.get(0), "CIF111121", "C0003839");
        checkValueMap(callsQuery.get(1), "CIF111122", "C0003839");
    }

    @Test
    public void testImportUpdate() throws IOException, ParseException {
        Mockito.when(namedParameterJdbcTemplate.queryForObject(Mockito.eq("SELECT count(*) FROM TPIB_CAMPAIGN_TARGETTED WHERE   CIF_NUMBER=:CIF_NUMBER  AND  CPM_CODE=:CPM_CODE "), Mockito.any(Map.class), Mockito.eq(Integer.class))).thenReturn(1);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> argumentQuery = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        String inputFolder = importProcessor.configuration.getInputFolder();
        importProcessor.configuration.setInputFolder("src/test/resources");

        int returnCode = importProcessor.processImpl();
        importProcessor.configuration.setInputFolder(inputFolder);
        Assert.assertEquals(0, returnCode);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(2)).update(argument.capture(), argumentMap.capture());
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(2)).queryForObject(Mockito.anyString(), argumentQuery.capture(), Mockito.eq(Integer.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(expectedUpdates, calls.get(i));
        }

        List<Map> callsMap = argumentMap.getAllValues();
        checkValueMap(callsMap.get(0), "CIF111121", "C0003839", 1.0, "AMOUNT", "20180601", "20180729");
        checkValueMap(callsMap.get(1), "CIF111122", "C0003839", 2.0, "PERCENTAGE", "20180601", "20180829");

        List<Map> callsQuery = argumentQuery.getAllValues();
        checkValueMap(callsQuery.get(0), "CIF111121", "C0003839");
        checkValueMap(callsQuery.get(1), "CIF111122", "C0003839");
    }

    private void checkValueMap(Map<String, Object> items, String cif, String cpm, double value, String type, String start, String end) {
        checkValueMap(items, cif, cpm);
        Assert.assertEquals(value, items.get("CPM_INCENTIVE_VALUE"));
        Assert.assertEquals(type, items.get("CPM_INCENTIVE_TYPE"));
        Assert.assertEquals(start, format.format((Date) items.get("CPM_START")));
        Assert.assertEquals(end, format.format((Date) items.get("CPM_END")));
    }

    private void checkValueMap(Map<String, Object> items, String cif, String cpm) {
        Assert.assertEquals(cif, items.get("CIF_NUMBER"));
        Assert.assertEquals(cpm, items.get("CPM_CODE"));
    }
}