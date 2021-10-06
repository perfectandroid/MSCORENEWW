package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 7/23/2018 - 11:47 AM.
 */
public class SyncDMenu {
    @SerializedName("Recharge")
    private String recharge;

    @SerializedName("Imps")
    private String imps;

    @SerializedName("Rtgs")
    private String rtgs;

    @SerializedName("Kseb")
    private String kseb;

    @SerializedName("Neft")
    private String neft;

    @SerializedName("OwnImps")
    private String ownImps;

    public String getRecharge() {
        return recharge;
    }

    public String getImps() {
        return imps;
    }

    public String getRtgs() {
        return rtgs;
    }

    public String getKseb() {
        return kseb;
    }

    public String getNeft() {
        return neft;
    }

    public String getOwnImps() {
        return ownImps;
    }
}
