package com.uob.dge.cpmbatch.models.pushnotification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerInformation {

    @JsonProperty("CIFNumber")
    private String CIFNumber;

    public CustomerInformation() {
    }

    public CustomerInformation(String CIFNumber) {
        this.CIFNumber = CIFNumber;
    }

    @JsonProperty("CIFNumber")
    public String getCIFNumber() {
        return CIFNumber;
    }

    @JsonProperty("CIFNumber")
    public void setCIFNumber(String CIFNumber) {
        this.CIFNumber = CIFNumber;
    }
}
