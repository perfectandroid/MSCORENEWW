
package com.creativethoughts.iscore.money_transfer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.neftrtgs.OtherBankFundTransferServiceChooserFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FundTransferActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    String reqmode,token,cusid;
    private ViewPager viewPager;
    LinearLayout ll_standingins,ll_notice,llviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_accsummary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        llviews = (LinearLayout)findViewById(R.id.llviews);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {


        try {
            FundTransferActivity.ViewPagerAdapter adapter = new FundTransferActivity.ViewPagerAdapter(getSupportFragmentManager());
            SharedPreferences ImpsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF45, 0);
            SharedPreferences RtgsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF46, 0);
            SharedPreferences NeftSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF48, 0);

            if (NeftSP.getString("Neft","").equals("true") &&
                    RtgsSP.getString("Rtgs","").equals("true") && ImpsSP.getString("Imps","").equals("true")){

                if (!ImpsSP.getString("Imps","").equals("true")){
                    adapter.addFragment(new OwnBankFundTransferServiceChooserFragment(), "OWN BANK");
                }else {
                    adapter.addFragment(new OwnBankFundTransferServiceChooserFragment(), "OWN BANK");
                    adapter.addFragment(new OtherBankFundTransferServiceChooserFragment(), "OTHER BANK");
                }

            }else {
                adapter.addFragment(new OwnBankFundTransferServiceChooserFragment(), "OWN BANK");
                adapter.addFragment(new OtherBankFundTransferServiceChooserFragment(), "OTHER BANK");
            }

            viewPager.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

//package com.creativethoughts.iscore.money_transfer;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.creativethoughts.iscore.R;
//import com.creativethoughts.iscore.neftrtgs.OtherBankFundTransferServiceChooserFragment;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FundTransferActivity extends AppCompatActivity {
//
//    ProgressDialog progressDialog;
//    private Toolbar toolbar;
//    private TabLayout tabLayout;
//    String reqmode,token,cusid;
//    private ViewPager viewPager;
//    LinearLayout ll_standingins,ll_notice,llviews;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_accsummary);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        viewPager = findViewById(R.id.viewPager);
//        setupViewPager(viewPager);
//
//        tabLayout = findViewById(R.id.tabLayout);
//        tabLayout.setupWithViewPager(viewPager);
//
//        llviews = (LinearLayout)findViewById(R.id.llviews);
//
//
//    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        FundTransferActivity.ViewPagerAdapter adapter = new FundTransferActivity.ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new OwnBankFundTransferServiceChooserFragment(), "OWN BANK");
//        adapter.addFragment(new OtherBankFundTransferServiceChooserFragment(), "OTHER BANK");
//        viewPager.setAdapter(adapter);
//
//
//    }
//
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//
//
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }