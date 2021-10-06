package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.model.Message;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.ArrayList;

/**
 * Created by muthukrishnan on 06/10/15. <BR>
 * <p/>
 * This adapter class is used to populate the Message and the offer in offer screen in list view.
 */
public class MessageAdapter extends BaseAdapter {

    ArrayList<Message> mMessages = new ArrayList<Message>();

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public MessageAdapter(Context context) {
        mContext = context;

        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setMessages(ArrayList<Message> messages) {

        mMessages.clear();

        // Add only when the given message array is not null.
        if (messages != null) {
            mMessages.addAll(messages);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = mMessages.get(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.message_item_row, null);
        }

        TextView messageHead = (TextView) convertView.findViewById(R.id.message);

        TextView time = (TextView) convertView.findViewById(R.id.time);

        TextView detail = (TextView) convertView.findViewById(R.id.detail);

        messageHead.setText(message.head);

        if(TextUtils.isEmpty(message.date) == false) {
            time.setText(CommonUtilities.getFormatedMsgDate(message.date));
        } else {
            time.setText("");
        }

        detail.setText(message.detail);

        if (message.isSeen) {
            messageHead.setTypeface(messageHead.getTypeface(), Typeface.NORMAL);
        } else {
            messageHead.setTypeface(messageHead.getTypeface(), Typeface.BOLD);
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }
}
