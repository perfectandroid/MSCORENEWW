package com.creativethoughts.iscore.db.dao.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Share {

    private String AccountNumber,IFSCCode,FundTransferAccount,Branch,name,loantype;
    private boolean isSelected = false;

    public Share(String AccountNumber, String IFSCCode,String FundTransferAccount,String Branch,String loantype,String name) {

        this.AccountNumber = AccountNumber;
        this.IFSCCode = IFSCCode;
        this.FundTransferAccount = FundTransferAccount;
        this.Branch = Branch;
        this.loantype=loantype;
        this.name = name;

    }


    public String getAccountNumber() {
        return AccountNumber;
    }
    public void setAccountNumber(String AccountNumber) {
        AccountNumber = AccountNumber;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }
    public void setIFSCCode(String IFSCCode) {
        IFSCCode = IFSCCode;
    }

    public String getFundTransferAccount() {
        return FundTransferAccount;
    }
    public void setFundTransferAccount(String FundTransferAccount) {
        FundTransferAccount = FundTransferAccount;
    }
    public String getBranch() {
        return Branch;
    }
    public void setBranch(String Branch) {
        Branch = Branch;
    }

    public String getLoantype() {
        return loantype;
    }
    public void setLoantype(String loantype) {
        loantype = loantype;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        name = name;
    }




    public void setSelected(boolean isChecked) {
        isSelected = isChecked;
    }


    public boolean isSelected() {
        return isSelected;
    }

/*    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            // obj.put("Id", _masterId);
            obj.put("AccountNumber", AccountNumber);
            obj.put("IFSCCode", IFSCCode);


        } catch (JSONException e) {
            // ("DefaultListItem.toString JSONException: "+e.getMessage());
        }
        return obj;
    }*/
}
