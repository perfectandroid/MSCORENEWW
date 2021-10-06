package com.creativethoughts.iscore.neftrtgs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 1/30/2018 - 11:17 AM
 */

public class NeftRtgsOtpResponseModel implements Parcelable {
    @SerializedName("HttpStatusCode")
    private int statusCode;
    @SerializedName("StatusCode")
    private String otpRefNo;
    @SerializedName("Message")
    private String message;
    @SerializedName("ExMessge")
    private String exMessage;
    @SerializedName("RefID")
    private String refId;
    @SerializedName("Amount")
    private String amount;


    public NeftRtgsOtpResponseModel() {

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getOtpRefNo() {
        return otpRefNo;
    }

    public void setOtpRefNo(String otpRefNo) {
        this.otpRefNo = otpRefNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExMessage() {
        return exMessage;
    }

    public void setExMessage(String exMessage) {
        this.exMessage = exMessage;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.statusCode);
        dest.writeString(this.otpRefNo);
        dest.writeString(this.message);
        dest.writeString(this.exMessage);
        dest.writeString(this.refId);
        dest.writeString(this.amount);
    }

    protected NeftRtgsOtpResponseModel(Parcel in) {
        this.statusCode = in.readInt();
        this.otpRefNo = in.readString();
        this.message = in.readString();
        this.exMessage = in.readString();
        this.refId = in.readString();
        this.amount = in.readString();
    }

    public static final Creator<NeftRtgsOtpResponseModel> CREATOR = new Creator<NeftRtgsOtpResponseModel>() {
        @Override
        public NeftRtgsOtpResponseModel createFromParcel(Parcel source) {
            return new NeftRtgsOtpResponseModel(source);
        }

        @Override
        public NeftRtgsOtpResponseModel[] newArray(int size) {
            return new NeftRtgsOtpResponseModel[size];
        }
    };
}
