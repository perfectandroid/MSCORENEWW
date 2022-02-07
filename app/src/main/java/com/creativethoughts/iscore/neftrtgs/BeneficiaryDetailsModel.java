package com.creativethoughts.iscore.neftrtgs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 9/7/2017 - 12:04 PM - 12:05 PM
 */
public class BeneficiaryDetailsModel implements Parcelable {
    @SerializedName( "BeneAccNo" )
    private String beneficiaryAccNo;

    @SerializedName( "BeneName" )
    private String beneficiaryName;

    @SerializedName( "BeneIFSC" )
    private String beneficiaryIfsc;

    public BeneficiaryDetailsModel(String benefname, String beneIFSC, String beneAccNo) {
        this.beneficiaryName = benefname;
        this.beneficiaryIfsc = beneIFSC;
        this.beneficiaryAccNo = beneAccNo;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }


    public String getBeneficiaryIfsc() {
        return beneficiaryIfsc;
    }

    public void setBeneficiaryIfsc(String beneficiaryIfsc) {
        this.beneficiaryIfsc = beneficiaryIfsc;
    }


    public String getBeneficiaryAccNo() {
        return beneficiaryAccNo;
    }

    public void setBeneficiaryAccNo(String beneficiaryAccNo) {
        this.beneficiaryAccNo = beneficiaryAccNo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.beneficiaryAccNo);
        dest.writeString(this.beneficiaryName);
        dest.writeString(this.beneficiaryIfsc);
    }

    public BeneficiaryDetailsModel() {
    }

    protected BeneficiaryDetailsModel(Parcel in) {
        this.beneficiaryAccNo = in.readString();
        this.beneficiaryName = in.readString();
        this.beneficiaryIfsc = in.readString();
    }

    public static final Creator<BeneficiaryDetailsModel> CREATOR = new Creator<BeneficiaryDetailsModel>() {
        @Override
        public BeneficiaryDetailsModel createFromParcel(Parcel source) {
            return new BeneficiaryDetailsModel(source);
        }

        @Override
        public BeneficiaryDetailsModel[] newArray(int size) {
            return new BeneficiaryDetailsModel[size];
        }
    };
}
