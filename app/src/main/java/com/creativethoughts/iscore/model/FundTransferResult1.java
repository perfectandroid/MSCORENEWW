package com.creativethoughts.iscore.model;

public class FundTransferResult1 {
 //   private int statusCode;
    public String refId;
    public String mobileNumber;
    public String amount;
    public String accNo;
    public String statusmsg;





    public FundTransferResult1(String refId, String mobileNumber, String amount, String accNo) {
        this.refId = refId;
        this.mobileNumber = mobileNumber;
        this.amount = amount;
        this.accNo = accNo;
    }

    public FundTransferResult1() {

    }




   /* public int getstatusCode() {
        return statusCode;
    }

    public void setstatusCode(String statusCode) {
        statusCode = statusCode;
    }*/

    public String getrefId() {
        return refId;
    }

    public void setRefId(String refId) {
        refId = refId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        mobileNumber = mobileNumber;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        amount = amount;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        accNo = accNo;
    }

    public String getStatusmsg() {
        return statusmsg;
    }

    public void setStatusmsg(String statusmsg) {
        statusmsg = statusmsg;
    }
}
