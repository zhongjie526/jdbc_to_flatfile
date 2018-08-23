package com.uob.dge.cpmbatch.models.pushnotification;

public class RequesterContext {

    private String applicationCode;
    private String applicationSubCode;
    private String countryCode;
    private String appIPAddress;
    private String appIPAddressPortNumber;

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getApplicationSubCode() {
        return applicationSubCode;
    }

    public void setApplicationSubCode(String applicationSubCode) {
        this.applicationSubCode = applicationSubCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAppIPAddress() {
        return appIPAddress;
    }

    public void setAppIPAddress(String appIPAddress) {
        this.appIPAddress = appIPAddress;
    }

    public String getAppIPAddressPortNumber() {
        return appIPAddressPortNumber;
    }

    public void setAppIPAddressPortNumber(String appIPAddressPortNumber) {
        this.appIPAddressPortNumber = appIPAddressPortNumber;
    }
}
