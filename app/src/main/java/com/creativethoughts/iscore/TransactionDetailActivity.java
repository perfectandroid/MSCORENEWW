package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.utility.CommonUtilities;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

/**
 * Created by muthukrishnan on 27/11/15 - 10:00 AM
 */
public class TransactionDetailActivity extends AppCompatActivity {

    private static final String BUNDLE_TRANSACTION_KEY = "transaction_key";
    private LinearLayout mDetailLayout;

    public static void openTransactionDetails(Context activity, Transaction transaction) {
        Intent intent = new Intent(activity, TransactionDetailActivity.class);
        intent.putExtra(BUNDLE_TRANSACTION_KEY, transaction);

        activity.startActivity(intent);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction_deatils);

      //  setTitle("Transaction Details");

        mDetailLayout = (LinearLayout) findViewById(R.id.detail_layout);


        Transaction transaction =
                (Transaction) getIntent().getSerializableExtra(BUNDLE_TRANSACTION_KEY);

        if (transaction == null) {
            finish();
        }

        assert transaction != null;

        updateView("Cheque No", transaction.chequeNo, R.drawable.chequeno);



        if (!TextUtils.isEmpty(transaction.chequeDate)) {
            updateView("Cheque Date", CommonUtilities.getFormatedDate(transaction.chequeDate), R.drawable.chequedate);
        } else {
            updateView("Cheque Date", "", R.drawable.chequedate);
        }

//        updateView("Remarks", transaction.remarks);

        if ("C".equalsIgnoreCase(transaction.transType)) {
            updateView("Trans Type", "Credit", R.drawable.transtype);
        } else {
            updateView("Trans Type", "Debit", R.drawable.transtype);
        }

        final String amountValue;

        if(!TextUtils.isEmpty(transaction.amount)) {
            amountValue = String.format("%.2f", Double.parseDouble(transaction.amount));
        } else {
            amountValue = transaction.amount;
        }


        double num = Double.parseDouble(amountValue);
        updateView("Amount", "\u20B9 "+ CommonUtilities.getDecimelFormate(num), R.drawable.amount);


        if (!TextUtils.isEmpty(transaction.narration)) {
            final TextView narrationDesc = (TextView) findViewById(R.id.narration_desc);
            assert narrationDesc != null;
            narrationDesc.setText(transaction.narration);
        }

    }



    private void updateView(String key, String value, int img) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.row_transaction_details, null);

        TextView keyTv = (TextView) view.findViewById(R.id.key);
        TextView valueTv = (TextView) view.findViewById(R.id.value);
        ImageView imgv1 = (ImageView) view.findViewById(R.id.imgv1);

        keyTv.setText(key);
        valueTv.setText(value);
        imgv1.setBackgroundResource(img);
        imgv1.setAdjustViewBounds(true);
        imgv1.setScaleType(ImageView.ScaleType.FIT_XY);
      //  keyTv.setCompoundDrawablesWithIntrinsicBounds(img, 0, 0, 0);
        mDetailLayout.addView(view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
