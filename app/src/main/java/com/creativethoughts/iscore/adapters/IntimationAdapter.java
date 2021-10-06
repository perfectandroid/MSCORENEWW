package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class IntimationAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public IntimationAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_intimation_details, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                if(position %2 == 1){
                    holder.itemView.setBackgroundColor(Color.parseColor("#CED1D1"));
                }else{
                    holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                }


                ((MainViewHolder) holder).tvNoticeDate.setText(jsonObject.getString("NoticeDate"));
                ((MainViewHolder)holder).tvName.setText(jsonObject.getString("NoticeTypeName"));
                ((MainViewHolder)holder).tvAccno.setText(jsonObject.getString("AccountNo"));
                ((MainViewHolder)holder).tvAcctype.setText(jsonObject.getString("AccountType"));
                ((MainViewHolder)holder).tvDueAmt.setText("₹ "+ CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("DueAmount"))));
            }
        } catch (JSONException /*| ParseException*/ e) {
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
        TextView tvName,tvAccno,tvAcctype,tvNoticeDate,tvDueAmt;
        public MainViewHolder(View v) {
            super(v);
            tvName=v.findViewById(R.id.tvName);
            tvAccno=v.findViewById(R.id.tvAccno);
            tvAcctype=v.findViewById(R.id.tvAcctype);
            tvNoticeDate=v.findViewById(R.id.tvNoticeDate);
            tvDueAmt=v.findViewById(R.id.tvDueAmt);
        }
    }


}
