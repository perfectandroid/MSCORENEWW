package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.model.BarcodeAgainstCustomerAccountList;
import com.creativethoughts.iscore.model.CustomerModulesModel;

import java.util.ArrayList;

public class CustomListAdapter  extends BaseAdapter{
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<BarcodeAgainstCustomerAccountList> arraylist;

    public CustomListAdapter(Context context, ArrayList<BarcodeAgainstCustomerAccountList> arraylist) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = arraylist;
    }

    public class ViewHolder {
        TextView tvAccount;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public BarcodeAgainstCustomerAccountList getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final  ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.single_item_account, null);
            holder.tvAccount = (TextView) view.findViewById(R.id.tvAccount);
            view.setTag(holder);
        } else {
            holder = ( ViewHolder) view.getTag();
        }
        holder.tvAccount.setText(arraylist.get(position).getAccountNumber());
        return view;
    }
}