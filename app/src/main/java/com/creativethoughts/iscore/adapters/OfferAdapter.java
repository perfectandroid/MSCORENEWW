package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder>{

    String TAG = "OfferAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;

    public OfferAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView messageHead, time, detail;


        public MyViewHolder(View view) {
            super(view);
            messageHead = (TextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
            detail = (TextView) view.findViewById(R.id.detail);
        }
    }

    @Override
    public OfferAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_message, parent, false);

        return new OfferAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        try {
            jsonObject=jsonArray.getJSONObject(position);
            holder.messageHead.setText(jsonObject.getString("messageHead"));
            holder.detail.setText(jsonObject.getString("messageDetail"));

            if(!jsonObject.getString("messageDate").equals("")) {
                holder.time.setText(CommonUtilities.getFormatedMsgDate(jsonObject.getString("messageDate")));
            } else {
                holder.time.setText("");
            }

        }catch (Exception e){

            Log.e(TAG,"Exception   57   "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

}
