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

public class StandingInstructionAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public StandingInstructionAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_standinginstrctn, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                /*if(position %2 == 1){
                    holder.itemView.setBackgroundColor(Color.parseColor("#CED1D1"));
                }else{
                    holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                }*/
             /*   if(!jsonObject.getString("Date").isEmpty()) {
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String dateinput = jsonObject.getString("Date");
                    Date date = null;
                    date = inputFormat.parse(dateinput);
                    String dateoutput = outputFormat.format(date);
                    ((MainViewHolder) holder).tvdate.setText(dateinput);
                }*/
                ((MainViewHolder) holder).tvdate.setText(jsonObject.getString("Date"));
              //  ((MainViewHolder)holder).tvSLNO.setText(""+(position+1));
                ((MainViewHolder)holder).tvSource.setText(/*jsonObject.getString("SourceCustomer")+"\n("+*/jsonObject.getString("SourceAccountNo")/*+")"*/);
                ((MainViewHolder)holder).tvDestination.setText(jsonObject.getString("DestCustomer")+"\n("+jsonObject.getString("DestAccountNo")+")");

                double num =Double.parseDouble(jsonObject.getString("Amount"));
                ((MainViewHolder)holder).tvamount.setText("₹ "+ CommonUtilities.getDecimelFormate(num));
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
        TextView tvSLNO,tvSource,tvDestination,tvdate,tvamount;
        public MainViewHolder(View v) {
            super(v);
            tvSource=v.findViewById(R.id.tvSource);
            tvSLNO=v.findViewById(R.id.tvSLNO);
            tvDestination=v.findViewById(R.id.tvDestination);
            tvdate=v.findViewById(R.id.tvdate);
            tvamount=v.findViewById(R.id.tvamount);
        }
    }



}
