package com.creativethoughts.iscore.Recharge;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.RechargeFragment;
import com.creativethoughts.iscore.db.dao.DynamicMenuDao;
import com.creativethoughts.iscore.db.dao.model.DynamicMenuDetails;

public class OptionFragment extends Fragment implements View.OnClickListener {

    String TAG = "OptionFragment";
    RelativeLayout rltv_prepaid;
    RelativeLayout rltv_dth;
    RelativeLayout rltv_landline;
    RelativeLayout rltv_postpaid;
    RelativeLayout rltv_datacard;

    private DynamicMenuDetails dynamicMenuDetails;
    AlertDialog alertDialog2=null;
    private AlertDialog.Builder builder;
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


        return view;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        assert getFragmentManager() != null;
        switch (view.getId()) {
            case R.id.rltv_prepaid:
//                fragment = RechargeFragment.newInstance(0);
                dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (IScoreApplication.decryptStart(dynamicMenuDetails.getRecharge()).equals("0")) {
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
                dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (IScoreApplication.decryptStart(dynamicMenuDetails.getRecharge()).equals("0")) {
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
                dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (IScoreApplication.decryptStart(dynamicMenuDetails.getRecharge()).equals("0")) {
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
                dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
                builder = new AlertDialog.Builder(getContext());
                try {

                    if (IScoreApplication.decryptStart(dynamicMenuDetails.getRecharge()).equals("0")) {
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
                dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
                builder = new AlertDialog.Builder(getContext());
                try {
                    if (IScoreApplication.decryptStart(dynamicMenuDetails.getRecharge()).equals("0")) {
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
