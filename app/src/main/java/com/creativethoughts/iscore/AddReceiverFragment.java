package com.creativethoughts.iscore;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.adapters.SenderReceiverSpinnerAdapter;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.money_transfer.AddSenderReceiverResponseModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddReceiverFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AddReceiverFragment.class.getSimpleName();
    private final ArrayList<SenderReceiver> mSenderReceivers = new ArrayList<>();
    private AppCompatEditText mReceiverEt;
    private AppCompatEditText mMobileNumberEt;
    private AppCompatEditText mIFSCCodeEt;
    private AppCompatEditText mAccountNumberEt;
    private AppCompatEditText mConfirmAccountNumberEt;
    private ProgressDialog mProgressDialog;
    private Spinner mSenderSpinner;
    private String url;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_receiver, container, false);

        mSenderSpinner = view.findViewById(R.id.sender_name_spinner);

        mReceiverEt = view.findViewById(R.id.receiver_name);
        mMobileNumberEt = view.findViewById(R.id.mobile_number);
        mIFSCCodeEt = view.findViewById(R.id.ifsc_code);
        mAccountNumberEt = view.findViewById(R.id.account_number);
        mConfirmAccountNumberEt = view.findViewById(R.id.confirm_account_number);

        Button button = view.findViewById(R.id.btn_submit);
        button.setOnClickListener(this);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            //noinspection LoopStatementThatDoesntLoop
            for (int i = start; i < end; i++) {

                if ( Character.isLetter(source.charAt(i))) {
                    return null;
                }
                else if ( Character.isSpaceChar(source.charAt(i)))
                    return " ";
                else return "";
            }
            return "";
        };
        mReceiverEt.setFilters(new InputFilter[] {filter});

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new SenderReceiverAsyncTask().execute();
    }

    private void setSenderSpinner() {

        SenderReceiverSpinnerAdapter spinnerAdapter = new SenderReceiverSpinnerAdapter(getActivity());
        spinnerAdapter.setIsSender(true);

        spinnerAdapter.addItems(mSenderReceivers);

        mSenderSpinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_submit:
                if (isValid()) {
                    if (NetworkUtil.isOnline()) {
                        SenderReceiver senderReceiver = ((SenderReceiver) mSenderSpinner.getSelectedItem());

                        String sender = String.valueOf(senderReceiver.userId);

                        String receiverName = mReceiverEt.getText().toString();
                        String mobileNumber = mMobileNumberEt.getText().toString();
                        String ifscCode = mIFSCCodeEt.getText().toString();
                        String accNumber = mAccountNumberEt.getText().toString();

                        new AddSenderAsyncTask(sender, receiverName, mobileNumber, ifscCode, accNumber).execute();
                    } else {
                        DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");
                    }
                }
                break;
        }
    }

    private boolean isValid() {
        SenderReceiver senderReceiver = ((SenderReceiver) mSenderSpinner.getSelectedItem());

        if (senderReceiver == null) {
            if (mSenderReceivers.isEmpty()) {
                showToast("Please add minimum one sender, and then add receiver");
            }
            return false;
        }

        if(senderReceiver.fkSenderId == -100) {
            showToast("Please select sender");

            return false;
        }

        String receiverName = mReceiverEt.getText().toString();
        String mobileNumber = mMobileNumberEt.getText().toString();
        String ifscCode = mIFSCCodeEt.getText().toString();
        String accNumber = mAccountNumberEt.getText().toString();
        String confirmAccNumber = mConfirmAccountNumberEt.getText().toString();

        if (TextUtils.isEmpty(receiverName)) {
            mReceiverEt.setError("Please enter receiver name");

            return false;
        }
        mReceiverEt.setError(null);

        if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberEt.setError("Please enter mobile number");

            return false;
        }

        if (mobileNumber.length() > 10 || mobileNumber.length() < 10) {
            mMobileNumberEt.setError("Please enter valid 10 digit mobile number");
            return false;
        }

        mMobileNumberEt.setError(null);

        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(mMobileNumberEt.getText().toString());
        }catch (Exception e){
            return false;
        }

        if (TextUtils.isEmpty(ifscCode)) {
            mIFSCCodeEt.setError("Please enter IFSC code");

            return false;
        }
        else {
            if (! isIfscIsValid(ifscCode)){
                mIFSCCodeEt.setError("Invalid ifsc");
                return false;
            }
        }
        mIFSCCodeEt.setError(null);

        if (TextUtils.isEmpty(accNumber)) {
            mAccountNumberEt.setError("Please enter account number");

            return false;
        }
        mAccountNumberEt.setError(null);

        if (TextUtils.isEmpty(confirmAccNumber)) {
            mConfirmAccountNumberEt.setError("Please enter confirm account number");

            return false;
        }

        if (!accNumber.equalsIgnoreCase(confirmAccNumber)) {
            mConfirmAccountNumberEt.setError("Account number and Confirm Account number not matching");

            return false;
        }
        mConfirmAccountNumberEt.setError(null);

        return true;
    }

    private AddSenderReceiverResponseModel addReceiverToServer(String sender, String receiver, String mobileNumber, String ifscCode, String accountNumber) {



        try {

            UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

            String custId = user.customerId;

            url =
                    CommonUtilities.getUrl() + "/MTAddnewreceiver?senderid=" +

                            IScoreApplication.encodedUrl(IScoreApplication.encryptStart(sender.trim()))
                            + "&receiver_name=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart( receiver.trim()))
                            + "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId))
                            + "&receiver_mobile=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mobileNumber.trim()) )
                            + "&receiver_IFSCcode=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(ifscCode.trim()))
                            + "&receiver_accountno=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountNumber.trim()));

            String strResponse = ConnectionUtil.getResponse(url);

            Gson gson = new Gson();
            return gson.fromJson( strResponse, AddSenderReceiverResponseModel.class );

        } catch (Exception e) {
            return new AddSenderReceiverResponseModel();
        }
    }

    private void showToast(String value) {
        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
    }

    private boolean isIfscIsValid(String ifsCode){

        return ifsCode.length() > 0;
    }

    private class AddSenderAsyncTask extends AsyncTask<String, android.R.integer, AddSenderReceiverResponseModel > {
        private final String mSender;
        private final String mReceiver;
        private final String mMobileNumber;
        private final String mIfscCode;
        private final String mAccountNo;

        private AddSenderAsyncTask(String sender, String receiver, String mobileNumber, String ifscCode, String accountNumber) {
            mSender = sender;
            mReceiver = receiver;
            mMobileNumber = mobileNumber;
            mIfscCode = ifscCode;
            mAccountNo = accountNumber;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show( getActivity(), "", "Adding Receiver..." );
        }

        @Override
        protected AddSenderReceiverResponseModel doInBackground(String... params) {

            return addReceiverToServer(mSender, mReceiver, mMobileNumber, mIfscCode, mAccountNo);
        }

        @Override
        protected void onPostExecute( AddSenderReceiverResponseModel addSenderResponseModel ) {
            mProgressDialog.dismiss();
            Context context = getContext();
            Activity activity = getActivity();
            if ( context == null || activity == null ){
                return;
            }
            try{
                if ( addSenderResponseModel.getStatusCode().equals("200") && !addSenderResponseModel.getOtpRefNo().equals("0") ){
                    TransactionOTPFragment.openSenderOTP( getActivity(),   addSenderResponseModel , url, false );
                    getActivity().finish();
                }else if ( addSenderResponseModel.getStatusCode().equals("200") && addSenderResponseModel.getOtpRefNo().equals("0") ){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setCustomImage(R.drawable.aappicon)
                            .setConfirmText("Ok!")
                            .showCancelButton(true)
                            .setTitleText(addSenderResponseModel.getStatus())
                            .setContentText( addSenderResponseModel.getMessage() )
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }else {
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
                        .setContentText( " Something went wrong " )
                        .show();
            }

            /*if ( addSenderResponseModel.getStatusCode().equals("200") ){
                new SweetAlertDialog( context, SweetAlertDialog.SUCCESS_TYPE )
                        .setCustomImage(R.mipmap.thumbs_up)
                        .setConfirmText("Ok")
                        .setTitleText( addSenderResponseModel.getStatus() )
                        .setContentText( addSenderResponseModel.getMessage() )
                        .setConfirmClickListener( mSweetAlertDialog -> activity.finish())
                        .showCancelButton( true )

                        .show();

            }
            else {
                new SweetAlertDialog( context, SweetAlertDialog.ERROR_TYPE )
                        .setConfirmText( "Ok" )
                        .setTitleText( addSenderResponseModel.getStatus() )
                        .setContentText( addSenderResponseModel.getMessage() )
                        .setConfirmClickListener( mSweetAlertDialog -> activity.finish())
                        .showCancelButton( true )
                        .show();
                activity.finish();
            }*/

        }

    }

   private class SenderReceiverAsyncTask extends AsyncTask<String, android.R.integer, ArrayList<SenderReceiver>> {

        private SenderReceiverAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show(getActivity(), "", "Fetching Sender and Receiver...");
        }

        @Override
        protected ArrayList<SenderReceiver> doInBackground(String... params) {

            UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

            String custId = user.customerId;

            //https://122.166.228.144/mscore/api/Mv2/GenerateSenderReceiverList?ID_Customer=101
            String url ;
            try {
                url = CommonUtilities.getUrl() + "/GenerateSenderReceiverList?ID_Customer=" +
                        IScoreApplication.encodedUrl
                                (IScoreApplication.encryptStart(custId));
            } catch (Exception e) {
//                e.printStackTrace();
                url = CommonUtilities.getUrl() + "/GenerateSenderReceiverList";
            }

            Log.d(TAG, "url : " + url);

            String strResponse = ConnectionUtil.getResponse(url);

            Log.d(TAG, "Result : " + strResponse);

            if (!TextUtils.isEmpty(strResponse)) {

                if (!IScoreApplication.containAnyKnownException(strResponse)){

                    try {
                        JSONObject jsonObject = new JSONObject(strResponse);

                        JSONArray jsonArray = jsonObject.optJSONArray("senderreciverlist");

                        if (jsonArray != null) {
                            ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject senRecObj = jsonArray.optJSONObject(i);

                                if (senRecObj == null) {
                                    continue;
                                }

                                SenderReceiver senderReceiver = new SenderReceiver();

                                senderReceiver.userId = senRecObj.optLong("UserID");
                                senderReceiver.fkSenderId = senRecObj.optLong("FK_SenderID");
                                senderReceiver.senderName = senRecObj.optString("SenderName");
                                senderReceiver.senderMobile = senRecObj.optString("SenderMobile");
                                senderReceiver.receiverAccountno = senRecObj.optString("ReceiverAccountno");

                                String modeStr = senRecObj.optString("Mode");

                                if (!TextUtils.isEmpty(modeStr)) {
                                    modeStr = modeStr.trim();

                                    if (TextUtils.isDigitsOnly(modeStr)) {
                                        senderReceiver.mode = Integer.parseInt(modeStr);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }

                                senderReceivers.add(senderReceiver);
                            }

                            return senderReceivers;
                        }
                    } catch (JSONException e) {
                     if (IScoreApplication.DEBUG)  e.printStackTrace();
                    }
            }else {
                    int flag= IScoreApplication.getFlagException(strResponse);
                    ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                    SenderReceiver senderReceiver = new SenderReceiver();
                    senderReceiver.checkError =flag;
                    return senderReceivers;
                }

            }else{

                ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                SenderReceiver senderReceiver = new SenderReceiver();
                senderReceiver.checkError = -50800;
                return senderReceivers;

            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SenderReceiver> result) {
            mProgressDialog.dismiss();

//            Log.d(TAG, "result : " +result);

            mSenderReceivers.clear();

            SenderReceiver defaultSelect = new SenderReceiver();
            defaultSelect.senderName = "Select";
            defaultSelect.fkSenderId = -100;

            mSenderReceivers.add(defaultSelect);

            //noinspection StatementWithEmptyBody
            if (result == null || result.size() <= 0) {

//                DialogUtil.showAlert(getActivity(),
//                        "Transaction Failed, Please try again later");
            } else {
                if (result.get(0).checkError != -50800) {


                    if (IScoreApplication.checkPermissionIemi(result.get(0).checkError, getActivity())){

                        for (int i = 0; i < result.size(); i++) {
                            SenderReceiver senderReceiver = result.get(i);

                            if (senderReceiver == null) {
                                continue;
                            }

                            if (senderReceiver.mode == 1) {
                                mSenderReceivers.add(senderReceiver);
                            }
                        }
                }

                }else {
                                    DialogUtil.showAlert(getActivity(),
                        IScoreApplication.MSG_EXCEPTION_NETWORK);
                }
            }
            setSenderSpinner();

        }

    }

}
