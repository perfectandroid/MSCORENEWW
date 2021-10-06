package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.model.SenderReceiver;

import java.util.ArrayList;
import java.util.List;

public class SenderReceiverSpinnerAdapter extends BaseAdapter {
    LayoutInflater mInflater = null;
    private List<SenderReceiver> mItems = new ArrayList<SenderReceiver>();
    private Context context;
    private boolean mIsSender;

    public SenderReceiverSpinnerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setIsSender(boolean isSender) {
        mIsSender = isSender;
    }

    public void clear() {
        mItems.clear();

        notifyDataSetChanged();
    }

    public void addItem(SenderReceiver spinnerModel) {
        if (spinnerModel == null) {
            return;
        }

        mItems.add(spinnerModel);

        notifyDataSetChanged();
    }

    public void addItems(List<SenderReceiver> spinnerModels) {
        mItems.clear();

        if (spinnerModels != null && spinnerModels.size() > 0) {
            mItems.addAll(spinnerModels);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {

            view = mInflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mInflater.inflate(R.layout.
                    toolbar_spinner_item, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        if(mIsSender) {
            if(position == 0) {
                return mItems.get(position).senderName;
            }
            return position >= 0 && position < mItems.size() ? (mItems.get(position).senderName + "(" + mItems.get(position).senderMobile + ")") : "";
        } else {
            if(position == 0) {
                return mItems.get(position).senderName;
            }
            return position >= 0 && position < mItems.size() ? (mItems.get(position).senderName + "(" + mItems.get(position).receiverAccountno + ")"): "";
        }
    }
}
