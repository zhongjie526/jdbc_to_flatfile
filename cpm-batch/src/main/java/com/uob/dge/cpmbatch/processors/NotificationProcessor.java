package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.models.pushnotification.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "batch.processor", havingValue = "NotificationProcessor")
public class NotificationProcessor extends BatchProcessorBase {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public int processImpl() throws IOException, ParseException {
        int returnCode = 0;
        logger.info("Starting Notification");
        if (configuration.getQueries() == null || configuration.getQueries().isEmpty()) {
            logger.error("No queries detected");
            returnCode = 1;
        } else if (configuration.getPushField() == null || StringUtils.isEmpty(configuration.getPushField())) {
            logger.error("No Push field defined");
            returnCode = 1;
        } else if (configuration.getPushCif() == null || StringUtils.isEmpty(configuration.getPushCif())) {
            logger.error("No Push CIF defined");
            returnCode = 1;
        } else if (configuration.getTableName() == null || StringUtils.isEmpty(configuration.getTableName())) {
            logger.error("No table defined");
            returnCode = 1;
        } else if (configuration.getUpdateInstructionsAfterPush() == null || StringUtils.isEmpty(configuration.getUpdateInstructionsAfterPush())) {
            logger.error("No update instruction defined");
            returnCode = 1;
        } else if (configuration.getKeys() == null || configuration.getKeys().isEmpty()) {
            logger.error("No keys defined for update");
            returnCode = 1;
        } else {
            String query = configuration.getQueries().get(0);
            returnCode = processQuery(query);
        }
        logger.info("Ending Notification");
        return returnCode;
    }

    private int processQuery(String query) {
        logger.info("Starting Notification on - {}", query);
        final int[] returnCode = {0};
        final int[] recordCount = {0};
        parameterJdbcTemplate.query(query, new HashMap(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) {
                try {
                    returnCode[0] = returnCode[0] | processItem(convertToMap(resultSet));
                } catch (Exception e) {
                    logger.error("Unable to process item: {}", resultSet);
                    logger.error("Stack", e);
                }
                recordCount[0]++;
            }
        });
        logger.info("Ending Notification on - {}:{}", recordCount[0], query);
        return returnCode[0];
    }

    private int processItem(Map<String, Object> item) {
        String message = (String) item.get(configuration.getPushField());
        String cif = (String) item.get(configuration.getPushCif());
        if (!StringUtils.isEmpty(message) && !StringUtils.isEmpty(cif)) {
            if (sentPush(cif, message)) {
                Map<String, Object> updateParams = new HashMap<>();
                String updateBuilder = prepareUpdateStatement(item, updateParams);
                int count = parameterJdbcTemplate.update(updateBuilder, updateParams);
                if (count != 1) {
                    logger.error("Invalid count for update {}: {}", count, updateBuilder);
                    return 1;
                }
                return 0;
            } else {
                return 1;
            }
        } else {
            logger.error("Missing CIF or Message");
            return 1;
        }
    }

    private boolean sentPush(String cif, String message) {
        try {
            PushNotificationRequest request = prepareRequest(cif, message);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<PushNotificationRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<PushNotificationResponseHeader> responseHeader = restTemplate.postForEntity(String.format("%s/ntn/notification/push", configuration.getPushServiceUrl()), requestEntity, PushNotificationResponseHeader.class);
            if (responseHeader.getStatusCode() == HttpStatus.OK) {
                String responseCode = responseHeader.getBody().getServiceResponseHeader().getResponseContext().getResponseCode();
                if (responseCode != null && responseCode.equals("0000000")) {
                    logger.info("push sent to {} {}", cif, message);
                    return true;
                } else {
                    String responseDesc = responseHeader.getBody().getServiceResponseHeader().getResponseContext().getResponseDescription();
                    logger.error("Dge response error {} {} {} {}", responseCode, responseDesc, cif, message);
                    return false;
                }
            } else {
                logger.error("Can't send push notification request {} {} {} ", responseHeader.getStatusCode(), cif, message);
                return false;
            }
        } catch (Exception ex) {
            logger.error("Unable to push {}: {}", cif, message);
            logger.error("Stack", ex);
            return false;
        }
    }

    private PushNotificationRequest prepareRequest(String cif, String message) throws UnknownHostException {
        RequesterContext requesterContext = new RequesterContext();
        requesterContext.setApplicationCode(configuration.getPushApplicationCode());
        requesterContext.setApplicationSubCode(configuration.getPushApplicationSubCode());
        requesterContext.setCountryCode(configuration.getCountry());
        requesterContext.setAppIPAddress(Inet4Address.getLocalHost().getHostAddress());

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setServiceVersionNumber(configuration.getPushServiceVersion());
        serviceContext.setServiceCode("");

        PushNotificationRequestHeader requestHeader = new PushNotificationRequestHeader(requesterContext, serviceContext);

        CustomerInformation customerInfo = new CustomerInformation(cif);
        PushNotificationRequestBody requestBody = new PushNotificationRequestBody();
        requestBody.setCustomerInformation(customerInfo);
        requestBody.setAlertText(message);
        requestBody.setMessageType("DEVICE");
        requestBody.setAlertTitle("Rewards");
        return new PushNotificationRequest(requestBody, requestHeader);
    }

    private String prepareUpdateStatement(Map<String, Object> item, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("UPDATE %s SET %s ", configuration.getTableName(), configuration.getUpdateInstructionsAfterPush()));
        builder.append(" WHERE ");
        boolean first = true;
        for (String key : configuration.getKeys()) {
            first = addIntoBuilder(builder, first, " AND ", " %s=:%s ", key, params, item.get(key));
        }
        return builder.toString();
    }
}
