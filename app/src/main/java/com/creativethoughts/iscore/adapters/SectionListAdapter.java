package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtilitySectionList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vishnu on 3/16/2017.
 */

public class SectionListAdapter extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> sectionList;
    HashMap<String, String> sectionHashMap;
    ArrayList<HashMap<String, String>> sectionListHashmap;

    public SectionListAdapter(Context context,int textViewResourceId){
        super(context, textViewResourceId);
        sectionList = new ArrayList<String>();
    }
    @Override
    public int getCount(){
        return sectionList.size();
    }
    @Override
    public String getItem(int index){
        return sectionList.get(index);
    }
    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                try{
                    ArrayList<String> tempSectionList = null;
                    tempSectionList = new LoadSectionList().execute(new String[]{constraint.toString()}).get();
                   for ( String item: tempSectionList) {
                        if(!sectionList.contains(item))
                            sectionList.add(item);
                    }
                }catch (Exception e){
                    if(IScoreApplication.DEBUG) Log.e("Exception", e.toString());
                }
                filterResults.values = sectionList;
                filterResults.count = sectionList.size();
                return filterResults;
//                filterResults.values = sectionListHashmap;
//                filterResults.count = sectionListHashmap.size();
//                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public void downloadList(String constraint){
//        SharedPreferences pref =getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//        String BASE_URL=pref.getString("oldbaseurl", null);

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        String response, tempSectionName, tempSectionCode;
        try{
            String url = BASE_URL+"/KSEBSectionList?Sectionname="+constraint;
            if (IScoreApplication.DEBUG) Log.e("url", url);
            response = ConnectionUtilitySectionList.getResponse(url);
            if(!TextUtils.isEmpty(response)){
                if(!IScoreApplication.containAnyKnownException(response)){
                    try{
                        sectionList.clear();
                        JSONObject responseObject = new JSONObject(response);
                        JSONArray responseArray = responseObject.optJSONArray("KSEBSectionList");
                        if(responseArray.length() != 0){
                            for(int i = 0; i < responseArray.length(); i++){
                                JSONObject sectionObject = responseArray.getJSONObject(i);
                                tempSectionName = sectionObject.getString("SectionName");
                                tempSectionCode = sectionObject.getString("SectionCode");
//                                sectionHashMap = null;
//
//                                sectionHashMap.put("SectionCode", tempSectionCode);
//                                sectionListHashmap.add(sectionHashMap);
//                                sectionList.clear();
                                if(!sectionList.contains(tempSectionName))
                                    sectionList.add(tempSectionName.trim()+"["+tempSectionCode+"]" );

                            }
                        }
                    }catch (JSONException j){
//                        if(!sectionList.contains("Exception from json"))
//                            sectionList.add("Exception from json");
                    }
                }
            }
//            if(!sectionList.contains("Empty resoponse"))
//                sectionList.add("Empty resoponse");
        }catch (Exception e){

        }
    }

    private class LoadSectionList extends AsyncTask<String, Void, ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(String... constraint){
           try{
              downloadList(constraint[0]);
           }catch (Exception e){
               if(IScoreApplication.DEBUG)Log.e("sdafj", e.toString());
                return sectionList;
           }
           return sectionList;
        }
        @Override
        protected void onPostExecute(ArrayList<String> result) {
        }

    }
}
