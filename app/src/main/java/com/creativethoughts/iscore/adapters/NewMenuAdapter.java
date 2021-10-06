package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativethoughts.iscore.MenuItem;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by muthukrishnan on 19/08/16.
 */
public class NewMenuAdapter extends BaseExpandableListAdapter {

    public OnItemClickListener mOnItemClickListener;
    ArrayList<MenuItem> mItems = new ArrayList<>();
    private Context mContext;

    public NewMenuAdapter(Context context, OnItemClickListener onItemClickListener) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(ArrayList<MenuItem> items) {
        mItems.clear();

        if (items != null) {
            mItems.addAll(items);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mItems.get(groupPosition).subItems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItems.get(groupPosition).subItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.menu_item, parent, false);
//        convertView.setBackgroundColor(R.color.slate);
        MenuItem menuItem = mItems.get(groupPosition);

        updateView(rowView, menuItem);

        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.menu_item, parent, false);

        MenuItem menuItem = mItems.get(groupPosition).subItems.get(childPosition);

        updateView(rowView, menuItem);

        return rowView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void updateView(final View rowView, final MenuItem menuItem) {
        TextView textView = (TextView) rowView.findViewById(R.id.txtMenuItem);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgMenuIcon);


        TextView txtMesssageCount = (TextView) rowView.findViewById(R.id.txtMessageCount);

        textView.setText(menuItem.getMenuLabel());

        txtMesssageCount.setVisibility(View.INVISIBLE);

        if ("Messages".equalsIgnoreCase(menuItem.getMenuLabel())) {
            UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();

            int messageCount = 0;

            if (TextUtils.isEmpty(userDetails.customerId) == false) {
                messageCount =
                        PBMessagesDAO.getInstance().getMessageUnReadCount(userDetails.customerId);
            }

            if (messageCount > 0) {
                txtMesssageCount.setText(String.valueOf(messageCount));

                txtMesssageCount.setVisibility(View.VISIBLE);
            }
        } else if ("Offers".equalsIgnoreCase(menuItem.getMenuLabel())) {
            UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();

            int offerCount = 0;

            if (TextUtils.isEmpty(userDetails.customerId) == false) {
                offerCount =
                        PBMessagesDAO.getInstance().getOffersUnReadCount(userDetails.customerId);
            }

            if (offerCount > 0) {
                txtMesssageCount.setText(String.valueOf(offerCount));

                txtMesssageCount.setVisibility(View.VISIBLE);
            }
        }

        imageView.setImageResource(menuItem.getMenuIcon());

        if(menuItem.id != 5 &&  menuItem.id != 10 &&menuItem.id != 19) {
            rowView.setOnClickListener(v -> {
                if(menuItem.id != 5 &&  menuItem.id != 10) {
                    mOnItemClickListener.onSelect(menuItem.id);
                }

            });
        }
    }

    public interface OnItemClickListener {
        public void onSelect(int position);
    }

}
