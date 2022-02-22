package com.creativethoughts.iscore.model;

/**
 * Created by muthukrishnan on 10/08/16
 */
public class AddSenderReceiverResponseModel {
    public String statusCode;
    public String status;
    public String senderid;
    public String receiverid;
    public String otprefno;
    public String message;
    public String mobileno;


    public AddSenderReceiverResponseModel() {

    }


    public String getStatusCode() {

        return statusCode;
    }

    public void setStatusCode(String statusCode) {

        statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        status = status;
    }


    public String getOtprefno() {
        return otprefno;
    }

    public void setOtprefno(String otprefno) {
        otprefno = otprefno;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = message;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        mobileno = mobileno;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        receiverid = receiverid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        senderid = senderid;
    }


}
