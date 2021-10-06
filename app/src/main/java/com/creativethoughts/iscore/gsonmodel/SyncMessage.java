package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 7/23/2018 - 2:44 PM.
 */
public class SyncMessage {
    @SerializedName("messageId")
    private int messageId;

    @SerializedName("messageHead")
    private String messageHead;

    @SerializedName("messageDetail")
    private String messageDetail;

    @SerializedName("messageDate")
    private String messageDate;

    @SerializedName("messageType")
    private int messageType;

    @SerializedName("messageMode")
    private int messageMode;

    public int getMessageId() {
        return messageId;
    }

    public String getMessageHead() {
        return messageHead;
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageMode() {
        return messageMode;
    }
}
