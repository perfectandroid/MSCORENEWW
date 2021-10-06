package com.creativethoughts.iscore.money_transfer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment;
import com.creativethoughts.iscore.neftrtgs.ListSavedBeneficiaryFragment;


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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,ListSavedBeneficiaryFragment.newInstance(mMode))
                    .commit();
        }
        if (mMode.equals("QKPY")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,QuickPayMoneyTransferFragment.newInstance())
                    .commit();
        }



    }

}
