package com.creativethoughts.iscore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;

public class AboutUsActivity extends AppCompatActivity {

    public String TAG = "AboutUsActivity";
    ImageView imCompanylogo,img_applogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        imCompanylogo = findViewById(R.id.imCompanylogo);
        img_applogo = findViewById(R.id.img_applogo);
        try {



            SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
            String IMAGEURL = imageurlSP.getString("imageurl","");
            SharedPreferences CompanyLogoImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
            SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
            String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
            String companylogoPath =IMAGEURL+CompanyLogoImageCodeSP.getString("CompanyLogoImageCode","");

            PicassoTrustAll.getInstance(AboutUsActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
            PicassoTrustAll.getInstance(AboutUsActivity.this).load(companylogoPath).error(R.drawable.errorlogo).into(imCompanylogo);


        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Exception   "+e.toString());

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
