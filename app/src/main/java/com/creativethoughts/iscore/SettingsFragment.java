package com.creativethoughts.iscore;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.SyncAll;
import com.creativethoughts.iscore.utility.SyncUtils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Button mApplyBtn;
    private Spinner mDaySpinner;
    private Spinner mDefaultAccountSpinner;

    private Spinner mHourSpinner;
    private Spinner mMinSpinner;


    private int mSelectedHours = 0;
    private int mSelectedMinute = 0;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {

        SettingsFragment fragment = new SettingsFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        mDaySpinner =   rootView.findViewById(R.id.spnUpdateDays);

        mHourSpinner =   rootView.findViewById(R.id.hoursSpinner);
        mMinSpinner =  rootView.findViewById(R.id.minutesSpinner);


        mDefaultAccountSpinner =   rootView.findViewById(R.id.spnDefAcc);

        if (settingsModel == null) {
            CommonUtilities.setAccountNumber(null, mDefaultAccountSpinner, getActivity());

            setDaysForDropDown(null);
            updateIntervalHours(0);
            updateIntervalMinutes(1);
        } else {
            mSelectedHours = settingsModel.hours;
            mSelectedMinute = settingsModel.minutes;

            CommonUtilities.setAccountNumber(settingsModel.customerId, mDefaultAccountSpinner,
                    getActivity());

            setDaysForDropDown(String.valueOf(settingsModel.days));
            updateIntervalHours(mSelectedHours);
            updateIntervalMinutes(mSelectedMinute);

        }

        mApplyBtn =   rootView.findViewById(R.id.btnApply);
        mApplyBtn.setOnClickListener(this);

        return rootView;
    }



    public void onClick(View v) {
        if (v == mApplyBtn) {
            mSelectedHours = Integer.parseInt(mHourSpinner.getSelectedItem().toString());
            mSelectedMinute = Integer.parseInt(mMinSpinner.getSelectedItem().toString());

            if (mSelectedHours == 0 && mSelectedMinute == 0) {
                DialogUtil.showAlert(getActivity(),
                        "Please give minimum 15 minutes  interval for data update");

            } else {
                if (mSelectedHours == 0 && mSelectedMinute < 15) {
                    DialogUtil.showAlert(getActivity(),
                            "Please give minimum 15 minutes  interval for data update");
                } else {
                    if (NetworkUtil.isOnline()) {
                        String days = mDaySpinner.getSelectedItem().toString();

                      //  String accountNumber = mDefaultAccountSpinner.getSelectedItem().toString();
                        String accountNumber = "001001001055";
                        SettingsDAO.getInstance()
                                .insertValues(days, mSelectedHours, mSelectedMinute, accountNumber);

                        SyncUtils.startAlarmManage(getActivity());

                        final ProgressDialog pDialog =
                                ProgressDialog.show(getActivity(), "", "Updating to server...");

                        SyncAll.syncAllAccounts(new SyncAll.OnSyncStateListener() {
                            @Override
                            public void onCompleted() {
                                Toast.makeText(getActivity(),
                                        "Application will update data in every12 " + mSelectedHours + ":" + mSelectedMinute + " hours",
                                        Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(getActivity(),
                                        "Application will update data in every23 " + mSelectedHours + ":" + mSelectedMinute + " hours",
                                        Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                            }
                        }, false, getContext());
                    } else {
                        DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");
                    }


                }
            }
        }
    }

    private void updateIntervalHours(int hours) {
        ArrayList<String> hoursItems = new ArrayList<>();

        for(int i = 0; i < 24; i++) {
            hoursItems.add(String.format("%02d", i));
        }

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(getActivity(), R.layout.simple_spinner_item_dark, hoursItems);

        mHourSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mHourSpinner.setSelection(hours);
    }

    private void updateIntervalMinutes(int minutes) {
        ArrayList<String> minutesItems = new ArrayList<>();
        minutesItems.add("00");
        minutesItems.add("15");
        minutesItems.add("30");
        minutesItems.add("45");

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(getActivity(), R.layout.simple_spinner_item_dark, minutesItems);

        mMinSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        switch (minutes) {
            case 0:
                mMinSpinner.setSelection(0);
                break;
            case 15:
                mMinSpinner.setSelection(1);
                break;
            case 30:
                mMinSpinner.setSelection(2);
                break;
            case 45:
                mMinSpinner.setSelection(3);
                break;
            default:
                break;
        }
    }

    private void setDaysForDropDown(String days) {
        Activity activity = getActivity();

        if ( activity == null )
            return;

        ArrayList<String> items = new ArrayList<>();
        items.add("7");
        items.add("10");
        items.add("14");
        items.add("30");
        items.add("60");
        items.add("120");
        items.add("150");
        items.add("180");

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(activity, R.layout.simple_spinner_item_dark, items);

        mDaySpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (days == null) {
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            String day = items.get(i);

            if (TextUtils.isEmpty(day)) {
                continue;
            }

            if (day.equalsIgnoreCase(days)) {
                mDaySpinner.setSelection(i);

                break;
            }
        }
    }
}
