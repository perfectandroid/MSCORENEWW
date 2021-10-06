package com.creativethoughts.iscore.money_transfer;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 11/28/2017 - 11:47 AM.
 */

public class  MoneyTransferResponseModel {
    @SerializedName( "StatusCode" )
    private String statusCode;

    @SerializedName( "Status" )
    private String status;
    @SerializedName( "TrasactionID" )
    private String transactionId;
    @SerializedName( "otpRefNo" )
    private String otpRefNo;
    @SerializedName( "message" )
    private String message;
    private String mAccNo,msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch,mAmount;

    public String getmAccNo() {
        return mAccNo;
    }

    public void setmAccNo(String mAccNo) {
        this.mAccNo = mAccNo;
    }

    public String getMsenderName() {
        return msenderName;
    }

    public void setMsenderName(String msenderName) {
        this.msenderName = msenderName;
    }

    public String getMsenderMobile() {
        return msenderMobile;
    }

    public void setMsenderMobile(String msenderMobile) {
        this.msenderMobile = msenderMobile;
    }

    public String getMreceiverAccountno() {
        return mreceiverAccountno;
    }

    public void setMreceiverAccountno(String mreceiverAccountno) {
        this.mreceiverAccountno = mreceiverAccountno;
    }
    public String getMrecieverMobile() {
        return mrecieverMobile;
    }

    public void setMrecieverMobile(String mrecieverMobile) {
        this.mrecieverMobile = mrecieverMobile;
    }
    public String getMbranch() {
        return mbranch;
    }

    public void setMbranch(String mbranch) {
        this.mbranch = mbranch;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }


    public String getMrecievererName() {
        return mrecievererName;
    }

    public void setMrecievererName(String mrecievererName) {
        this.mrecievererName = mrecievererName;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public String getOtpRefNo() {
        return otpRefNo;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
