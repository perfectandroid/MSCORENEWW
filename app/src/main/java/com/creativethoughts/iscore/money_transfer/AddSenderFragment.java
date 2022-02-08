package com.creativethoughts.iscore.money_transfer;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.TransactionOTPFragment;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSenderFragment extends Fragment implements View.OnClickListener {


    private AppCompatEditText mFirstNameEt;
    private AppCompatEditText mLastNameEt;
    private AppCompatEditText mMobileNumberEt;

    private TextView mDOBTv;

    private SweetAlertDialog mSweetAlertDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sender, container, false);

        mFirstNameEt = view.findViewById(R.id.first_name);
        mLastNameEt = view.findViewById(R.id.last_name);
        mMobileNumberEt = view.findViewById(R.id.mobile_number);

        mDOBTv = view.findViewById(R.id.txtDOB);
        String defaultDate = "01-01-1990";
        mDOBTv.setText( defaultDate );

        mDOBTv.setOnClickListener(this);

        Button button = view.findViewById(R.id.btn_submit);
        button.setOnClickListener(this);

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if ( Character.isLetter(source.charAt(i))) {
                    return null;
                }
                else if ( Character.isSpaceChar(source.charAt(i)))
                    return " ";
                else if ( Character.isDigit(source.charAt(i)))
                    return "";

                else return "";
            }
            return "";
        };
        mFirstNameEt.setFilters(new InputFilter[] {filter});
        mLastNameEt.setFilters(new InputFilter[] {filter});
        loadSenderDetails();
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.txtDOB:
                showDOBDatePicker();
                break;
            case R.id.btn_submit:
                if (isValid()) {

                    if (NetworkUtil.isOnline()) {
                        String firstName = mFirstNameEt.getText().toString();
                        String lastName = mLastNameEt.getText().toString();
                        String mobileNumber = mMobileNumberEt.getText().toString();
                        getObservable( firstName, lastName, mobileNumber, mDOBTv.getText().toString() )
                                .subscribeOn(Schedulers.io() )
                                .observeOn(AndroidSchedulers.mainThread() )
                                .subscribe( getObserver() );
                        //new AddSenderAsyncTask(firstName, lastName, mobileNumber, mDOBTv.getText().toString()).execute();

                    } else {
                        DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");
                    }

                }
                break;
        }
    }

    private boolean isValid() {

        String firstName = mFirstNameEt.getText().toString();
        String lastName = mLastNameEt.getText().toString();
        String mobileNumber = mMobileNumberEt.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEt.setError("Please enter first name");

            return false;
        }
        mFirstNameEt.setError(null);

        if (TextUtils.isEmpty(lastName)) {
            mLastNameEt.setError("Please enter last name");

            return false;
        }
        mLastNameEt.setError(null);

        if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberEt.setError("Please enter mobile number");

            return false;
        }

        if (mobileNumber.length() > 10 || mobileNumber.length() < 10) {
            mMobileNumberEt.setError("Please enter valid 10 digit mobile number");

            return false;
        }

        mMobileNumberEt.setError(null);

        return true;
    }

    private void showDOBDatePicker() {
        String fromParse = mDOBTv.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null;
        try {
            date = formatter.parse(fromParse);
        } catch (ParseException e) {
           //Do nothing
        }


        final Calendar c = Calendar.getInstance();
        c.setTime(date);


        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(1990, 1,1);

        if ( getActivity() == null )
            return;

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dateOfBirth = getTwoDigitNumber(dayOfMonth) + "-" + getTwoDigitNumber(monthOfYear + 1) + "-" + year1;
                    mDOBTv.setText( dateOfBirth );
                    mDOBTv.setError(null);
                }, 1990, 0, 1);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();
    }

    private String getTwoDigitNumber(int value) {
        return new DecimalFormat("00").format(value);
    }

    private void loadSenderDetails(){
        SharedPreferences customerNameSP = getActivity().getSharedPreferences(Config.SHARED_PREF28, 0);
        mFirstNameEt.setText( customerNameSP.getString("customerName","") );
        SharedPreferences customerAddress1SP = getActivity().getSharedPreferences(Config.SHARED_PREF29, 0);
        mLastNameEt.setText( customerAddress1SP.getString("customerAddress1","") );
        SharedPreferences mobileNoSP = getActivity().getSharedPreferences(Config.SHARED_PREF31, 0);
        mMobileNumberEt.setText( mobileNoSP.getString("mobileNo","") );
    }
    private Observable<AddSenderReceiverResponseModel> getObservable( String firstName, String lastName, String mobileNumber, String dob ){
        if ( getActivity()!= null ){
            mSweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE );
            mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mSweetAlertDialog.setTitleText( "Adding sender" );
            mSweetAlertDialog.setCancelable( false );
            mSweetAlertDialog.show();
        }
        return Observable.fromCallable(  ()-> addSenderToServer(firstName, lastName, mobileNumber, dob ));
    }
    private Observer<AddSenderReceiverResponseModel> getObserver(){
        return new Observer<AddSenderReceiverResponseModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AddSenderReceiverResponseModel addSenderResponseModel ) {
                if ( mSweetAlertDialog != null )
                    mSweetAlertDialog.dismissWithAnimation();
                if ( getContext() == null || getActivity() == null )
                    return;

                try{
                    if ( addSenderResponseModel.getOtpRefNo().equals("0") && addSenderResponseModel.getStatusCode().equals("200") ){
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setCustomImage(R.drawable.aappicon)
                                .setConfirmText("Ok!")
                                .showCancelButton(true)
                                .setTitleText(addSenderResponseModel.getStatus())
                                .setContentText( addSenderResponseModel.getMessage() )
                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();

                    }
                    else if ( !addSenderResponseModel.getOtpRefNo().equals("0") && addSenderResponseModel.getStatusCode().equals("200") ){

//                        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                        String BASE_URL=pref.getString("oldbaseurl", null);
                        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                        String BASE_URL=pref.getString("baseurl", null);


                        String mOtpResendLink = BASE_URL + "/MTResendSenderOTP?senderid=" + IScoreApplication.encodedUrl(addSenderResponseModel.getIdSender());
                        TransactionOTPFragment.openSenderOTP( getActivity(),   addSenderResponseModel , mOtpResendLink, true);
                        getActivity().finish();
                    }
                    else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setCustomImage(R.drawable.aappicon)
                                .setConfirmText("Ok!")
                                .showCancelButton(true)
                                .setTitleText(addSenderResponseModel.getStatus())
                                .setContentText( addSenderResponseModel.getMessage() )
                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();
                    }


                }catch ( Exception e ){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE )
                            .setTitleText( "Oops....!" )
                            .setContentText( IScoreApplication.SERVICE_NOT_AVAILABLE )
                            .show();
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
    private AddSenderReceiverResponseModel addSenderToServer(String firstName, String lastName, String mobileNumber, String dob) {

        try {


            SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
            String custId =customerIdSP.getString("customerId","");
            custId = custId.trim();
            firstName = firstName.trim();
            lastName = lastName.trim();
            dob = dob.trim();
            mobileNumber = mobileNumber.trim();

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            String url =
                    BASE_URL + "/MTAddnewsender?sender_fname="
                            + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(firstName))
                            + "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId))
                            + "&sender_lname=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(lastName))
                            + "&sender_dob=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(dob))
                            + "&sender_mobile=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mobileNumber));

            String strResponse = ConnectionUtil.getResponse(url);
            Log.e("TAG","url   3144   "+url);
            Log.e("TAG","strResponse   3145   "+strResponse);

            try {
                Gson gson = new Gson();
                Log.e("TAG","gson   3141   "+gson);
                return gson.fromJson( strResponse, AddSenderReceiverResponseModel.class );
            }catch ( Exception e){
                Log.e("TAG","Exception   3142   "+e.toString());
                return new AddSenderReceiverResponseModel();
            }
        } catch (Exception e) {
            //Do nothing
            Log.e("TAG","Exception   3143   "+e.toString());
        }
        return new AddSenderReceiverResponseModel();
    }
}
