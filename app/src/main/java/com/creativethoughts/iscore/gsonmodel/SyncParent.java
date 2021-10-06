package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vishnu on 7/23/2018 - 11:43 AM.
 */
public class SyncParent {
    @SerializedName("acInfo")
    private List<SyncMain> acInfo;

    public List<SyncMain> getAcInfo() {
        return acInfo;
    }
}
