package com.creativethoughts.iscore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.money_transfer.AddSenderFragment;

public class AddSenderReceiverActivity extends AppCompatActivity {

    public static void openActivity(Context context, boolean isForSender) {

        Intent intent = new Intent(context, AddSenderReceiverActivity.class);

        intent.putExtra("isSender", isForSender);

        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender_receiver);

        boolean isForSender = getIntent().getBooleanExtra("isSender", true );

        final Fragment fragment;

        Toolbar toolbar =   findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (isForSender) {
            fragment = new AddSenderFragment();

            if (toolbar != null) {
                getSupportActionBar().setTitle("Add New Sender");
            }
        } else {
            fragment = new AddReceiverFragment();

            if (toolbar != null) {
                getSupportActionBar().setTitle("Add New Receiver");
            }
        }

        fragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if ( item.getItemId() ==  android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
