package com.creativethoughts.iscore.kseb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 1/20/2018 - 4:21 PM.
 */

public class SectionDetails implements Parcelable {
    public static final Parcelable.Creator<SectionDetails> CREATOR = new Parcelable.Creator<SectionDetails>() {
        @Override
        public SectionDetails createFromParcel(Parcel source) {
            return new SectionDetails(source);
        }

        @Override
        public SectionDetails[] newArray(int size) {
            return new SectionDetails[size];
        }
    };
    @SerializedName( "SectionName" )
    private String sectionName;
    @SerializedName( "SectionCode" )
    private String sectionCode;

    public SectionDetails() {
    }

    protected SectionDetails(Parcel in) {
        this.sectionName = in.readString();
        this.sectionCode = in.readString();
    }

    public String getSectionName() {
        return sectionName.trim();
    }

    public void setSectionName(String sectionName) {
        sectionName.replaceAll("\t", "");

        this.sectionName = sectionName.trim();
    }

    public String getSectionCode() {
        return sectionCode.trim();
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sectionName);
        dest.writeString(this.sectionCode);
    }
}
