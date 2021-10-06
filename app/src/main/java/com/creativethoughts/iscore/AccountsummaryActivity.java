package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class AccountsummaryActivity extends AppCompatActivity implements View.OnClickListener{
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

/*
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);*/



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        llviews = (LinearLayout)findViewById(R.id.llviews);
      //  llviews.setVisibility(View.VISIBLE);
        ll_notice = (LinearLayout)findViewById(R.id.ll_notice);
        ll_notice.setOnClickListener(this);
        ll_standingins =(LinearLayout)findViewById(R.id.ll_standingins);
        ll_standingins.setOnClickListener(this);



        ll_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //   Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
              /*  FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frame1,new NotificationPostingFragment());
                fr.commit();*/
                Intent i = new Intent(AccountsummaryActivity.this,NotificationPostingActivity.class);
                startActivity(i);

            }
        });
        ll_standingins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
             /*   StandingInstructionFragment fragment = new StandingInstructionFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame1, fragment);
                transaction.commit();*/

                Intent i = new Intent(AccountsummaryActivity.this,StandingInstructionActivity.class);
                startActivity(i);

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DepositFragment(), "Deposit");
        adapter.addFragment(new LoansFragment(), "Loan");
        viewPager.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {

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