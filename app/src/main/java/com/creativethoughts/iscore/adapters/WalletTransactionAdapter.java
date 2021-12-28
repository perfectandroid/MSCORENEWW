package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class WalletTransactionAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public WalletTransactionAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_wallettranslist, parent, false);
        vh = new WalletTransactionAdapter.MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof WalletTransactionAdapter.MainViewHolder) {

                ((MainViewHolder) holder).txtv_date.setText(jsonObject.getString("Date"));
                ((MainViewHolder)holder).tvSLNO.setText(jsonObject.getString("sl"));
                ((MainViewHolder)holder).txtv_crdr.setText(jsonObject.getString("DrCr"));
                ((MainViewHolder)holder).txtv_amount.setText("₹ "+ CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Amount"))));

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
        TextView  txtv_amount;
        TextView txtv_crdr;
        TextView tvSLNO;
        TextView txtv_date;
        public MainViewHolder(View v) {
            super(v);

            txtv_amount=v.findViewById(R.id.txtv_amount);
            txtv_crdr=v.findViewById(R.id.txtv_crdr);
            tvSLNO=v.findViewById(R.id.tvSLNO);
            txtv_date=v.findViewById(R.id.txtv_date);
        }
    }
}


