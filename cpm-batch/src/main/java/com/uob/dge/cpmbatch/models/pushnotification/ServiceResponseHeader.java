package com.uob.dge.cpmbatch.models.pushnotification;

public class ServiceResponseHeader {

    private ResponseContext responseContext;
    private RequesterContext requesterContext;
    private ServiceContext serviceContext;

    public ResponseContext getResponseContext() {
        return responseContext;
    }

    public void setResponseContext(ResponseContext responseContext) {
        this.responseContext = responseContext;
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
