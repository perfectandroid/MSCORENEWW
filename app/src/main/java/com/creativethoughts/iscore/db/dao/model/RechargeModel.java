package com.creativethoughts.iscore.db.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vishnu on 3/20/2017 - 10:38 AM.
 */

public class RechargeModel implements Parcelable {
    public  String mobileNo;
    public String serviceProvider;
    public int type;
    public String RefID;
    public String accno,branch,operator,circle,amount;

    public RechargeModel(Parcel in) {
        mobileNo = in.readString();
        serviceProvider = in.readString();
        type = in.readInt();
        accno = in.readString();
        branch = in.readString();
        operator = in.readString();
        circle = in.readString();
        amount = in.readString();
    }

    public static final Creator<RechargeModel> CREATOR = new Creator<RechargeModel>() {
        @Override
        public RechargeModel createFromParcel(Parcel in) {
            return new RechargeModel(in);
        }

        @Override
        public RechargeModel[] newArray(int size) {
            return new RechargeModel[size];
        }
    };

    public RechargeModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobileNo);
        dest.writeString(serviceProvider);
        dest.writeInt(type);
        dest.writeString(accno);
        dest.writeString(branch);
        dest.writeString(operator);
        dest.writeString(circle);
        dest.writeString(amount);
    }
}
