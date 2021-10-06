package com.creativethoughts.iscore;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.adapters.SpinnerAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private Spinner spnOrderBy;
    private Spinner spnAccountNumber;
    private Spinner spnTransitionStatus;
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parametunters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        TextView fromDate;
        TextView toDate;
        EditText maxAmount;
        EditText minAmount;
        RadioGroup group;


       /* ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ImageButton im_back = rootView.findViewById(R.id.im_back);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                im_back.setBackgroundColor(getResources().getColor(R.color.grey));
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
            }
        });*/


        Button btnGo = rootView.findViewById(R.id.btnSearchGo);
        fromDate = rootView.findViewById(R.id.txtFromDate);
        toDate = rootView.findViewById(R.id.txtToDate);

        maxAmount = rootView.findViewById(R.id.edtMaxAmount);
        minAmount = rootView.findViewById(R.id.edtMinAmount);

        spnAccountNumber = rootView.findViewById(R.id.spnAccountNum);

        spnOrderBy = rootView.findViewById(R.id.spnAscending);
        spnTransitionStatus = rootView.findViewById(R.id.spnTransType);



        group = rootView.findViewById(R.id.radioGroupSortBy);

        setOrderBy();
        setTransactionType();
       /* SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
        CommonUtilities.setAccountNumber( settingsModel.customerId, spnAccountNumber, getActivity());*/

        btnGo.setOnClickListener(v -> {

            String strFromDate = fromDate.getText().toString();
            String strToDate = toDate.getText().toString();

            if (strFromDate.equals("")) {

                showToast("From Date should not be empty");

            } else if (strToDate.equals("")) {

                showToast("To Date should not be empty");

            }
            else if (!strFromDate.equals("") && !strToDate.equals("")) {

                String fromParse = strFromDate + " " + "00:00"; // Results in "2-5-2012 20:43"
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "d-M-yyyy hh:mm", Locale.ENGLISH ); // I assume d-M, you may refer to M-d for month-day instead.
                Date date = null;
                try {
                    date = formatter.parse(fromParse);
                } catch (ParseException e) {
                    //Do nothing
                } // You will need try/catch around this
                assert date != null;
                long frommillis = date.getTime();

                long lDateTime = new Date().getTime();


                String toParse = strToDate + " " + "00:00"; // Results in "2-5-2012 20:43"
                SimpleDateFormat formatter1 = new SimpleDateFormat(
                        "d-M-yyyy hh:mm", Locale.ENGLISH ); // I assume d-M, you may refer to M-d for month-day instead.
                Date date1 = null;
                try {
                    date1 = formatter1.parse(toParse);
                } catch (ParseException e) {
                    if ( IScoreApplication.DEBUG )
                        Log.e("Exceptiondate", e.toString() );
                } // You will need try/catch around this
                assert date1 != null;
                long tomillis = date1.getTime();


                long diff = frommillis - tomillis;
                long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
                int daysdiff = (int) diffDays;


                if (frommillis > lDateTime) {
                    showToast("Future Date should not allow");

                } else if (tomillis > lDateTime) {
                    showToast("Future Date should not allow");

                } else if (daysdiff > 1) {
                    showToast("To Date should be greater than from date");

                } else {


                    if (NetworkUtil.isOnline()) {
                        String extractedAccNo = spnAccountNumber.getSelectedItem().toString();
                        extractedAccNo = extractedAccNo.replace(extractedAccNo.substring(extractedAccNo.indexOf(" (")+1, extractedAccNo.indexOf(")")+1), "");
                        extractedAccNo = extractedAccNo.replace(" ","");

                        Intent passBookAccount =
                                new Intent(getActivity(), SearchResultActivity.class);
                        passBookAccount.putExtra("fromDate", strFromDate);
                        passBookAccount.putExtra("toDate", strToDate);
                        passBookAccount.putExtra("maxAmount1", maxAmount.getText().toString());
                        passBookAccount.putExtra("minAmount1", minAmount.getText().toString());
                        passBookAccount
                                .putExtra("order", spnOrderBy.getSelectedItem().toString());
                        passBookAccount.putExtra("transType",
                                spnTransitionStatus.getSelectedItem().toString());

                        passBookAccount.putExtra("acno",
                                extractedAccNo);

                        if (group.getCheckedRadioButtonId() == R.id.dateRadio) {
                            passBookAccount.putExtra("sortField", "Date");
                        } else {
                            passBookAccount.putExtra("sortField", "Amount");
                        }

                        startActivity(passBookAccount);
                    } else {
                        DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");
                    }

                }
            }

        });

        final Calendar c = Calendar.getInstance();
        int year;
        int month;
        int day;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        String tempDateDisplayString = day + "-" + (month + 1) + "-" + year;
        fromDate.setText( tempDateDisplayString );
        toDate.setText(tempDateDisplayString);

        fromDate.setOnClickListener(v -> {

            String fromParse = fromDate.getText().toString() + " " + "00:00"; // Results in "2-5-2012 20:43"
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "d-M-yyyy hh:mm", Locale.ENGLISH ); // I assume d-M, you may refer to M-d for month-day instead.
            Date date = null;
            try {
                date = formatter.parse(fromParse);
            } catch (ParseException e) {
               if ( IScoreApplication.DEBUG )
                   Log.e( "exception", e.toString() );
            } // You will need try/catch around this


            final Calendar c1 = Calendar.getInstance();
            c1.setTime(date);

            int year1;
            int month1;
            int day1;
            year1 = c1.get(Calendar.YEAR);
            month1 = c1.get(Calendar.MONTH);
            day1 = c1.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    (view, year11, monthOfYear, dayOfMonth) -> {
                        // Display Selected date in textbox
                        String tempStringFromDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year11;
                        fromDate.setText( tempStringFromDate );
                        fromDate.setError(null);
                    }, year1, month1, day1);
            dpd.show();
        });


        toDate.setOnClickListener(v -> {

            // Get current date by calender

            String fromParse = toDate.getText().toString() + " " + "00:00"; // Results in "2-5-2012 20:43"
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "d-M-yyyy hh:mm", Locale.ENGLISH ); // I assume d-M, you may refer to M-d for month-day instead.
            Date date = null;
            try {
                date = formatter.parse(fromParse);
            } catch (ParseException e) {
                if ( IScoreApplication.DEBUG )
                    Log.e( "exception", e.toString() );
            } // You will need try/catch around this


            final Calendar c12 = Calendar.getInstance();
            c12.setTime(date);

            int year12;
            int month12;
            int day12;
            year12 = c12.get(Calendar.YEAR);
            month12 = c12.get(Calendar.MONTH);
            day12 = c12.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    (view, year121, monthOfYear, dayOfMonth) -> {
                        // Display Selected date in textbox
                        String tempToDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year121;
                        toDate.setText( tempToDate );
                        toDate.setError(null);
                    }, year12, month12, day12);
            dpd.show();
        });

        return rootView;
    }

    private void showToast(String value) {
        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
    }


    private void setOrderBy() {
        Activity activity = getActivity();
        if ( activity == null )
            return;
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter( activity );

        spinnerAdapter.addItem("Ascending");
        spinnerAdapter.addItem("Descending");

        spnOrderBy.setAdapter(spinnerAdapter);

    }

    private void setTransactionType() {
        Context context = getContext();
        if ( context == null )
            return;
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context);

        spinnerAdapter.addItem("All");
        spinnerAdapter.addItem("Credit");
        spinnerAdapter.addItem("Debit");

        spnTransitionStatus.setAdapter(spinnerAdapter);
        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
        List<String> accountNos = PBAccountInfoDAO.getInstance().getAccountNos();
        try{
            String accNo = settingsModel.customerId;
            int index = accountNos.indexOf( accNo );
            accountNos.remove( index );
            accountNos.add( 0, accNo );
        }catch ( Exception e ){
            //Do nothing
        }
        if (accountNos.isEmpty()) {
            return;
        }
        SpinnerAdapter accountSpinnerAdapter = new SpinnerAdapter(getContext());
        for (String item: accountNos
             ) {
            accountSpinnerAdapter.addItem(item);
        }
        spnAccountNumber.setAdapter(accountSpinnerAdapter);


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
