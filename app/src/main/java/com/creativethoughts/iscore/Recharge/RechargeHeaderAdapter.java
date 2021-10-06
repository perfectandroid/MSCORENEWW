package com.creativethoughts.iscore.Recharge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import java.util.ArrayList;


public class RechargeHeaderAdapter extends RecyclerView.Adapter  {

    int selPos = 0;
    Context context;
    ArrayList<String> jsonArray;
    OnItemClickListener onItemClickListener;

    public RechargeHeaderAdapter(Context context, ArrayList<String> arrayHeader) {
        this.context=context;
        this.jsonArray=arrayHeader;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh ;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_recharge_header, parent, false);

        vh = new MainViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {

            if (selPos == position){
                ((MainViewHolder)holder).tv_header.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.underline_tab));
            }else {
                ((MainViewHolder)holder).tv_header.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.underline_trans));;
            }
            ((MainViewHolder)holder).tv_header.setText(""+jsonArray.get(position));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
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
        TextView tv_header;
        public MainViewHolder(View v) {
            super(v);

            tv_header=v.findViewById(R.id.tv_header);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            selPos = getAdapterPosition();
            notifyDataSetChanged();
            if (onItemClickListener != null) onItemClickListener.onItemClick(view,getAdapterPosition(),tv_header.getText().toString(),"0");
        }
    }
}
