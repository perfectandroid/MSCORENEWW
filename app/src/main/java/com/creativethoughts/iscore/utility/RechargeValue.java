package com.creativethoughts.iscore.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap    ;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by muthukrishnan on 19/05/16.
 */
public class RechargeValue {

    public static ArrayList<String> getAllOperator() {
        ArrayList<String> operator = new ArrayList<>();

//        operator.add("Airtel");
//        operator.add("Vodafone");
//        operator.add("BSNL TopUp");
//        operator.add("BSNL Recharge/Validity (RCV)");
//        operator.add("BSNL 3G");
//        operator.add("BSNL Special (STV)");
//        operator.add("Reliance CDMA");
//        operator.add("Reliance GSM");
//        operator.add("Aircel");
//        operator.add("MTNL TopUp");
//        operator.add("MTNL Recharge/Special");
//        operator.add("Idea");
//        operator.add("Tata Indicom");
//        operator.add("Loop Mobile");
//        operator.add("Tata Docomo");
//        operator.add("Tata Docomo Special");
//        operator.add("Virgin CDMA");
//        operator.add("MTS");
//        operator.add("Virgin GSM");
//        operator.add("S Tel");
//        operator.add("Uninor");
//        operator.add("Uninor Special");
//        operator.add("Videocon");
//        operator.add("Videocon Special");

        Iterator it = getOperators().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            operator.add((String) pair.getKey());
        }

        Collections.sort(operator);

