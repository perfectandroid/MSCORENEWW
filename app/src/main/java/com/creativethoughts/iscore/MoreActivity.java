package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_version, tv_abt_us, tv_contact_us, tv_rate_us, tv_feed_back, tv_faq,tv_features;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        intViews();
        setRegViews();
        tv_version.setText("Version : "+ IScoreApplication.versionName);
    }
    private void intViews() {
        tv_version=findViewById(R.id.tv_version);
        tv_abt_us=findViewById(R.id.tv_abt_us);
        tv_contact_us=findViewById(R.id.tv_contact_us);
        tv_rate_us=findViewById(R.id.tv_rate_us);
        tv_feed_back=findViewById(R.id.tv_feed_back);
        tv_faq=findViewById(R.id.tv_faq);
        tv_features=findViewById(R.id.tv_feat);
    }

    private void setRegViews() {

        tv_abt_us.setOnClickListener(this);
        tv_contact_us.setOnClickListener(this);
        tv_rate_us.setOnClickListener(this);
        tv_feed_back.setOnClickListener(this);
        tv_faq.setOnClickListener(this);
        tv_features.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.tv_abt_us:
                intent=new Intent(MoreActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_contact_us:
                intent=new Intent(MoreActivity.this, ContactusActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_feat:
                intent=new Intent(MoreActivity.this, FeaturesActvity.class);
                startActivity(intent);
                break;
            case R.id.tv_rate_us:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MoreActivity.this);
                alertDialogBuilder.setMessage("Do you love this app? Please rate us.");
                alertDialogBuilder.setPositiveButton("Rate Now",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                /*        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(goToMarket);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                        }*/
                                final String appPackageName = MoreActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.tv_feed_back:
                intent=new Intent(MoreActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_faq:
                intent=new Intent(MoreActivity.this, FAQActivity.class);
                startActivity(intent);
                break;
        }
    }
//    {
//        Intent intent;
//        switch (v.getId()){
//            case R.id.tv_abt_us:
//                intent=new Intent(MoreActivity.this, AboutUsActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.tv_contact_us:
//                intent=new Intent(MoreActivity.this, ContactusActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.tv_feat:
//                intent=new Intent(MoreActivity.this, FeaturesActvity.class);
//                startActivity(intent);
//                break;
//            case R.id.tv_rate_us:
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//                alertDialogBuilder.setMessage("Do you love this app? Please rate us.");
//                        alertDialogBuilder.setPositiveButton("Rate Now",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface arg0, int arg1) {
//                                /*        Uri uri = Uri.parse("market://details?id=" + getPackageName());
//                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                                        try {
//                                            startActivity(goToMarket);
//                                        } catch (ActivityNotFoundException e) {
//                                            startActivity(new Intent(Intent.ACTION_VIEW,
//                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                                        }*/
//                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                                        try {
//                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                                        } catch (android.content.ActivityNotFoundException anfe) {
//                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                                        }
//                                    }
//                                });alertDialogBuilder.setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface arg0, int arg1) {
//                                    }
//                                });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                break;
//            case R.id.tv_feed_back:
//                intent=new Intent(MoreActivity.this, FeedbackActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.tv_faq:
//                intent=new Intent(MoreActivity.this, FAQActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }
}
