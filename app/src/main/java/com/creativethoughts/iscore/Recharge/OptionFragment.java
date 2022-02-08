package com.creativethoughts.iscore.Recharge;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.RechargeFragment;

public class OptionFragment extends Fragment implements View.OnClickListener {

    String TAG = "OptionFragment";
    RelativeLayout rltv_prepaid;
    RelativeLayout rltv_dth;
    RelativeLayout rltv_landline;
    RelativeLayout rltv_postpaid;
    RelativeLayout rltv_datacard;

    AlertDialog alertDialog2=null;
    private AlertDialog.Builder builder;

    SharedPreferences RechargeSP = null;
    SharedPreferences KsebSP = null;

    public OptionFragment() {

    }
    public static OptionFragment newInstance() {
        OptionFragment fragment = new OptionFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

//    public static OptionFragment newInstance() {
//        OptionFragment optionFragment = new OptionFragment();
//        return optionFragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        rltv_prepaid = view.findViewById(R.id.rltv_prepaid);
        rltv_dth = view.findViewById(R.id.rltv_dth);
        rltv_landline = view.findViewById(R.id.rltv_landline);
        rltv_postpaid = view.findViewById(R.id.rltv_postpaid);
        rltv_datacard = view.findViewById(R.id.rltv_datacard);

        rltv_prepaid.setOnClickListener(this);
        rltv_dth.setOnClickListener(this);
        rltv_landline.setOnClickListener(this);
        rltv_postpaid.setOnClickListener(this);
        rltv_datacard.setOnClickListener(this);

        RechargeSP = getActivity().getSharedPreferences(Config.SHARED_PREF44, 0);
        //ImpsSP = getActivity().getSharedPreferences(Config.SHARED_PREF45, 0);
        // RtgsSP = getActivity().getSharedPreferences(Config.SHARED_PREF46, 0);
        KsebSP = getActivity().getSharedPreferences(Config.SHARED_PREF47, 0);
        //  NeftSP = getActivity().getSharedPreferences(Config.SHARED_PREF48, 0);
        //  OwnImpsSP = getActivity().getSharedPreferences(Config.SHARED_PREF49, 0);



        return view;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        assert getFragmentManager() != null;
        switch (view.getId()) {
            case R.id.rltv_prepaid:
//                fragment = RechargeFragment.newInstance(0);
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for Prepaid option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
                        fragment = new RechargeFragment().newInstance(0);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_dth:
                //DO something
//                fragment = RechargeFragment.newInstance(0);
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for DTH option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
                        fragment = new RechargeFragment().newInstance(3);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.rltv_landline:
                //DO something
                Log.e(TAG, "CLICK   75");
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for LandLine option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
                        fragment = new RechargeFragment().newInstance(2);
                    }
                }catch (Exception e){
                    Log.e(TAG,"Exception  140    "+e.toString());
                    e.printStackTrace();

                }


                break;

            case R.id.rltv_postpaid:
                //DO something
                Log.e(TAG, "CLICK   82");

                builder = new AlertDialog.Builder(getContext());
                try {

                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for Postpaid option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
                        fragment = new RechargeFragment().newInstance(1);
                        alertDialog2.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case R.id.rltv_datacard:
                //DO something
                Log.e(TAG, "CLICK   89");
//                fragment = RechargeFragment.newInstance(0);
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for DataCard option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
                        fragment = new RechargeFragment().newInstance(4);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }

    }

    private void goingTo(View view) {

        int tempId = view.getId();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        ActionBar actionBar = activity.getSupportActionBar();
        Fragment fragment = null;
        switch (tempId) {
            case R.id.rltv_prepaid:

                break;
        }
    }



}
