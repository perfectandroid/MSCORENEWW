package com.creativethoughts.iscore.model;

public class Receivers {



    public String senderName;
    public String receiverAccountno;
    public long receiverid;
    public String receivermobile;

        public Receivers(String senderName, String receiverAccountno,long receiverid,String receivermobile) {

            this.senderName = senderName;
            this.receiverAccountno = receiverAccountno;
            this.receiverid = receiverid;
            this.receivermobile = receivermobile;



        }


        public String getSenderName() {
        return senderName;
    }

        public void setSenderName(String senderName) {

            this.senderName = senderName;
    }


    public String getReceiverAccountno()
    {
        return receiverAccountno;
    }

    public void setReceiverAccountno(String receiverAccountno) {

        this.receiverAccountno = receiverAccountno;
    }
    public long getReceiverid()
    {
        return receiverid;
    }

    public void setReceiverid(long receiverid) {

        this.receiverid = receiverid;
    }
    public String getReceivermobile()
    {
        return receivermobile;
    }

    public void setReceivermobile(String receivermobile) {

        this.receivermobile = receivermobile;
    }


    @Override
    public String toString() {
        return senderName+"("+receiverAccountno+")";
    }




}
