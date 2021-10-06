package com.creativethoughts.iscore.money_transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 11/27/2017 - 12:09 PM
 */

public class AddSenderReceiverResponseModel implements Parcelable {

    public static final Creator<AddSenderReceiverResponseModel> CREATOR = new Creator<AddSenderReceiverResponseModel>() {
        @Override
        public AddSenderReceiverResponseModel createFromParcel(Parcel source) {
            return new AddSenderReceiverResponseModel(source);
        }

        @Override
        public AddSenderReceiverResponseModel[] newArray(int size) {
            return new AddSenderReceiverResponseModel[size];
        }
    };

    @SerializedName( "StatusCode" )
    private String statusCode;

    @SerializedName( "Status" )
    private String status;

    @SerializedName( "ID_Sender" )
    private String idSender;

    @SerializedName( "ID_Receiver" )
    private String idReceiver;

    @SerializedName( "otpRefNo" )
    private String otpRefNo;

    @SerializedName( "message" )
    private String message;

    private String mobileNo;

    public AddSenderReceiverResponseModel() {
    }

    protected AddSenderReceiverResponseModel(Parcel in) {
        this.statusCode = in.readString();
        this.status = in.readString();
        this.idSender = in.readString();
        this.idReceiver = in.readString();
        this.otpRefNo = in.readString();
        this.message = in.readString();
        this.mobileNo = in.readString();
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getIdSender() {
        return idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.statusCode);
        dest.writeString(this.status);
        dest.writeString(this.idSender);
        dest.writeString(this.idReceiver);
        dest.writeString(this.otpRefNo);
        dest.writeString(this.message);
        dest.writeString(this.mobileNo);
    }
}
