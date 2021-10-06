package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vishnu on 7/23/2018 - 11:51 AM.
 */
public class SyncAccount {
    @SerializedName("account")
    private String account;

    @SerializedName("demandDeposit_id")
    private String demandDepositId;

    @SerializedName("acno")
    private String accNo;

    @SerializedName("lastacessdate")
    private String lastAccessDate;

    @SerializedName("module")
    private String module;

    @SerializedName("acType")
    private String acType;

    @SerializedName("typeShort")
    private String typeShort;

    @SerializedName("branchCode")
    private String branchCode;

    @SerializedName("branchName")
    private String branchName;

    @SerializedName("branchShort")
    private String branchShort;

    @SerializedName("depositDate")
    private String depositDate;

    @SerializedName("oppmode")
    private String oppMode;

    @SerializedName("FK_CustomerID")
    private String fkCustomerId;

    @SerializedName("AvailableBal")
    private String availableBal;

    @SerializedName("UnClrBal")
    private String unClrBal;

    @SerializedName("transactions")
    private List< SyncTransactions > transactionsList;

    public String getAccount() {
        return account;
    }

    public String getDemandDepositId() {
        return demandDepositId;
    }

    public String getAccNo() {
        return accNo;
    }

    public String getLastAccessDate() {
        try{
            String[] parts = lastAccessDate.split("/");
            String days6 = parts[0];
            String months7 = parts[1];
            String years8 = parts[2];
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH );
            Date date;
            date = originalFormat.parse(days6 + "-" + months7 + "-" + years8);
            return originalFormat.format(date);
        }catch ( Exception e ){
            return lastAccessDate;
        }
    }

    public String getModule() {
        return module;
    }

    public String getAcType() {
        return acType;
    }

    public String getTypeShort() {
        return typeShort;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getBranchShort() {
        return branchShort;
    }

    public String getDepositDate() {
        return depositDate;
    }

    public String getOppMode() {
        return oppMode;
    }

    public String getFkCustomerId() {
        return fkCustomerId;
    }

    public String getAvailableBal() {
        return availableBal;
    }

    public String getUnClrBal() {
        return unClrBal;
    }

    public List<SyncTransactions> getTransactionsList() {
        return transactionsList;
    }
}
