package com.creativethoughts.iscore.model;

/**
 * Created by muthukrishnan on 10/08/16
 */
public class SenderReceiver {
    public long userId;
    public long fkSenderId;
    public String senderName;
    public String senderMobile;
    public String receiverAccountno;
    public int mode;
    public int checkError;


    public SenderReceiver(long userId,long fkSenderId,String senderName,String senderMobile,String receiverAccountno,int mode) {
        this.userId = userId;
        this.fkSenderId = fkSenderId;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.receiverAccountno = receiverAccountno;
        this.mode = mode;
    }


    public long getUserID() {

        return userId;
    }

    public void setUserId(String userId) {

        userId = userId;
    }

    public long getFkSenderId() {
        return fkSenderId;
    }

    public void setFkSenderId(String fkSenderId) {
        fkSenderId = fkSenderId;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        senderName = senderName;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        senderMobile = senderMobile;
    }

    public String getReceiverAccountno() {
        return receiverAccountno;
    }

    public void setReceiverAccountno(String receiverAccountno) {
        receiverAccountno = receiverAccountno;
    }
    public int getMode() {
        return mode;
    }

    public void setMode(String mode) {
        mode = mode;
    }

    @Override
    public String toString() {
        return senderName+"("+senderMobile+")";
    }
}
