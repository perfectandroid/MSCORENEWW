package com.creativethoughts.iscore.Recharge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class CircleAdapter  extends RecyclerView.Adapter{

    String TAG = "OperatorAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    OnItemClickListener onItemClickListener;

    public CircleAdapter(Context context, JSONArray arrayHeader) {
        this.context=context;
        this.jsonArray=arrayHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh ;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.oprator_list, parent, false);

        vh = new CircleAdapter.MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            ((CircleAdapter.MainViewHolder)holder).tva_oprator.setText(""+jsonObject.getString("ID_Recharge"));

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
        TextView tva_oprator;
        LinearLayout ll_operators;
        public MainViewHolder(View v) {
            super(v);

            tva_oprator=v.findViewById(R.id.tva_oprator);
            ll_operators=v.findViewById(R.id.ll_operators);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (onItemClickListener != null) onItemClickListener.onItemClick(view,getAdapterPosition(),"circle","0");
        }
    }

}
