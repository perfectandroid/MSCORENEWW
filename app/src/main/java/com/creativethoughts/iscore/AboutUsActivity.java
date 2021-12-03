package com.creativethoughts.iscore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        try {
            SharedPreferences companypref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
            ImageView imCompanylogo = findViewById(R.id.imCompanylogo);
            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);
            String strimagepath1 = BASE_URL + companypref.getString("CompanyLogoImageCode", null);
           //   String strimagepath1 = "https://play-lh.googleusercontent.com/o4P4Bi80PUysmO-y5ulsJJ4H-Vb6AkRMPNtGxtMzBLmobS3DP_DHu6bqu79_iSIBew=w500";
           // PicassoTrustAll.getInstance(this).load(R.d).into(imCompanylogo);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class AboutUsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about_us);
//    }
//}
