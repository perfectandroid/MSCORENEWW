package com.creativethoughts.iscore.model;

public class FundTransferResult1 {
 //   private int statusCode;
    public String refId;
    public String mobileNumber;
    public String amount;
    public String accNo;
    public String statusmsg;
    public String senderacc;
    public String senderbranch;
    public String recvrbranch;
    public String recvrdate;
    public String recvraccno;






    public FundTransferResult1(String refId, String mobileNumber, String amount, String accNo,String senderacc,String senderbranch,
                               String recvrbranch,String recvrdate,String recvraccno) {
        this.refId = refId;
        this.mobileNumber = mobileNumber;
        this.amount = amount;
        this.accNo = accNo;
        this.senderacc = senderacc;
        this.senderbranch = senderbranch;
        this.recvrbranch = recvrbranch;
        this.recvrdate = recvrdate;
        this.recvraccno = recvraccno;
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

    public String getSenderacc() {
        return senderacc;
    }

    public void setSenderacc(String senderacc) {
        senderacc = senderacc;
    }

    public String getSenderbranch() {
        return senderbranch;
    }

    public void setSenderbranch(String senderbranch) {
        senderbranch = senderbranch;
    }

    public String getRecvrbranch() {
        return recvrbranch;
    }

    public void setRecvrbranch(String recvrbranch) {
        recvrbranch = recvrbranch;
    }

    public String getRecvrdate() {
        return recvrdate;
    }

    public void setRecvrdate(String recvrdate) {
        recvrdate = recvrdate;
    }

    public String getRecvraccno() {
        return recvraccno;
    }

    public void setRecvraccno(String recvraccno) {
        recvraccno = recvraccno;
    }
}
