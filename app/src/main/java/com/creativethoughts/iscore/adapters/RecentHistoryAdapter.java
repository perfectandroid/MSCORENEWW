package com.creativethoughts.iscore.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Recharge.OnItemClickListener;
import com.creativethoughts.iscore.RechargeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class RecentHistoryAdapter extends RecyclerView.Adapter{

    String TAG  ="RecentHistoryAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String ID_Recharge,RechargeDate,AccountNo,MobileNo,RechargeRs,StatusType;
    OnItemClickListener onItemClickListener;


    public RecentHistoryAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_recent_history, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {

                ID_Recharge = jsonObject.getString("ID_Recharge");
                RechargeDate = jsonObject.getString("RechargeDate");
                AccountNo = jsonObject.getString("AccountNo");
                MobileNo = jsonObject.getString("MobileNo");
                RechargeRs = ""+jsonObject.getInt("RechargeRs");
                StatusType = jsonObject.getString("StatusType");


                ((MainViewHolder) holder).txtv_ph.setText(MobileNo);

                double num =Double.parseDouble(RechargeRs);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
                ((MainViewHolder) holder).txtv_operator.setText(""+ jsonObject.getString("OperatorName"));
//                ((RecentHistoryAdapter.MainViewHolder) holder).txtv_amount.setText("\u20B9 "+ fmt.format(num));
                ((MainViewHolder) holder).txtv_amount.setText("\u20B9 "+ RechargeRs);
                ((MainViewHolder) holder).txtv_date.setText("Last Recharge On "+RechargeDate+" , "+StatusType);

                ((MainViewHolder) holder).ll_main.setTag(position);
                ((MainViewHolder) holder).ll_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("rechargeHistoryAdapter","rechargeHistoryAdapter   "+position);
//                        try {
//                            jsonObject = jsonArray.getJSONObject(position);
//                            Intent i = new Intent(context, SingleBranchActivity.class);
//                            i.putExtra("ID_Br", jsonObject.getString("ID_Branch"));
//                            i.putExtra("Lat", jsonObject.getString("LocationLatitude"));
//                            i.putExtra("Long", jsonObject.getString("LocationLongitude"));
//                            i.putExtra("BNK", jsonObject.getString("BankName"));
//                            context.startActivity(i);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        if (onItemClickListener != null) onItemClickListener.onItemClick(v,position,"h","1");

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


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout ll_main;
        TextView txtv_ph, txtv_date,txtv_amount,txtv_operator;

        public MainViewHolder(View v) {
            super(v);

            txtv_ph = v.findViewById(R.id.txtv_ph);
            txtv_amount = v.findViewById(R.id.txtv_amount);
            txtv_date = v.findViewById(R.id.txtv_date);
            txtv_operator = v.findViewById(R.id.txtv_operator);
            ll_main = v.findViewById(R.id.ll_main);

            v.setTag(v);
            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
          //  if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
            Log.e(TAG,"onClick    1375     "+getAdapterPosition());

        }
    }
}
