package com.creativethoughts.iscore.custom_alert_dialogs;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyValuePair implements Parcelable {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    public KeyValuePair() {
    }

    protected KeyValuePair(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<KeyValuePair> CREATOR = new Parcelable.Creator<KeyValuePair>() {
        @Override
        public KeyValuePair createFromParcel(Parcel source) {
            return new KeyValuePair(source);
        }

        @Override
        public KeyValuePair[] newArray(int size) {
            return new KeyValuePair[size];
        }
    };
}
