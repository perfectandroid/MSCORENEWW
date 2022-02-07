package com.creativethoughts.iscore.money_transfer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.creativethoughts.iscore.ListSavedBeneficiaryActivity;
import com.creativethoughts.iscore.QuickPayMoneyTransferActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.neftrtgs.ListSavedBeneficiaryFragment;

import androidx.appcompat.app.AppCompatActivity;


public class OtherbankFundTransferActivity extends AppCompatActivity {

    String TAG  = "OtherbankFundTransferActivity";
    String mMode = "";
    ListSavedBeneficiaryFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_bank_fundtransfer);

        mMode = getIntent().getStringExtra("mMode");
        Log.e(TAG,"mMode  22   "+mMode);


        if (mMode.equals("NEFT") || mMode.equals("RTGS") || mMode.equals("IMPS")){
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,ListSavedBeneficiaryFragment.newInstance(mMode))
                    .commit();*/

            Intent i = new Intent(OtherbankFundTransferActivity.this, ListSavedBeneficiaryActivity.class);
            i.putExtra("mode",mMode);
            startActivity(i);
        }
        if (mMode.equals("QKPY")){
        /*    getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, QuickPayMoneyTransferFragment.newInstance())
                    .commit();*/
            Intent i = new Intent(OtherbankFundTransferActivity.this, QuickPayMoneyTransferActivity.class);
            startActivity(i);
        }



    }

}
