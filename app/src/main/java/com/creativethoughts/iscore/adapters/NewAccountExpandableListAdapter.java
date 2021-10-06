package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.TransactionDetailActivity;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by muthukrishnan on 06/10/15.
 */
public class NewAccountExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mListHeader = new ArrayList<>(); // header titles

    private HashMap<String, List<Transaction>> mHeadChildHashMap =
            new HashMap<>();

    public NewAccountExpandableListAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * To add the item and the child items in the list
     *
     * @param header string
     * @param child  child transaction for the coresponding header
     */
    public void addItem(String header, List<Transaction> child) {
        mListHeader.add(header);
        mHeadChildHashMap.put(header, child);

        notifyDataSetChanged();
    }

    /**
     * To clear all the item in list
     */
    public void clearAll() {
        mListHeader.clear();
        mHeadChildHashMap.clear();

        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mHeadChildHashMap.get(this.mListHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Transaction transaction = (Transaction) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.date_list_item, null);
        }

        final TextView txtTrasactionType        =   convertView.findViewById(R.id.trans_type);

        final TextView txtTransactionAmount     =   convertView.findViewById(R.id.trans_mount);

        final TextView txtTransactionChequeNo   =   convertView.findViewById(R.id.chequeNo);

        final TextView txtTransactionNarration  =   convertView.findViewById(R.id.narration);


        //As per the bug list on 22 Jan 2016 we dont need to show the effective TIME_IN_MILLISECOND in transaction page
        String debited = "Debited";
        String credited = "Credited";
        if ("C".equalsIgnoreCase(transaction.transType)) {
            txtTrasactionType.setText(credited);

            txtTransactionAmount.setTextColor(Color.parseColor("#666666"));
        } else {
            txtTrasactionType.setText(debited);

            txtTransactionAmount.setTextColor(Color.RED);
        }

        if (transaction.isNew) {
            txtTrasactionType.setTypeface(txtTrasactionType.getTypeface(), Typeface.BOLD);
        } else {
            txtTrasactionType.setTypeface(txtTrasactionType.getTypeface(), Typeface.NORMAL);
        }

        final String amountValue;

        if(!TextUtils.isEmpty(transaction.amount)) {
            amountValue = String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transaction.amount));
        } else {
            amountValue = transaction.amount;
        }
        String amount = "Rs. " + amountValue;
        txtTransactionAmount.setText( amount );

        if (TextUtils.isEmpty(transaction.chequeNo)) {
            txtTransactionChequeNo.setVisibility(View.GONE);
            txtTransactionChequeNo.setText("");
        } else {
            txtTransactionChequeNo.setVisibility(View.VISIBLE);
            String chequNo = "Cheque No - " + transaction.chequeNo;
            txtTransactionChequeNo.setText( chequNo );
        }

        if (TextUtils.isEmpty(transaction.narration)) {
            txtTransactionNarration.setVisibility(View.GONE);
            txtTransactionNarration.setText("");
        } else {
            txtTransactionNarration.setVisibility(View.VISIBLE);
            txtTransactionNarration.setText(transaction.narration);
        }

        convertView.setOnClickListener(new OnItemClickListener(transaction));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mHeadChildHashMap.get(this.mListHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.date_list_group, null);
        }

        TextView lblListHeader =  convertView.findViewById(R.id.lblDateListHeader);

        lblListHeader.setText(CommonUtilities.getDateString(headerTitle));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class OnItemClickListener implements View.OnClickListener {

        private Transaction mTransaction;
        private OnItemClickListener(Transaction transaction) {
            mTransaction = transaction;
        }

        @Override
        public void onClick(View v) {
            TransactionDetailActivity.openTransactionDetails(mContext, mTransaction);
        }
    }
}
