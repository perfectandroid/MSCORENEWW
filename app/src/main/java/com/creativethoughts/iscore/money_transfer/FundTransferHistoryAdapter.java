package com.creativethoughts.iscore.money_transfer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.adapters.AccountSummaryAdapter;
import com.creativethoughts.iscore.adapters.AccountSummaryListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FundTransferHistoryAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    JSONObject jObject=null;
    Context context;

    public FundTransferHistoryAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_accountsummary, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            JSONArray jarray = jsonObject.getJSONArray("Details");

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
        RecyclerView rv_accountSummaryDetails;
        public MainViewHolder(View v) {
            super(v);
            rv_accountSummaryDetails=v.findViewById(R.id.rv_accountSummaryDetails);
        }
    }
}
