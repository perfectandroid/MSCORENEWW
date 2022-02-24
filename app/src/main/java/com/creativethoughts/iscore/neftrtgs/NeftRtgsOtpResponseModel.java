package com.creativethoughts.iscore.neftrtgs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 1/30/2018 - 11:17 AM
 */

public class NeftRtgsOtpResponseModel implements Parcelable {
    @SerializedName("HttpStatusCode")
    public int statusCode;
    @SerializedName("StatusCode")
    public String otpRefNo;
    @SerializedName("Message")
    public String message;
    @SerializedName("ExMessge")
    public String exMessage;
    @SerializedName("RefID")
    public String refId;
    @SerializedName("Amount")
    public String amount;
    @SerializedName("AccNo")
    public String accno;
    @SerializedName("BenAccNo")
    public String benaccno;
    @SerializedName("Branch")
    public String branch;
    @SerializedName("TransDate")
    public String transdate;
    @SerializedName("Time")
    public String time;



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

    public String getAccno() {
        return accno;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getBenaccno() {
        return benaccno;
    }

    public void setBenaccno(String benaccno) {
        this.benaccno = benaccno;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTransdate() {
        return transdate;
    }

    public void setTransdate(String transdate) {
        this.transdate = transdate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
