package com.creativethoughts.iscore.model;

public class Receivers {



    public String senderName;
    public String receiverAccountno;


        public Receivers(String senderName, String receiverAccountno) {

            this.senderName = senderName;
            this.receiverAccountno = receiverAccountno;


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


    @Override
    public String toString() {
        return senderName+"("+receiverAccountno+")";
    }




}
