package com.creativethoughts.iscore.neftrtgs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vishnu on 2/17/2018 - 2:15 PM.
 */

public class PaymentModel implements Parcelable {
    private String accNo;
    private String module;
    private String beneficiaryName;
    private String ifsc;
    private String beneficiaryAccNo;
    private String amount;
    private String mode;
    private String beneficiaryAdd;
    private String pin;
    private String otp;
    private String otpRefferenceNo;
    private String branch;
    private String from;
    private String balance;
    private String operator;
    private String circle;


    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBeneficiaryAccNo() {
        return beneficiaryAccNo;
    }

    public void setBeneficiaryAccNo(String beneficiaryAccNo) {
        this.beneficiaryAccNo = beneficiaryAccNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBeneficiaryAdd() {
        return beneficiaryAdd;
    }

    public void setBeneficiaryAdd(String beneficiaryAdd) {
        this.beneficiaryAdd = beneficiaryAdd;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpRefferenceNo() {
        return otpRefferenceNo;
    }

    public void setOtpRefferenceNo(String otpRefferenceNo) {
        this.otpRefferenceNo = otpRefferenceNo;
    }
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getBal() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accNo);
        dest.writeString(this.module);
        dest.writeString(this.beneficiaryName);
        dest.writeString(this.ifsc);
        dest.writeString(this.beneficiaryAccNo);
        dest.writeString(this.amount);
        dest.writeString(this.mode);
        dest.writeString(this.beneficiaryAdd);
        dest.writeString(this.pin);
        dest.writeString(this.otp);
        dest.writeString(this.otpRefferenceNo);
    }

    public PaymentModel() {
    }

    protected PaymentModel(Parcel in) {
        this.accNo = in.readString();
        this.module = in.readString();
        this.beneficiaryName = in.readString();
        this.ifsc = in.readString();
        this.beneficiaryAccNo = in.readString();
        this.amount = in.readString();
        this.mode = in.readString();
        this.beneficiaryAdd = in.readString();
        this.pin = in.readString();
        this.otp = in.readString();
        this.otpRefferenceNo = in.readString();
    }

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel source) {
            return new PaymentModel(source);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };
}
