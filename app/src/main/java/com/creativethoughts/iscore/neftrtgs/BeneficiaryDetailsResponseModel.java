package com.creativethoughts.iscore.neftrtgs;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by vishnu on 2/6/2018 - 3:34 PM.
 */

public class BeneficiaryDetailsResponseModel {
    @SerializedName( "reciverlist" )
    private ArrayList< BeneficiaryDetailsModel > beneficiaryDetailsModelList;

    public ArrayList<BeneficiaryDetailsModel> getBeneficiaryDetailsModelList() {
        return beneficiaryDetailsModelList;
    }

    public void setBeneficiaryDetailsModelList(ArrayList<BeneficiaryDetailsModel> beneficiaryDetailsModelList) {
        this.beneficiaryDetailsModelList = beneficiaryDetailsModelList;
    }
}