        return operator;
    }

    public static String getOperatorId(String operator) {

        return getOperators().get(operator);
    }

    public static Hashtable<String, String> getOperators() {
        Hashtable<String, String> operator = new Hashtable<>();

        operator.put("Airtel", "1");
        operator.put("V!", "2");
        operator.put("BSNL", "3");
//        operator.put("BSNL Recharge/Validity (RCV)", "301");
//        operator.put("BSNL 3G", "302");
//        operator.put("BSNL Special (STV)", "303");
//        operator.put("Reliance CDMA", "4");
//        operator.put("Reliance GSM", "5");
//        operator.put("Aircel", "6");
//        operator.put("MTNL TopUp", "25");
//        operator.put("MTNL Recharge/Special", "2501");
//        operator.put("Idea", "8");
//        operator.put("Tata Indicom", "9");
//        operator.put("Loop Mobile", "10");
//        operator.put("Tata Docomo", "11");
//        operator.put("Tata Docomo Special", "1101");
//        operator.put("Virgin CDMA", "12");
//        operator.put("MTS", "13");
//        operator.put("Virgin GSM", "14");
//        operator.put("S Tel", "15");
//        operator.put("Uninor", "16");
//        operator.put("Uninor Special", "1601");
//        operator.put("Videocon", "17");
//        operator.put("Videocon Special", "1701");
        operator.put("Jio", "400");
        return operator;
    }
    public static Hashtable<String, String> getDataCardOperators(){
        Hashtable<String, String > dataCardHashMap = new Hashtable<>();
        dataCardHashMap.put("NetConnect +", "4");
        dataCardHashMap.put("NetConnect 3G", "5");
        dataCardHashMap.put("Tata Photon +", "9");
        dataCardHashMap.put("Mbrowse", "13");
        return dataCardHashMap;
    }
    public static String getDataCardOperatorsId(String operator){
        return getDataCardOperators().get(operator);
    }

    public static ArrayList<String> getAllLandLineOperator() {
        ArrayList<String> operator = new ArrayList<>();

        Iterator it = getLandLineOperators().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            operator.add((String) pair.getKey());
        }

        Collections.sort(operator);

        return operator;
    }
    public static ArrayList<String> getAllDataCardOperators(){
        ArrayList<String > dataCardOperatorsList = new ArrayList<>();
        Iterator iterator = getDataCardOperators().entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            dataCardOperatorsList.add((String) pair.getKey());
        }
        Collections.sort(dataCardOperatorsList);
        return  dataCardOperatorsList;
    }
    public static String getLandLineOperatorId(String operator) {
        return getLandLineOperators().get(operator);
    }

    public static Hashtable<String, String> getLandLineOperators() {
        Hashtable<String, String> operator = new Hashtable<>();

        operator.put("Airtel Landline", "42");
        operator.put("BSNL Landline", "37");
        operator.put("MTNL Landline", "41");

        return operator;
    }


    public static ArrayList<String> getAllDTHOperator() {
        ArrayList<String> operator = new ArrayList<>();

        Iterator it = getDTHOperators().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            operator.add((String) pair.getKey());
        }

        Collections.sort(operator);

        return operator;
    }

    public static String getDTHOperatorId(String operator) {

        return getDTHOperators().get(operator);
    }

    public static Hashtable<String, String> getDTHOperators() {
        Hashtable<String, String> operator = new Hashtable<>();

        operator.put("Dish TV DTH", "18");
        operator.put("Tata Sky DTH", "19");
        operator.put("Big TV DTH", "20");
        operator.put("Videocon DTH", "21");
        operator.put("Sun DTH", "22");
        operator.put("Airtel DTH", "23");

        return operator;
    }

    public static ArrayList<String> getAllPostPaidOperator() {
        ArrayList<String> operator = new ArrayList<>();

        Iterator it = getPostPaidOperators().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            operator.add((String) pair.getKey());
        }

        Collections.sort(operator);

        return operator;
    }

    public static String getPostPaidOperatorId(String operator) {

        return getPostPaidOperators().get(operator);
    }

    public static Hashtable<String, String> getPostPaidOperators() {
        Hashtable<String, String> operator = new Hashtable<>();

        operator.put("Airtel", "31");
        operator.put("V!", "33");
        operator.put("BSNL Mobile", "36");
//        operator.put("Reliance CDMA", "35");
//        operator.put("Reliance GSM", "34");
//        operator.put("Idea", "32");
//        operator.put("Tata Indicom", "39");
       /* operator.put("LOOP Mobile", "40");*/
//        operator.put("Tata Docomo GSM", "38");

        return operator;
    }

    public static ArrayList<String> getAllCircle() {
        ArrayList<String> circle = new ArrayList<>();

//        circle.add("Andhra Pradesh");
//        circle.add("Assam");
//        circle.add("Bihar & Jharkhand");
//        circle.add("Chennai");
//        circle.add("Delhi");
//        circle.add("Gujarat");
//        circle.add("Haryana");
//        circle.add("Himachal Pradesh");
//        circle.add("Jammu & Kashmir");
//        circle.add("Karnataka");
//        circle.add("Kerala");
//        circle.add("Kolkata");
//        circle.add("Maharashtra & Goa (except Mumbai)");
//        circle.add("Madhya Pradesh & Chhattisgarh");
//        circle.add("Mumbai");
//        circle.add("North East");
//        circle.add("Orissa");
//        circle.add("Punjab");
//        circle.add("Rajasthan");
//        circle.add("Tamil Nadu");
//        circle.add("Uttar Pradesh - East");
//        circle.add("Uttar Pradesh - West");
//        circle.add("West Bengal");

        Iterator it = getCircle().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            circle.add((String) pair.getKey());
        }

        Collections.sort(circle);

        return circle;
    }

    public static String getCircleId(String city) {

        return getCircle().get(city);
    }

    public static HashMap<String, String> getCircle() {
        HashMap<String, String> circle = new HashMap<>();

        circle.put("Andhra Pradesh", "1");
        circle.put("Assam", "2");
        circle.put("Bihar & Jharkhand", "3");
        circle.put("Chennai", "4");
        circle.put("Delhi", "5");
        circle.put("Gujarat", "6");
        circle.put("Haryana", "7");
        circle.put("Himachal Pradesh", "8");
        circle.put("Jammu & Kashmir", "9");
        circle.put("Karnataka", "10");
        circle.put("Kerala", "11");
        circle.put("Kolkata", "12");
        circle.put("Maharashtra & Goa (except Mumbai)", "13");
        circle.put("Madhya Pradesh & Chhattisgarh", "14");
        circle.put("Mumbai", "15");
        circle.put("North East", "16");
        circle.put("Orissa", "17");
        circle.put("Punjab", "18");
        circle.put("Rajasthan", "19");
        circle.put("Tamil Nadu", "20");
        circle.put("Uttar Pradesh - East", "21");
        circle.put("Uttar Pradesh - West", "22");
        circle.put("West Bengal", "23");

        return circle;
    }
    public static int getOperatorName(String id){
        String keyString = ""; int i =0;
        ArrayList<String> sortedArrayList = new ArrayList<>();
        Hashtable<String, String> tempHashTable = getOperators();
        Set<String> keys = tempHashTable.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            i++;
            keyString = iterator.next();
            if(id.equals(tempHashTable.get(keyString)))
                break;

        }
        if(keyString.equals(""))
            return -1;
        sortedArrayList = getAllOperator();
        i = 0;
        for(String tempSortedArrayString: sortedArrayList){
            i++;
            if(tempSortedArrayString.equals(keyString))
                return i;
        }
        return tempHashTable.size();
    }
}
