package com.creativethoughts.iscore.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SingleBranchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class rechargeHistoryAdapter extends RecyclerView.Adapter {
    public String TAG  = "rechargeHistoryAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String ID_Recharge,RechargeDate,AccountNo,MobileNo,RechargeRs;

    public rechargeHistoryAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_search_history, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {

                ID_Recharge = jsonObject.getString("ID_Recharge");
                RechargeDate = jsonObject.getString("RechargeDate");
                AccountNo = jsonObject.getString("AccountNo");
                MobileNo = jsonObject.getString("MobileNo");
                RechargeRs = jsonObject.getString("RechargeRs");
                String OperatorName = jsonObject.getString("OperatorName");
                String StatusType = jsonObject.getString("StatusType");
                String StatusTypeCode = jsonObject.getString("StatusTypeCode");


                ((MainViewHolder) holder).txtv_ph.setText(MobileNo);

                double num =Double.parseDouble(RechargeRs);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
                ((MainViewHolder) holder).txtv_amount.setText("\u20B9 "+ fmt.format(num));
                ((MainViewHolder) holder).txtv_date.setText("Done On "+RechargeDate);
                ((MainViewHolder) holder).tv_status.setText(""+StatusType);


//                StatusTypeCode
//                1.Success
//                2.Failed
//                3.Pending
//                4.Reversed

                if (StatusTypeCode.equals("2")){
                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.FireBrick));
                }
                if (StatusTypeCode.equals("1")){
                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Green));
                }
                if (StatusTypeCode.equals("3")){
                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Chocolate));
                }
                if (StatusTypeCode.equals("4")){
                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Magenta));
                }

//                if (StatusType.equals("Failed")){
//                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.FireBrick));
//                  //  ((MainViewHolder) holder).tv_status.setTextColor(Color.parseColor("#008000"));
//                }
//                if (StatusType.equals("Success")){
//                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Green));
//                }
//                if (StatusType.equals("Pending")){
//                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Chocolate));
//                }
//                if (StatusType.equals("Reversed")){
//                    ((MainViewHolder) holder).tv_status.setTextColor(ContextCompat.getColor(context,R.color.Magenta));
//                }

               // Log.e(TAG,"OperatorImagePath  891     "+jsonObject.getString("OperatorImagePath"));

                SharedPreferences pref =context.getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL=pref.getString("baseurl", null);
                String ProvidersImagePath = BASE_URL+ jsonObject.getString("OperatorImagePath");
               // Log.e(TAG,"ProvidersImagePath  8911    "+ProvidersImagePath);
                PicassoTrustAll.getInstance(context).load(ProvidersImagePath).error(R.drawable.no_image).into(((MainViewHolder) holder).iv_operator);

                ((MainViewHolder) holder).ll_main.setTag(position);
                ((MainViewHolder) holder).ll_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
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
        LinearLayout ll_main;
        TextView txtv_ph,tv_status, txtv_date,txtv_amount;
        ImageView iv_operator;

        public MainViewHolder(View v) {
            super(v);

            iv_operator = v.findViewById(R.id.iv_operator);
            tv_status = v.findViewById(R.id.tv_status);
            txtv_ph = v.findViewById(R.id.txtv_ph);
            txtv_amount = v.findViewById(R.id.txtv_amount);
            txtv_date = v.findViewById(R.id.txtv_date);
            ll_main = v.findViewById(R.id.ll_main);

        }
    }
}
