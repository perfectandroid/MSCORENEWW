package com.creativethoughts.iscore.db.dao.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 12/13/2017 - 10:04 AM.
 */

public class DynamicMenuDetails {

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

    public String getNeft() {
        return neft;
    }

    public void setNeft(String neft) {
        this.neft = neft;
    }

    public String getRecharge() {
        return recharge;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getImps() {
        return imps;
    }

    public void setImps(String imps) {
        this.imps = imps;
    }

    public String getRtgs() {
        return rtgs;
    }

    public void setRtgs(String rtgs) {
        this.rtgs = rtgs;
    }

    public String getKseb() {
        return kseb;
    }

    public void setKseb(String kseb) {
        this.kseb = kseb;
    }

    public String getOwnImps() {
        return ownImps;
    }

    public void setOwnImps(String ownImps) {
        this.ownImps = ownImps;
    }
}
