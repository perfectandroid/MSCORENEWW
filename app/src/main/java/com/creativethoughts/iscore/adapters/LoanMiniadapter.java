package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LoanMiniadapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String date, amount, narrat,type;

    public LoanMiniadapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        // this.strdate = strdate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ministatement_list_row1, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                date = jsonObject.getString("TransDate");
                SimpleDateFormat convetDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

                String date1 = convetDateFormat.format(Date.parse(date));

                amount = jsonObject.getString("Amount");
                narrat = jsonObject.getString("Narration");
                type = jsonObject.getString("TransType");


                if(type.equals("C"))
                {
                    String type1="Cr";
                    ((MainViewHolder) holder).transctn.setText(type1);

                    ((MainViewHolder) holder).transctn.setTextColor(ContextCompat.getColor(context, R.color.Green));
                    ((MainViewHolder) holder).trans_amount.setTextColor(ContextCompat.getColor(context, R.color.Green));
                }
                else if(type.equals("D"))
                {
                    String type1="Dr";
                    ((MainViewHolder) holder).transctn.setText(type1);
                    ((MainViewHolder) holder).transctn.setTextColor(ContextCompat.getColor(context, R.color.FireBrick));
                    ((MainViewHolder) holder).trans_amount.setTextColor(ContextCompat.getColor(context, R.color.FireBrick));
                }


                ((MainViewHolder) holder).tv_date.setText(date1);
                double num =Double.parseDouble(amount);
                ((MainViewHolder) holder).trans_amount.setText("\u20B9 "+ NumberFormat.getNumberInstance(Locale.US).format(num));
                ((MainViewHolder) holder).narration.setText(narrat);


            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        public LinearLayout lnLayout;
        public TextView tv_date,trans_amount,narration,transctn;
       // ImageView imgv_dirctn;

        public MainViewHolder(View v) {
            super(v);
            //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
            tv_date = v.findViewById(R.id.tv_date);
            trans_amount = v.findViewById(R.id.trans_amount);
            narration = v.findViewById(R.id.narration);
            transctn= v.findViewById(R.id.trans_type);

        }
    }
}
