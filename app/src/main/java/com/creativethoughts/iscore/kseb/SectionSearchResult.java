package com.creativethoughts.iscore.kseb;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 1/20/2018 - 4:22 PM
 */

public class SectionSearchResult {
    @SerializedName( "KSEBSectionList" )
    private ArrayList< SectionDetails > sectionDetailsList;

    public List<SectionDetails> getSectionDetailsList() {
        return sectionDetailsList;
    }

    /*public void setSectionDetailsList(ArrayList<SectionDetails> sectionDetailsList) {
        this.sectionDetailsList = sectionDetailsList;
    }*/
}
