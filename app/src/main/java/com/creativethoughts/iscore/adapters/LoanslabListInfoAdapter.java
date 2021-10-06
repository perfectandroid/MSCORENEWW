package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SingleBranchActivity;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class LoanslabListInfoAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String slbDate,slbTotat,slbBalanceAmount;

    public LoanslabListInfoAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slab_list_row, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                slbDate=jsonObject.getString("Demand");
              /*  DateFormat outputFormat  = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todateinput=slbDate;
                Date date = null;
                try {
                    date = inputFormat.parse(todateinput);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String todateoutput = outputFormat.format(date);*/

                slbTotat=jsonObject.getString("Total");
                slbBalanceAmount=jsonObject.getString("Principal");


                ((MainViewHolder)holder).tv_sl.setText((position+1)+". ");
                ((MainViewHolder)holder).tv_Period.setText(jsonObject.getString("Period"));
//                ((MainViewHolder)holder).tv_Demand.setText(jsonObject.getString("Demand"));
                ((MainViewHolder)holder).tv_Demand.setText(jsonObject.getString("Demand"));
                ((MainViewHolder)holder).tv_Principal.setText("₹ "+ CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Principal"))));
                ((MainViewHolder)holder).tv_Total.setText("₹ "+CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Total"))));
                ((MainViewHolder)holder).tv_Interest.setText("₹ "+CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Interest"))));
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
        TextView tv_sl,tv_Period,tv_Demand,tv_Principal,tv_Interest,tv_Total;
        public MainViewHolder(View v) {
            super(v);
            tv_sl =v.findViewById(R.id.tv_sl);
            tv_Period =v.findViewById(R.id.tv_Period);
            tv_Demand =v.findViewById(R.id.tv_Demand);
            tv_Principal =v.findViewById(R.id.tv_Principal);
            tv_Interest =v.findViewById(R.id.tv_Interest);
            tv_Total =v.findViewById(R.id.tv_Total);
        }
    }

}