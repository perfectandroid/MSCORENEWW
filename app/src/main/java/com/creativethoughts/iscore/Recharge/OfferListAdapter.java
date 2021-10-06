package com.creativethoughts.iscore.Recharge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfferListAdapter extends RecyclerView.Adapter {

    Context context;
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    OnItemClickListener onItemClickListener;

    public OfferListAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh ;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_offerlist, parent, false);

        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            ((MainViewHolder)holder).tv_amount.setText(""+jsonObject.getString("amount"));
            ((MainViewHolder)holder).tv_validity.setText("Validity : "+jsonObject.getString("validity"));
            ((MainViewHolder)holder).tv_description.setText(""+jsonObject.getString("description"));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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

    private class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_amount,tv_validity,tv_description;
        public MainViewHolder(View v) {
            super(v);

            tv_amount=v.findViewById(R.id.tv_amount);
            tv_validity=v.findViewById(R.id.tv_validity);
            tv_description=v.findViewById(R.id.tv_description);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) onItemClickListener.onItemClick(view,getAdapterPosition(),tv_amount.getText().toString(),"1");
        }
    }
}
