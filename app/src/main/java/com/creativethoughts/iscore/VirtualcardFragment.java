package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VirtualcardFragment extends Fragment {
    ProgressDialog progressDialog;
    TextView txtv_purpose;
    TextView txtv_points1;
    TextView txtv_points2;
    TextView txtv_points3;
    TextView txtv_points4;
    public VirtualcardFragment() {
        // Required empty public constructor
    }
    public static VirtualcardFragment newInstance() {
        VirtualcardFragment fragment = new VirtualcardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_virtualcard, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        txtv_purpose =  rootView.findViewById(R.id.txtv_purpose);
        txtv_points1  =  rootView.findViewById(R.id.txtv_points1);
        txtv_points2  =  rootView.findViewById(R.id.txtv_points2);
        txtv_points3  =  rootView.findViewById(R.id.txtv_points3);
        txtv_points4  =  rootView.findViewById(R.id.txtv_points4);



        tabs.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        tabs.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {

            progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(5900);
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Adapter adapter = new Adapter(getChildFragmentManager());
                                adapter.addFragment(new BarcodeFragment(), "Frontview");
                                adapter.addFragment(new QRFragment(), "Backview");
                                viewPager.setAdapter(adapter);

                                progressDialog.dismiss();
                                txtv_purpose.setText(R.string.purpose);
                                txtv_purpose.setVisibility(View.VISIBLE);
                                txtv_points1.setVisibility(View.VISIBLE);
                                txtv_points2.setVisibility(View.VISIBLE);
                                txtv_points3.setVisibility(View.VISIBLE);
                                txtv_points4.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }

                }
            }.start();


}


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(),HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }
}
