package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishnu on 7/23/2018 - 11:57 AM.
 */
public class SyncTransactions {
    @SerializedName("transaction")
    private String transaction;

    @SerializedName("fk_DemandDeposit")
    private String fkDemandDeposit;

    @SerializedName("effectDate")
    private String effectDate;

    @SerializedName("amount")
    private String amount;

    @SerializedName("chequeNo")
    private String chequeNo;

    @SerializedName("chequeDate")
    private String chequeDate;

    @SerializedName("narration")
    private String narration;

    @SerializedName("transType")
    private String transType;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("OpeningBal")
    private String openingBal;

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getFkDemandDeposit() {
        return fkDemandDeposit;
    }

    public void setFkDemandDeposit(String fkDemandDeposit) {
        this.fkDemandDeposit = fkDemandDeposit;
    }

    public String getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(String effectDate) {
        this.effectDate = effectDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOpeningBal() {
        return openingBal;
    }

    public void setOpeningBal(String openingBal) {
        this.openingBal = openingBal;
    }
}
