package com.creativethoughts.iscore.db.dao.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by muthukrishnan on 26/09/15.
 */
public class Transaction  implements Serializable {
    public long id;
    public String transactionId;
    public String demandDepositNo;
    public String amount;
    public String chequeNo;
    public String chequeDate;
    public String narration;
    public String transType;
    public String remarks;
    public String effectDate;
    public String transactionNo;
    public String accNo;
    public boolean isNew;

   public Transaction() {
       this.effectDate = effectDate;
        this.chequeNo = chequeNo;
        this.amount = amount;
    }
    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        chequeNo = chequeNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        amount = amount;
    }
    public String getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(String effectDate) {
        effectDate = effectDate;
    }
    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        narration = narration;
    }

    public String getTransType() {

       return transType;
    }

    public void setTransType(String transType) {

       transType = transType;
    }

    public boolean isNew() {

        return isNew;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
           // obj.put("Id", _masterId);
            obj.put("Amount", amount);
            obj.put("Type", transType);


        } catch (JSONException e) {
           // ("DefaultListItem.toString JSONException: "+e.getMessage());
        }
        return obj;
    }
}
