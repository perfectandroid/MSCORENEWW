package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.TransactionDetailActivity;
import com.creativethoughts.iscore.db.dao.NewTransactionDAO;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class PassbookListAdapter extends ArrayAdapter<Transaction> {

    Context mCtx;
    int listLayoutRes;
    List<Transaction> transactionList;
    NewTransactionDAO newTransactionDAO;

    public PassbookListAdapter(Context mCtx, int listLayoutRes,List<Transaction>transactionList) {
        super(mCtx, listLayoutRes,transactionList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.transactionList = transactionList;

    }

    public void clearAll() {
        transactionList.clear();


        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);



        Transaction transaction = transactionList.get(position);


        //getting views
        TextView txtTransactionYear = view.findViewById(R.id.tv_date);

       TextView txtTransactionAmount = view.findViewById(R.id.trans_amount);
        TextView txtTransactionChequeNo = view.findViewById(R.id.tv_chequeNo);
        TextView txtTransactiontype = view.findViewById(R.id.trans_type);

        TextView txtTransactionnarration = view.findViewById(R.id.narration);


        //getting the view
        String date = transaction.getEffectDate();
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        String date1 = convetDateFormat.format(Date.parse(date));
        txtTransactionYear.setText(date1);

        String transtype = transaction.getTransType();
        if(transtype.equals("C"))
        {
            String type="Cr";
            txtTransactiontype.setText(type);
            txtTransactiontype.setTextColor(ContextCompat.getColor(getContext(), R.color.Green));
            txtTransactionAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.Green));
            if (transaction.isNew) {
                txtTransactiontype.setTypeface(txtTransactiontype.getTypeface(), Typeface.BOLD);
            } else {
                txtTransactiontype.setTypeface(txtTransactiontype.getTypeface(), Typeface.NORMAL);
            }
        }
        else if(transtype.equals("D"))
        {
            String type="Dr";
            txtTransactiontype.setText(type);
            txtTransactiontype.setTextColor(ContextCompat.getColor(getContext(), R.color.FireBrick));
            txtTransactionAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.FireBrick));
            if (transaction.isNew) {
                txtTransactiontype.setTypeface(txtTransactiontype.getTypeface(), Typeface.BOLD);
            } else {
                txtTransactiontype.setTypeface(txtTransactiontype.getTypeface(), Typeface.NORMAL);
            }
        }


        double num = Double.parseDouble(transaction.getAmount());
        DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//        txtTransactionAmount.setText("\u20B9 "+ fmt.format(num));
        txtTransactionAmount.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));


        String chequeno =transaction.getChequeNo();
        if(chequeno.equals("")||chequeno.isEmpty())
        {
            txtTransactionChequeNo.setVisibility(View.GONE);
            txtTransactionChequeNo.setText("");

        }
        else
        {
            txtTransactionChequeNo.setVisibility(View.VISIBLE);
            String chequNo = "Cheque No - " + chequeno;
            txtTransactionChequeNo.setText( chequNo );
        }

        String narration =transaction.getNarration();
        if(narration.equals("")||narration.isEmpty())
        {
            txtTransactionnarration.setVisibility(View.GONE);
            txtTransactionnarration.setText("");

        }
        else
        {
            txtTransactionnarration.setVisibility(View.VISIBLE);
            txtTransactionnarration.setText(transaction.narration);
        }

      //  txtTransactionChequeNo.setText(transaction.getNarration());

         view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 TransactionDetailActivity.openTransactionDetails(mCtx, transaction);
             }
         });
        return view;
    }



    /*public void add(Transaction sortedList) {
        transactionList.add(sortedList);
        //  transList.add((Transaction) transList1);
        //    mHeadChildHashMap.put(header, child);

       // notifyDataSetChanged();
    }*/
}
