package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountSummaryAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    JSONObject jObject=null;
    Context context;

    public AccountSummaryAdapter(Context context, JSONArray jsonArray) {
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
            if (holder instanceof MainViewHolder) {
                if(position %2 == 1){
                   // holder.itemView.setBackgroundColor(Color.parseColor("#AFC0D3"));
                }else{
                  //  holder.itemView.setBackgroundColor(Color.parseColor("#CED1D1"));
                }

                if(jarray.length()!=0) {
                    GridLayoutManager lLayout = new GridLayoutManager(context, 2);
                    ((MainViewHolder)holder).rv_accountSummaryDetails.setLayoutManager(lLayout);
                    ((MainViewHolder)holder).rv_accountSummaryDetails.setHasFixedSize(true);
                    AccountSummaryListAdapter adapter = new AccountSummaryListAdapter(context, jarray);
                    ((MainViewHolder)holder).rv_accountSummaryDetails.setAdapter(adapter);
                }

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
        RecyclerView rv_accountSummaryDetails;
        public MainViewHolder(View v) {
            super(v);
            rv_accountSummaryDetails=v.findViewById(R.id.rv_accountSummaryDetails);
        }
    }
}
