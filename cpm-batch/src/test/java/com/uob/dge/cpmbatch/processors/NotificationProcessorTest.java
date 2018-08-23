package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.CpmBatchTestConfiguration;
import com.uob.dge.cpmbatch.models.pushnotification.PushNotificationRequest;
import com.uob.dge.cpmbatch.models.pushnotification.PushNotificationResponseHeader;
import com.uob.dge.cpmbatch.models.pushnotification.ResponseContext;
import com.uob.dge.cpmbatch.models.pushnotification.ServiceResponseHeader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmBatchTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test,base,push")
public class NotificationProcessorTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    private NotificationProcessor notificationProcessor;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ResultSet resultSet;

    @MockBean
    private ResultSetMetaData resultSetMetaData;

    @MockBean
    private ResponseEntity<PushNotificationResponseHeader> responseEntity;

    private String[] columnNames = new String[]{"CIF_NUMBER", "CPM_CODE", "CPM_PUSH_MESSAGE"};
    private Object[] columnValues = new Object[]{"CIF_NUMBER", "CPM_CODE", "CPM_PUSH_MESSAGE"};


    @Test
    public void testNotificationNoQueries() throws IOException, ParseException {
        List<String> queries = notificationProcessor.configuration.getQueries();
        notificationProcessor.configuration.setQueries(new ArrayList<String>());
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setQueries(queries);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No queries detected"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotificationNoKeys() throws IOException, ParseException {
        List<String> keys = notificationProcessor.configuration.getKeys();
        notificationProcessor.configuration.setKeys(new ArrayList<String>());
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setKeys(keys);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No keys defined for update"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotificationNoTable() throws IOException, ParseException {
        String tableName = notificationProcessor.configuration.getTableName();
        notificationProcessor.configuration.setTableName("");
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setTableName(tableName);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No table defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotificationNoPushCif() throws IOException, ParseException {
        String cif = notificationProcessor.configuration.getPushCif();
        notificationProcessor.configuration.setPushCif("");
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setPushCif(cif);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No Push CIF defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotificationNoPushField() throws IOException, ParseException {
        String field = notificationProcessor.configuration.getPushField();
        notificationProcessor.configuration.setPushField("");
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setPushField(field);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No Push field defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotificationNoPushUpdate() throws IOException, ParseException {
        String field = notificationProcessor.configuration.getUpdateInstructionsAfterPush();
        notificationProcessor.configuration.setUpdateInstructionsAfterPush("");
        int returnCode = notificationProcessor.processImpl();
        notificationProcessor.configuration.setUpdateInstructionsAfterPush(field);
        Assert.assertEquals(1, returnCode);
        Assert.assertTrue(outputCapture.toString().contains("No update instruction defined"));
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }

    @Test
    public void testNotification() throws SQLException, IOException, ParseException {
        setupNotificationMock(false, false, false, false);

        int returnCode = notificationProcessor.processImpl();

        Assert.assertEquals(0, returnCode);

        //verify query
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(notificationProcessor.configuration.getQueries().size())).query(argument.capture(), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(notificationProcessor.configuration.getQueries().get(i), calls.get(i));
        }

        //verify update
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(1)).update(Mockito.eq("UPDATE TPIB_CAMPAIGN_TARGETTED SET CPM_NOTIFIED = 1  WHERE  CIF_NUMBER=:CIF_NUMBER  AND  CPM_CODE=:CPM_CODE "), argumentMap.capture());
        List<Map> callsMap = argumentMap.getAllValues();
        checkValueMap(callsMap.get(0), (String) columnValues[0], (String) columnValues[1]);
        String messageConsole = outputCapture.toString();
        Assert.assertFalse(messageConsole.contains("Invalid count for update"));

        //verify send push
        ArgumentCaptor<HttpEntity<PushNotificationRequest>> argumentHttp = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentHttp.capture(), Mockito.eq(PushNotificationResponseHeader.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getAccept().get(0));
        Assert.assertEquals(columnValues[0], argumentHttp.getValue().getBody().getServiceRequestBody().getCustomerInformation().getCIFNumber());
        Assert.assertEquals(columnValues[2], argumentHttp.getValue().getBody().getServiceRequestBody().getAlertText());
    }

    @Test
    public void testNotificationUpdateFail() throws SQLException, IOException, ParseException {
        setupNotificationMock(true, false, false, false);

        int returnCode = notificationProcessor.processImpl();

        Assert.assertEquals(1, returnCode);

        //verify query
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(notificationProcessor.configuration.getQueries().size())).query(argument.capture(), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(notificationProcessor.configuration.getQueries().get(i), calls.get(i));
        }

        //verify update
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(1)).update(Mockito.eq("UPDATE TPIB_CAMPAIGN_TARGETTED SET CPM_NOTIFIED = 1  WHERE  CIF_NUMBER=:CIF_NUMBER  AND  CPM_CODE=:CPM_CODE "), argumentMap.capture());
        List<Map> callsMap = argumentMap.getAllValues();
        checkValueMap(callsMap.get(0), (String) columnValues[0], (String) columnValues[1]);
        String messageConsole = outputCapture.toString();
        Assert.assertTrue(messageConsole.contains("Invalid count for update"));

        //verify send push
        ArgumentCaptor<HttpEntity<PushNotificationRequest>> argumentHttp = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentHttp.capture(), Mockito.eq(PushNotificationResponseHeader.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getAccept().get(0));
        Assert.assertEquals(columnValues[0], argumentHttp.getValue().getBody().getServiceRequestBody().getCustomerInformation().getCIFNumber());
        Assert.assertEquals(columnValues[2], argumentHttp.getValue().getBody().getServiceRequestBody().getAlertText());
    }

    @Test
    public void testNotificationSendPushError() throws SQLException, IOException, ParseException {
        setupNotificationMock(false, true, false, false);

        int returnCode = notificationProcessor.processImpl();

        Assert.assertEquals(1, returnCode);

        //verify query
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(notificationProcessor.configuration.getQueries().size())).query(argument.capture(), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(notificationProcessor.configuration.getQueries().get(i), calls.get(i));
        }

        //verify update
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).update(Mockito.anyString(), Mockito.<String, Object>anyMap());

        //verify send push
        ArgumentCaptor<HttpEntity<PushNotificationRequest>> argumentHttp = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentHttp.capture(), Mockito.eq(PushNotificationResponseHeader.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getAccept().get(0));
        Assert.assertEquals(columnValues[0], argumentHttp.getValue().getBody().getServiceRequestBody().getCustomerInformation().getCIFNumber());
        Assert.assertEquals(columnValues[2], argumentHttp.getValue().getBody().getServiceRequestBody().getAlertText());
    }

    @Test
    public void testNotificationSendPushHttpError() throws SQLException, IOException, ParseException {
        setupNotificationMock(false, false, true, false);

        int returnCode = notificationProcessor.processImpl();

        Assert.assertEquals(1, returnCode);

        //verify query
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(notificationProcessor.configuration.getQueries().size())).query(argument.capture(), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(notificationProcessor.configuration.getQueries().get(i), calls.get(i));
        }

        //verify update
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).update(Mockito.anyString(), Mockito.<String, Object>anyMap());

        //verify send push
        ArgumentCaptor<HttpEntity<PushNotificationRequest>> argumentHttp = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentHttp.capture(), Mockito.eq(PushNotificationResponseHeader.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getAccept().get(0));
        Assert.assertEquals(columnValues[0], argumentHttp.getValue().getBody().getServiceRequestBody().getCustomerInformation().getCIFNumber());
        Assert.assertEquals(columnValues[2], argumentHttp.getValue().getBody().getServiceRequestBody().getAlertText());
    }

    @Test
    public void testNotificationSendPushException() throws SQLException, IOException, ParseException {
        setupNotificationMock(false, false, false, true);

        int returnCode = notificationProcessor.processImpl();

        Assert.assertEquals(1, returnCode);

        //verify query
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(notificationProcessor.configuration.getQueries().size())).query(argument.capture(), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
        List<String> calls = argument.getAllValues();
        for (int i = 0; i < calls.size(); i++) {
            Assert.assertEquals(notificationProcessor.configuration.getQueries().get(i), calls.get(i));
        }

        //verify update
        ArgumentCaptor<Map> argumentMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).update(Mockito.anyString(), Mockito.<String, Object>anyMap());

        //verify send push
        ArgumentCaptor<HttpEntity<PushNotificationRequest>> argumentHttp = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentHttp.capture(), Mockito.eq(PushNotificationResponseHeader.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, argumentHttp.getValue().getHeaders().getAccept().get(0));
        Assert.assertEquals(columnValues[0], argumentHttp.getValue().getBody().getServiceRequestBody().getCustomerInformation().getCIFNumber());
        Assert.assertEquals(columnValues[2], argumentHttp.getValue().getBody().getServiceRequestBody().getAlertText());
    }


    private void setupNotificationMock(boolean updateFail, boolean sentPushFail, boolean sentPushHttpError, boolean sentPushException) throws SQLException {
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
        if (sentPushException) {
            Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), Mockito.eq(PushNotificationResponseHeader.class))).thenThrow(new RuntimeException("ERROR"));
        } else {
            Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), Mockito.eq(PushNotificationResponseHeader.class))).thenReturn(responseEntity);
        }
        if (sentPushHttpError) {
            Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
        } else {
            Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        }
        ServiceResponseHeader header = new ServiceResponseHeader();
        ResponseContext context = new ResponseContext();
        header.setResponseContext(context);
        if (sentPushFail) {
            context.setResponseCode("111111");
        } else {
            context.setResponseCode("0000000");
        }
        PushNotificationResponseHeader responseHeader = new PushNotificationResponseHeader();
        responseHeader.setServiceResponseHeader(header);
        Mockito.when(responseEntity.getBody()).thenReturn(responseHeader);

        if (updateFail) {
            Mockito.when(namedParameterJdbcTemplate.update(Mockito.anyString(), Mockito.<String, Object>anyMap())).thenReturn(0);
        } else {
            Mockito.when(namedParameterJdbcTemplate.update(Mockito.anyString(), Mockito.<String, Object>anyMap())).thenReturn(1);
        }
    }

    private void checkValueMap(Map<String, Object> items, String cif, String cpm) {
        Assert.assertEquals(cif, items.get("CIF_NUMBER"));
        Assert.assertEquals(cpm, items.get("CPM_CODE"));
    }
}