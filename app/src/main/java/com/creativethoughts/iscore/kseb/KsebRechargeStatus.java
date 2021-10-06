package com.creativethoughts.iscore.kseb;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressLint("ParcelCreator")
public class KsebRechargeStatus implements Parcelable {
    @SerializedName("StatusCode")
    private int statusCode;

    @SerializedName("RefID")
    private String refId;

    @SerializedName("MobileNumber")
    private String mobileNo;

    @SerializedName("Amount")
    private String amount;

    @SerializedName("StatusMessage")
    private String statusMessage;

    @SerializedName("AccNumber")

    public String consumername;
    public String consumerno;
    public String mobile;
    public String sectionname;
    public String billno;
    public String accNo;
    public String branch;
    public String amt;
    public String statusmsg;

    public int getStatusCode() {
        return statusCode;
    }

    public String getRefId() {
        return refId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getAmount() {
        return amount;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }



    public String getConsumername() {
        return consumername;
    }

    public void setConsumername(String consumername) {
        this.consumername = consumername;
    }

    public String getConsumerno() {
        return consumerno;
    }

    public void setConsumerno(String consumerno) {
        this.consumerno = consumerno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getAmt() {
        return amt;
    }

    public void setAmount(String amt) {
        this.amt = amt;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
