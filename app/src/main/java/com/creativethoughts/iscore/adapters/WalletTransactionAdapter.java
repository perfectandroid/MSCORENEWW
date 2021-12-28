package com.creativethoughts.iscore.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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


                ((MainViewHolder) holder).txtv_date.setText(jsonObject.getString("Date"));
                ((MainViewHolder)holder).tvSLNO.setText(jsonObject.getString("sl"));
                ((MainViewHolder)holder).txtv_crdr.setText(jsonObject.getString("DrCr"));
                ((MainViewHolder)holder).txtv_amount.setText("₹ "+ CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Amount"))));
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
        TextView txtv_date,txtv_amount,txtv_crdr,tvSLNO;
        public MainViewHolder(View v) {
            super(v);
            txtv_amount=v.findViewById(R.id.txtv_amount);
            txtv_crdr=v.findViewById(R.id.txtv_crdr);
            tvSLNO=v.findViewById(R.id.tvSLNO);
            txtv_date=v.findViewById(R.id.txtv_date);
        }
    }








}
