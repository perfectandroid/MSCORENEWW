package com.creativethoughts.iscore.gsonmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vishnu on 7/23/2018 - 11:43 AM.
 */
public class SyncMain {
    @SerializedName("customerId")
    private String customerId;

    @SerializedName("customerNo")
    private String customerNo;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerAddress1")
    private String customerAddress1;

    @SerializedName("customerAddress2")
    private String customerAddress2;

    @SerializedName("customerAddress3")
    private String customerAddress3;

    @SerializedName("mobileNo")
    private String mobileNo;

    @SerializedName("pin")
    private String pin;

    @SerializedName("default1")

    private String default1;

    @SerializedName("login")
    private String login;

    @SerializedName("TokenNo")
    private String TokenNo;



    @SerializedName("DMenu")
    private String dMenuString;

    private SyncDMenu dMenu;

    @SerializedName("accounts")
    private List< SyncAccount > accountList;

    @SerializedName("Messages")
    private List<SyncMessage> messages;

    private String userLogin;
    public void setUserLogin( String userLogin ){
        this.userLogin = userLogin;
    }
    public void setdMenuString(String dMenuString) {
        this.dMenuString = dMenuString;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress1() {
        return customerAddress1;
    }

    public String getCustomerAddress2() {
        return customerAddress2;
    }

    public String getCustomerAddress3() {
        return customerAddress3;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getPin() {
        return pin;
    }

    public String getDefault1() {
        return default1;
    }

    public String getLogin() {
        return login;
    }

    public String getTokenNo() {
        return TokenNo;
    }

    public String getdMenuString() {
        return dMenuString;
    }

    public List<SyncAccount> getAccountList() {
        return accountList;
    }

    public List<SyncMessage> getMessages() {
        return messages;
    }

    public SyncDMenu getdMenu() {
        return dMenu;
    }

    public void setdMenu(SyncDMenu dMenu) {
        this.dMenu = dMenu;
    }
}
