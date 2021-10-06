package com.creativethoughts.iscore.model;

public class BarcodeAgainstCustomerAccountList {
    private String customerNumber;
    private String customerName;
    private String AccountName;
    private String AccountNumber;

    public BarcodeAgainstCustomerAccountList(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public BarcodeAgainstCustomerAccountList(String customerNumber, String customerName, String accountName, String accountNumber) {
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        AccountName = accountName;
        AccountNumber = accountNumber;
    }


    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }
}
