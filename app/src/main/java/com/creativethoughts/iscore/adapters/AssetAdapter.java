package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AssetAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String tasset;
    int [] a;

    public AssetAdapter(Context context, JSONArray jsonArray,  int [] a) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.a = a;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_piechart, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                tasset = jsonObject.getString("Account");

                ((MainViewHolder) holder).tvAsset.setText(tasset);
                ((MainViewHolder) holder).ivAsset.setBackgroundColor(a[position]);

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
        return jsonArray.length();
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView tvAsset;
        ImageView ivAsset;

        public MainViewHolder(View v) {
            super(v);
            tvAsset = v.findViewById(R.id.tvAsset);
            ivAsset = v.findViewById(R.id.ivAsset);

        }
    }
}
