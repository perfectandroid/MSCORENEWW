package com.creativethoughts.iscore.Recharge;

import androidx.fragment.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Recharge.OptionFragment;

public class RechargeActivity extends AppCompatActivity {

   // OptionFragment fragment;
    Fragment fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacharge);



        if (fragment==null){
//            fragment = RechargeFragment.newInstance(0);
            fragment = new OptionFragment().newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();


        }

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container, RechargeFragment)
//                .commit();
    }
}
