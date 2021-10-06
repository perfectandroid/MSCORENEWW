package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SingleBranchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SearchHistoryAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String ID_Recharge,RechargeDate,AccountNo,MobileNo,RechargeRs;

    public SearchHistoryAdapter(Context context, JSONArray jsonArray) {
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


                ((MainViewHolder) holder).txtv_ph.setText(MobileNo);

                double num =Double.parseDouble(RechargeRs);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
                ((MainViewHolder) holder).txtv_amount.setText("\u20B9 "+ fmt.format(num));
                ((MainViewHolder) holder).txtv_date.setText("Done On "+RechargeDate);
                if (OperatorName.startsWith("Airtel"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.airtel);
                if (OperatorName.startsWith("Jio"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.mipmap.jio);
                if (OperatorName.startsWith("BSNL"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.bsnal_3g);
                if (OperatorName.startsWith("Dish TV"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.dishtv);
                if (OperatorName.startsWith("Big TV"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.bigtv);
                if (OperatorName.startsWith("Tata Sky"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.tata_sky);
                if (OperatorName.startsWith("Sun"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.sun_direct);
                if (OperatorName.startsWith("NetConnect"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.reliance);
                if (OperatorName.startsWith("Tata Photon +"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.docomo);
                if (OperatorName.startsWith("Mbrowse"))
                    ((MainViewHolder) holder).iv_operator.setImageResource(R.drawable.mts);

                ((MainViewHolder) holder).ll_main.setTag(position);
                ((MainViewHolder) holder).ll_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });
              //  ((MainViewHolder) holder).imnext.setTag(position);
               /* ((MainViewHolder) holder).imnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            Intent i = new Intent(context, SingleLoanActivity.class);
                            i.putExtra("ID_BcLoanAccount", jsonObject.getString("ID_BcLoanAccount"));
                            i.putExtra("CustomerMobileNumber", jsonObject.getString("CustomerMobileNumber"));
                            i.putExtra("strdate", strdate);
                            context.startActivity(i);
                            ((Activity) context).finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });*/
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
        TextView txtv_ph, txtv_date,txtv_amount;
        ImageView iv_operator;

        public MainViewHolder(View v) {
            super(v);

            iv_operator = v.findViewById(R.id.iv_operator);
            txtv_ph = v.findViewById(R.id.txtv_ph);
            txtv_amount = v.findViewById(R.id.txtv_amount);
            txtv_date = v.findViewById(R.id.txtv_date);
            ll_main = v.findViewById(R.id.ll_main);

        }
    }
}
