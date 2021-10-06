package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

public class AccountSummaryListAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public AccountSummaryListAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_accountsummarylist, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                String b = jsonObject.getString("Key");
                ((MainViewHolder)holder).tvkey.setText(jsonObject.getString("Key"));

                String temp = jsonObject.getString("Value");
                String strValue;
                if(temp.indexOf("$")!=-1)
                {
                    strValue=(jsonObject.getString("Value").replaceAll("\\$", "    "));
                    ((MainViewHolder)holder).tvkey.setVisibility(View.GONE);
                    ((MainViewHolder)holder).imSeperator.setVisibility(View.GONE);
                    System.out.println("there is 'b' in temp string");
                    setMargins( ((MainViewHolder)holder).tvvalue, 20, 0, 0, 20);
                    strValue=context.getResources().getString(R.string.pledgeDeposit)+"  >>  \n"+(strValue.replaceAll("\\*", "\n"));
                }
                else
                {
                    strValue= jsonObject.getString("Value");
                }

                ((MainViewHolder)holder).tvvalue.setEnabled(false);
//                    ((MainViewHolder)holder).tvvalue.setFocusableInTouchMode(false);
                ((MainViewHolder)holder).tvvalue.setText(strValue);


                if(jsonObject.getString("Key").equals("Pledged Deposit")){
                    dateDifferentiate(2017,12,10);
                }


                if (jsonObject.getString("Key").equals("Account No")) {
                   /* ((MainViewHolder)holder).llDetails.setVisibility(View.GONE);
                    ((MainViewHolder)holder).llDetails.setMinimumHeight(0);
*/

                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);


                }
                if (jsonObject.getString("Key").equals("Aval Balance")) {

                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
                if (jsonObject.getString("Key").equals("Fund Transfer Account")) {

                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
                if (jsonObject.getString("Key").equals("IFSC Code")) {

                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }






          /*      if (jsonObject.getString("Key").equals("Account No")
                        || jsonObject.getString("Key").equals("Aval Balance")
                        || jsonObject.getString("Key").equals("Account Type")
                        || jsonObject.getString("Key").equals("Fund Transfer Account")
                        || jsonObject.getString("Key").equals("IFSC Code")
                        || jsonObject.getString("Key").equals("Mode Of Operation")) {
                 ((MainViewHolder)holder).llDetails.setVisibility(View.GONE);
                }
                else{
                  *//*  if(position==0){
                      //  ((MainViewHolder)holder).tvkey.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                      //  ((MainViewHolder)holder).tvvalue.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                      //  ((MainViewHolder)holder).tvkey.setTypeface(null, Typeface.BOLD);
                       // ((MainViewHolder)holder).tvvalue.setTypeface(null, Typeface.BOLD);
                     //   ((MainViewHolder)holder).llDetails.setBackgroundResource(R.color.blue_variant3);
                    }*//*
                    ((MainViewHolder)holder).tvkey.setText(jsonObject.getString("Key"));

                    String temp = jsonObject.getString("Value");
                    String strValue;
                    if(temp.indexOf("$")!=-1)
                    {
                        strValue=(jsonObject.getString("Value").replaceAll("\\$", "    "));
                        ((MainViewHolder)holder).tvkey.setVisibility(View.GONE);
                        ((MainViewHolder)holder).imSeperator.setVisibility(View.GONE);
                        System.out.println("there is 'b' in temp string");
                        setMargins( ((MainViewHolder)holder).tvvalue, 20, 0, 0, 20);
                        strValue=context.getResources().getString(R.string.pledgeDeposit)+"  >>  \n"+(strValue.replaceAll("\\*", "\n"));
                    }
                    else
                    {
                        strValue= jsonObject.getString("Value");
                    }

                    ((MainViewHolder)holder).tvvalue.setEnabled(false);
//                    ((MainViewHolder)holder).tvvalue.setFocusableInTouchMode(false);
                    ((MainViewHolder)holder).tvvalue.setText(strValue);


                    if(jsonObject.getString("Key").equals("Pledged Deposit")){
                        dateDifferentiate(2017,12,10);
                    }
                }*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void dateDifferentiate(int year,int month,int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();
        long todayInMillis = c.getTimeInMillis();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date dateSpecified = c.getTime();
        if (dateSpecified.before(today)) {
            System.err.println("Date specified [" + dateSpecified + "] is before today [" + today + "]");

        } else {
            System.err.println("Date specified [" + dateSpecified + "] is NOT before today [" + today + "]");

        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView  tvkey;
        TextView tvvalue;
        LinearLayout llDetails;
        ImageView imSeperator;
        public MainViewHolder(View v) {
            super(v);
            tvvalue=v.findViewById(R.id.tvvalue);
            tvkey=v.findViewById(R.id.tvkey);
            llDetails=v.findViewById(R.id.llDetails);
            // imSeperator=v.findViewById(R.id.imSeperator);
        }
    }
}