package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MiniStatementTranscationListAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String SubModule;

    public MiniStatementTranscationListAdapter(Context context, JSONArray jsonArray, String SubModule) {
        this.context=context;
        this.jsonArray=jsonArray;
        this.SubModule=SubModule;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout./*passbook_list_row */ministatement_list_row, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                Date date=new Date(jsonObject.getString("TransDate"));
                SimpleDateFormat formatter5=new SimpleDateFormat("dd-MM-yyyy");
                String formats1 = formatter5.format(date);
                System.out.println(formats1);

                ((MainViewHolder)holder).tv_date.setText(formats1);
                ((MainViewHolder)holder).narration.setText(jsonObject.getString("Narration"));
                ((MainViewHolder)holder).tv_chequeNo.setText(jsonObject.getString("chequeNo"));

                String transtype = jsonObject.getString("TransType");
                if(transtype.equals("C"))
                {
                    String type="Cr";
                    ((MainViewHolder)holder).trans_amount.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(jsonObject.getDouble("Amount"))+" "+type);
                    ((MainViewHolder)holder).trans_amount.setTextColor(ContextCompat.getColor(context, R.color.Green));
                }
                else if(transtype.equals("D"))
                {
                    String type="Dr";
                    ((MainViewHolder)holder).trans_amount.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(jsonObject.getDouble("Amount"))+" "+type);
                    ((MainViewHolder)holder).trans_amount.setTextColor(ContextCompat.getColor(context, R.color.FireBrick));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        int inlenght;
        if(jsonArray.length()<=10){
            inlenght=jsonArray.length();
        }else{
            inlenght=10;
        }
        return inlenght;
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
        TextView tv_date, trans_type, trans_amount, narration,tv_chequeNo;
       // LinearLayout lnLayout;
        public MainViewHolder(View v) {
            super(v);
            tv_date=v.findViewById(R.id.tv_date);
            trans_type=v.findViewById(R.id.trans_type);
            trans_amount=v.findViewById(R.id.trans_amount);
            narration=v.findViewById(R.id.narration);
            tv_chequeNo=v.findViewById(R.id.tv_chequeNo);
          //  lnLayout=v.findViewById(R.id.lnLayout);
        }
    }
}