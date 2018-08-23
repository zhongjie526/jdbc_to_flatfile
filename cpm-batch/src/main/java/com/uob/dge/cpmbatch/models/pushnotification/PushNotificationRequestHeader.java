package com.uob.dge.cpmbatch.models.pushnotification;

public class PushNotificationRequestHeader {

    private RequesterContext requesterContext;
    private ServiceContext serviceContext;

    public PushNotificationRequestHeader() {
    }

    public PushNotificationRequestHeader(RequesterContext requesterContext, ServiceContext serviceContext) {
        this.requesterContext = requesterContext;
        this.serviceContext = serviceContext;
    }

    public RequesterContext getRequesterContext() {
        return requesterContext;
    }

    public void setRequesterContext(RequesterContext requesterContext) {
        this.requesterContext = requesterContext;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }
}
