package com.uob.dge.cpmbatch.models.pushnotification;

public class PushNotificationRequest {

    private PushNotificationRequestBody serviceRequestBody;
    private PushNotificationRequestHeader serviceRequestHeader;

    public PushNotificationRequest() {
    }

    public PushNotificationRequest(PushNotificationRequestBody serviceRequestBody, PushNotificationRequestHeader serviceRequestHeader) {
        this.serviceRequestBody = serviceRequestBody;
        this.serviceRequestHeader = serviceRequestHeader;
    }

    public PushNotificationRequestBody getServiceRequestBody() {
        return serviceRequestBody;
    }

    public void setServiceRequestBody(PushNotificationRequestBody serviceRequestBody) {
        this.serviceRequestBody = serviceRequestBody;
    }

    public PushNotificationRequestHeader getServiceRequestHeader() {
        return serviceRequestHeader;
    }

    public void setServiceRequestHeader(PushNotificationRequestHeader serviceRequestHeader) {
        this.serviceRequestHeader = serviceRequestHeader;
    }
}
