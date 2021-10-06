package com.creativethoughts.iscore.model;

/**
 * Created by Viswanathan on 4/23/2015.
 */
public class Account {
    private String acc_numb;
    private String acc_amount;

    public Account() {
    }

    public Account(String acc_numb, String acc_amount) {
        this.acc_numb = acc_numb;
        this.acc_amount = acc_amount;
    }

    public String getAcc_numb() {
        return acc_numb;
    }

    public void setAcc_numb(String acc_numb) {
        this.acc_numb = acc_numb;
    }

    public String getAcc_amount() {
        return acc_amount;
    }

    public void setAcc_amount(String acc_amount) {
        this.acc_amount = acc_amount;
    }


    @Override
    public String toString() {
        return acc_numb;
    }
}
