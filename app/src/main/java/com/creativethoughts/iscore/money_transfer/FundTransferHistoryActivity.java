package com.creativethoughts.iscore.money_transfer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

public class FundTransferHistoryActivity extends AppCompatActivity {


    RecyclerView rv_ownfund_history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundtransfer_history);

        rv_ownfund_history = (RecyclerView)findViewById(R.id.rv_ownfund_history);

        getDataFromServer();
    }

    private void getDataFromServer() {


    }
}
