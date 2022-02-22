package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountSplitBalanceDetailsAdapter extends RecyclerView.Adapter {
    String TAG = "AccountSplitBalanceDetailsAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public AccountSplitBalanceDetailsAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_splitlist, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
//                ((MainViewHolder)holder).tvkey.setText(jsonObject.getString("Key"));
//                ((MainViewHolder)holder).tvvalue.setText(jsonObject.getString("Value"));

                if (jsonObject.getString("Key").equals("NetAmount") ){

                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(date);
                    ((MainViewHolder)holder).tvkey.setText("Due As On "+ formattedDate+"");
                    ((MainViewHolder)holder).tvvalue.setText(jsonObject.getString("Value"));
                    ((MainViewHolder)holder).v_sep.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).tvkey.setTextColor(Color.parseColor("#B22222"));
                    ((MainViewHolder)holder).tv_sep.setTextColor(Color.parseColor("#B22222"));
                    ((MainViewHolder)holder).tvvalue.setTextColor(Color.parseColor("#B22222"));
                }

                else {

                    ((MainViewHolder)holder).tvkey.setText(jsonObject.getString("Key"));
                    ((MainViewHolder)holder).tvvalue.setText(jsonObject.getString("Value"));
                    ((MainViewHolder)holder).v_sep.setVisibility(View.GONE);
                }

                if (jsonObject.getString("Key").equals("Total Liability Amount") ){

                    ((MainViewHolder)holder).ll_liability.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_main.setVisibility(View.GONE);


                    ((MainViewHolder)holder).tvkey1.setText("Total Liability Amount");
                    ((MainViewHolder)holder).tvvalue1.setText(jsonObject.getString("Value"));
                    ((MainViewHolder)holder).tvkey1.setTextColor(Color.parseColor("#63B456"));
                    ((MainViewHolder)holder).tv_sep1.setTextColor(Color.parseColor("#63B456"));
                    ((MainViewHolder)holder).tvvalue1.setTextColor(Color.parseColor("#63B456"));
                }

//                Log.e(TAG,"Value   57   "+jsonObject.getString("Value"));
//                double num =Double.parseDouble(""+jsonObject.getString("Value"));
//                ((MainViewHolder)holder).tvvalue.setText(""+ CommonUtilities.getDecimelFormate(num));
                if (jsonObject.getString("Key").equals("Pending Installment")) {
                    ((MainViewHolder)holder).tvkey.setTextColor(Color.parseColor("#000000"));
                    ((MainViewHolder)holder).tv_sep.setTextColor(Color.parseColor("#000000"));
                    ((MainViewHolder)holder).tvvalue.setTextColor(Color.parseColor("#000000"));
                    ((MainViewHolder)holder).llDetails.setBackgroundColor(Color.parseColor("#B0E0E6"));
//                    ((MainViewHolder)holder).llDetails.setBackgroundColor(Color.parseColor("#B5D5FE"));
                }
                if (jsonObject.getString("Key").equals("BalanceInstallment") ) {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
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
        TextView  tvkey,tvvalue,tv_sl_no,tv_sep;
        TextView  tvkey1,tvvalue1,tv_sl_no1,tv_sep1;
        LinearLayout llDetails;
        LinearLayout ll_main;
        LinearLayout ll_liability;
        ImageView imSeperator;
        View v_sep;
        public MainViewHolder(View v) {
            super(v);
            tv_sl_no=v.findViewById(R.id.tv_sl_no);
            v_sep=v.findViewById(R.id.v_sep);
            tvvalue=v.findViewById(R.id.tvvalue);
            tv_sep=v.findViewById(R.id.tv_sep);
            tvkey=v.findViewById(R.id.tvkey);
            llDetails=v.findViewById(R.id.llDetails);
           // imSeperator=v.findViewById(R.id.imSeperator);

            tv_sl_no1=v.findViewById(R.id.tv_sl_no1);
            tvvalue1=v.findViewById(R.id.tvvalue1);
            tv_sep1=v.findViewById(R.id.tv_sep1);
            tvkey1=v.findViewById(R.id.tvkey1);

            ll_main=v.findViewById(R.id.ll_main);
            ll_liability=v.findViewById(R.id.ll_liability);
        }
    }
}
