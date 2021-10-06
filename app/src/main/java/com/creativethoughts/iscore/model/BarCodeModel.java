package com.creativethoughts.iscore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BarCodeModel {

    @SerializedName("BarcodeFormatDet")
    @Expose
    private BarcodeFormatDet barcodeFormatDet;
    @SerializedName("StatusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("EXMessage")
    @Expose
    private Object eXMessage;

    public BarcodeFormatDet getBarcodeFormatDet() {
        return barcodeFormatDet;
    }

    public void setBarcodeFormatDet(BarcodeFormatDet barcodeFormatDet) {
        this.barcodeFormatDet = barcodeFormatDet;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Object getEXMessage() {
        return eXMessage;
    }

    public void setEXMessage(Object eXMessage) {
        this.eXMessage = eXMessage;
    }

}