package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.model.BarcodeAgainstCustomerAccountList;
import com.creativethoughts.iscore.model.ToAccountDetails;

import java.util.ArrayList;

public class TodetailsAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<ToAccountDetails> arraylist;

    public TodetailsAdapter(Context context, ArrayList<ToAccountDetails> arraylist) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = arraylist;
    }

    public class ViewHolder {
        TextView txtv_acno,txtvbrnch;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public ToAccountDetails getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final TodetailsAdapter.ViewHolder holder;
        if (view == null) {
            holder = new TodetailsAdapter.ViewHolder();
            view = inflater.inflate(R.layout.list_content_spin, null);
            holder.txtv_acno = (TextView) view.findViewById(R.id.textview);
          //  holder.txtvbrnch = (TextView) view.findViewById(R.id.textview1);
            view.setTag(holder);
        } else {
            holder = (TodetailsAdapter.ViewHolder) view.getTag();
        }
        holder.txtv_acno.setText(arraylist.get(position).getAccountNumber());
     //   holder.txtvbrnch.setText(arraylist.get(position).getBranchName());


        return view;
    }
}