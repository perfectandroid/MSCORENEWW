package com.creativethoughts.iscore.utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vishnu on 3/11/2017.
 */
public class Validation {
    //validate mobile number
    public static String mobileNumberValidator(String mobileNo){
        if(mobileNo == null || mobileNo.length() != 10)
            return "Phone number must be 10 digits";
        return "";
    }
    public static String nameValidation(String name){
        if(name.isEmpty() )
            return "Please enter consumer name";
        return"";
    }
    public static String amount(String amount){
        boolean isValidInteger = false;
        try{
            Float.parseFloat(amount);
            isValidInteger = true;
        }catch (NumberFormatException e){
        }
        if(amount.isEmpty() || isValidInteger == false)
            return "Please enter valid amount";
        return"";
    }
    public static String billNoValidator(String billNo){
        if(billNo.isEmpty() )
            return "Please enter bill no";
        return"";
    }
    public static String consumerNoValidation(String consumerNo){
        if (consumerNo.isEmpty()){
            return "Plase enter the consumer number";
        }
        else if (consumerNo.length() < 8 || consumerNo.length()>16)
            return "Please enter correct consumer number";
        return "";
    }
    public static String sectionCodeValidator(String sectionCode){
        if(sectionCode.isEmpty())
            return "Please select the section";
        return "";
    }
    public static String transactionIdValidator(String transactionId){
        if(!transactionId.isEmpty()){
            return "Please enter full transaction Id";
        }
        return "";
    }
}
