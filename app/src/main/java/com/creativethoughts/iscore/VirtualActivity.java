
package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VirtualActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    String reqmode,token,cusid;
    private ViewPager viewPager;
    LinearLayout ll_standingins,ll_notice,llviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_passbook);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        llviews = (LinearLayout)findViewById(R.id.llviews);


    }

    private void setupViewPager(ViewPager viewPager) {

        // DynamicMenuDetails dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
        try {
            VirtualActivity.ViewPagerAdapter adapter = new VirtualActivity.ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new VirtualcardFragment(), "Virtual Card");
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