package com.creativethoughts.iscore.model;

public class ToAccountDetails {
    private String FK_Account;
    private String AccountNumber;
    private String SubModule;
    private String Balance;
    private String typeShort;
    private String BranchName;


    public ToAccountDetails(String FK_Account, String AccountNumber, String SubModule, String Balance, String typeShort,String BranchName) {
        this.FK_Account = FK_Account;
        this.AccountNumber = AccountNumber;
        this.SubModule = SubModule;
        this.Balance = Balance;
        this.typeShort = typeShort;
        this.BranchName = BranchName;
    }

    public String getFK_Account() {
        return FK_Account;
    }

    public void setFK_Account(String FK_Account) {
        this.FK_Account = FK_Account;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public String getSubModule() {
        return SubModule;
    }

    public void setSubModule(String SubModule) {
        this.SubModule = SubModule;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String Balance) {
        this.Balance = Balance;
    }

    public String gettypeShort() {
        return typeShort;
    }

    public void settypeShort(String typeShort) {
        this.typeShort = typeShort;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String BranchName) {
        this.BranchName = BranchName;
    }


    @Override
    public String toString() {
        return AccountNumber;
    }
}


