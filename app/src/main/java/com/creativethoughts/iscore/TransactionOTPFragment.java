package com.creativethoughts.iscore;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.money_transfer.AddSenderReceiverResponseModel;
import com.creativethoughts.iscore.money_transfer.MoneyTransferResponseModel;
import com.creativethoughts.iscore.money_transfer.QuickPayMoneyTransferFragment;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionOTPFragment extends Fragment implements View.OnClickListener {


    private static final String BUNDLE_DATA_SENDER_ID = "sender_id";
    private static final String BUNDLE_DATA_RECEIVER_ID = "receiver_id";
    private static final String BUNDLE_DATA_TRANSACTION_ID = "transaction_id";
    private static final String BUNDLE_DATA_IS_TRANSACTION = "is_transaction";
    private static final String BUNDLE_OTP_REFERENCE = "otpReference";
    private static final String BUNDLE_MOBILE = "mobile";
    private static final String BUNDLE_RESEND_LINK = "resend_link";
    private static final String BUNDLE_IS_SENDER = "is_sender";
    private static final String BUNDLE_SENDER_RECEIVER_OBJ = "sender_reciever_obj";
    protected Button button;
    private String mSenderId;
    private String mReceiverId;
    private String mTransactionId;
    private boolean mIsForTransaction;
    private boolean mIsSender;
    public TextView txt_amtinword;
    private AppCompatEditText mOTPEt;
    private String mOtpReferenceNo;
    private String mMobileNo;
    private ProgressDialog mProgressDialog;
    private String mResendLink;
    private AddSenderReceiverResponseModel mAddSenderReceiverResponseModel;

    public TransactionOTPFragment() {

    }

    public static void openTransactionOTP(Context context, String senderId, String receiverId, String transactionId,
                                          AddSenderReceiverResponseModel addSenderResponseModel, String otpReferenceNo, String resendLink) {
        addSenderResponseModel.setOtpRefNo( otpReferenceNo);
        openOtp(context, senderId, receiverId, transactionId, true, addSenderResponseModel, resendLink, false );
    }

    public static void openSenderOTP(Context context, /*String senderId*/ AddSenderReceiverResponseModel addSenderResponseModel,String resendLink,
                                     boolean isSender){

        openOtp(context, addSenderResponseModel.getIdSender() , "", "", false, addSenderResponseModel, resendLink, isSender);

    }

    private static void openOtp(Context context, String senderId, String receiverId, String transactionId, boolean isForTransaction,
                                AddSenderReceiverResponseModel addSenderResponseModel, String resendLink, boolean isSender) {
        Intent intent = new Intent(context, TransactionOTPActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_DATA_SENDER_ID, senderId);
        bundle.putString(BUNDLE_DATA_RECEIVER_ID, receiverId);
        bundle.putString(BUNDLE_DATA_TRANSACTION_ID, transactionId);
        bundle.putBoolean(BUNDLE_DATA_IS_TRANSACTION, isForTransaction);
        bundle.putString(BUNDLE_OTP_REFERENCE, addSenderResponseModel.getOtpRefNo() );
        bundle.putString(BUNDLE_MOBILE, addSenderResponseModel.getMobileNo() );
        bundle.putString(BUNDLE_RESEND_LINK, resendLink);
        bundle.putBoolean( BUNDLE_IS_SENDER, isSender);
        bundle.putParcelable(BUNDLE_SENDER_RECEIVER_OBJ, addSenderResponseModel);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle bundle = getArguments();

        if (bundle != null) {

            mIsForTransaction = bundle.getBoolean(BUNDLE_DATA_IS_TRANSACTION, false);
            mSenderId = bundle.getString(BUNDLE_DATA_SENDER_ID);
            mReceiverId = bundle.getString(BUNDLE_DATA_RECEIVER_ID);
            mTransactionId = bundle.getString(BUNDLE_DATA_TRANSACTION_ID);
            mOtpReferenceNo = bundle.getString(BUNDLE_OTP_REFERENCE );
            mMobileNo = bundle.getString( BUNDLE_MOBILE );
            mResendLink = bundle.getString( BUNDLE_RESEND_LINK );
            mIsSender = bundle.getBoolean( BUNDLE_IS_SENDER );
            mAddSenderReceiverResponseModel = bundle.getParcelable(BUNDLE_SENDER_RECEIVER_OBJ);
        } else {
            activity.finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_ot, container, false);

        mOTPEt =   view.findViewById(R.id.otp);

        button =   view.findViewById(R.id.btn_submit);
        //txt_amtinword =   view.findViewById(R.id.txt_amtinword);

        button.setOnClickListener( this );
        Button btnResendOtp  =   view.findViewById( R.id.btn_resend_otp );
        btnResendOtp.setOnClickListener( this );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_submit:

                if (isValid()) {
                    if (NetworkUtil.isOnline()) {
                        button.setEnabled(false);
                        String otp = mOTPEt.getText().toString();
                        new confirmOtpAsyncTask(otp).execute();
                    } else {

                        alertMessage1("",  "Network is currently unavailable. Please try again later.");

                       /* DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");*/
                    }
                }
                break;
            case R.id.btn_resend_otp:{
                new ResendAsyncTask(mResendLink).execute();
            }
        }
    }

    private boolean isValid() {
        String otp = mOTPEt.getText().toString();

        if (TextUtils.isEmpty(otp)) {
            mOTPEt.setError("Please enter the OTP");

            return false;
        }

        mOTPEt.setError(null);

        return true;
    }

    public String confirmOtp(String otp) {


        try {

            final String url;

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            if (mIsForTransaction) {
                url =
                        BASE_URL + "/MTVerifyPaymentOTP?senderid=" +
                                IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mSenderId))
                                + "&receiverid=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mReceiverId))
                                + "&transcationID=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mTransactionId))
                                + "&OTP=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(otp.trim()))+
                                "&otpRefNo="+ IScoreApplication.encodedUrl( mOtpReferenceNo );
            } else {
                if ( mIsSender ){
                    url =
                            BASE_URL + "/MTVerifySenderOTP?senderid=" +
                                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mSenderId))
                                    + "&OTP=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(otp.trim()))+"&otpRefNo="+
                                    IScoreApplication.encodedUrl( IScoreApplication.encryptStart( mOtpReferenceNo.trim() ))+
                                    "&mobile="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mMobileNo ));
                }
                else {
                    url =
                            BASE_URL + "/MTVerifyReceiverOTP?senderid=" +
                                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAddSenderReceiverResponseModel.getIdSender()))+
                                    "&receiverid="+ IScoreApplication.encodedUrl( IScoreApplication.encryptStart(mAddSenderReceiverResponseModel.getIdReceiver()))
                                    + "&OTP=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(otp.trim()))+"&otpRefNo="+
                                    IScoreApplication.encodedUrl( IScoreApplication.encryptStart( mOtpReferenceNo )) ;
                }

            }



            return   ConnectionUtil.getResponse(url);



        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf( IScoreApplication.FLAG_NETWORK_EXCEPTION );
    }

    private void moneyTransactionOtpVerify( String response ){
        try {
            Gson gson = new Gson();
            MoneyTransferResponseModel moneyTransferResponseModel = gson.fromJson( response, MoneyTransferResponseModel.class );
            if ( moneyTransferResponseModel.getStatusCode().equals( "200" ) ){
         //   if ( moneyTransferResponseModel.getStatusCode().equals( "500" ) ){


                String mAccNo =moneyTransferResponseModel.getmAccNo();
                String msenderName =moneyTransferResponseModel.getMsenderName();
                String msenderMobile =moneyTransferResponseModel.getMsenderMobile();
                String mreceiverAccountno =moneyTransferResponseModel.getMreceiverAccountno();
                String mrecievererName =moneyTransferResponseModel.getMrecievererName();
                String mrecieverMobile =moneyTransferResponseModel.getMrecieverMobile();
                String mbranch =moneyTransferResponseModel.getMbranch();
                String mAmount =moneyTransferResponseModel.getmAmount();



                QuickSuccess(mAccNo,moneyTransferResponseModel.getStatus(),moneyTransferResponseModel.getMessage(),"",
                        moneyTransferResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch,mAmount);


               /*new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText( moneyTransferResponseModel.getStatus() )
                        .setContentText( moneyTransferResponseModel.getMessage())
                        .setConfirmText("Ok!")
                        .showCancelButton(true)
                        .setCustomImage(R.mipmap.thumbs_up)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                getActivity().finish();
                            }
                        }).show();*/
            }
            else {
                alertMessage1("Oops....!", "Something went wrong");
            }
        }catch ( Exception ignored ){
            alertMessage1("Oops....!", "Something went wrong");

        }
    }



    private void addOrRecieverOtpVerificationResultProcessing(String response){
        Gson gson = new Gson();
        try {

            AddSenderReceiverResponseModel addSenderReceiverResponseModel = gson.fromJson( response, AddSenderReceiverResponseModel.class );
            if ( addSenderReceiverResponseModel.getStatusCode() != null  && addSenderReceiverResponseModel.getStatusCode().equals("200")){

                alertMessage2(addSenderReceiverResponseModel.getStatus(), addSenderReceiverResponseModel.getMessage() );

              /*  SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                sweetAlertDialog
                        .setCustomImage(R.drawable.aappicon)
                        .setConfirmText("Ok!")
                        .showCancelButton(true)
                        .setTitleText(addSenderReceiverResponseModel.getStatus())
                        .setContentText( addSenderReceiverResponseModel.getMessage() )
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                getActivity().finish();
                            }

                        });
                sweetAlertDialog.show();*/
            }
            else {
                alertMessage2(addSenderReceiverResponseModel.getStatus(), addSenderReceiverResponseModel.getMessage() );
              /*  new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setCustomImage(R.drawable.aappicon)
                        .setConfirmText("Ok!")
                        .showCancelButton(true)
                        .setTitleText(addSenderReceiverResponseModel.getStatus())
                        .setContentText( addSenderReceiverResponseModel.getMessage() )
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                getActivity().finish();
                            }
                        })
                        .show();*/
            }

        }catch ( Exception ignored ){
            alertMessage1("Oops....!" , " Something went wrong " );
            /*new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE )
                    .setTitleText( "Oops....!" )
                    .setContentText( " Something went wrong " )
                    .show();*/
        }
    }
    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void alertMessage2(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                getActivity().finish();
            }
        });
        alertDialog.show();
    }

    private String resendOtp(String url){
        try{
            return ConnectionUtil.getResponse(url);
        }catch (Exception e){
            return String.valueOf( IScoreApplication.FLAG_NETWORK_EXCEPTION );
        }
    }

    class confirmOtpAsyncTask extends AsyncTask<String, android.R.integer, String > {
        private String mOtp;
        private confirmOtpAsyncTask(String otp) {
            mOtp = otp;
        }

        @Override
        protected void onPreExecute() {
            if (mIsForTransaction) {
                mProgressDialog = ProgressDialog
                        .show(getActivity(), "", "Transferring Amount...");

            }else{
                if ( mIsSender ){
                    mProgressDialog = ProgressDialog
                            .show(getActivity(), "", "Adding Sender...");

                }
                else {
                    mProgressDialog = ProgressDialog
                            .show(getActivity(), "", "Adding Receiver...");

                }

            }

        }

        @Override
        protected String doInBackground(String... params) {

            return confirmOtp(mOtp);
        }

        @Override
        protected void onPostExecute(String result) {
            button.setEnabled( true );
            mProgressDialog.dismiss();
            if ( mIsForTransaction ){
                moneyTransactionOtpVerify(result);
            }
            else {
                addOrRecieverOtpVerificationResultProcessing( result );
            }

        }


    }

    private class ResendAsyncTask extends AsyncTask< String, android.R.integer, String>{
        private String mResendUrl;
        private ResendAsyncTask(String resendUrl){
            mResendUrl = resendUrl;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show(getActivity(), "", "Talking to server...");
        }
        @Override
        protected String doInBackground(String... params) {

            return resendOtp( mResendUrl );
        }
        @Override
        protected void onPostExecute(String serverResponse ) {

            mProgressDialog.dismiss();
            mOtpReferenceNo = serverResponse.trim();

        }
    }

    private void QuickSuccess(String mAccNo, String status, String message, String statusCode, String otpRefNo, String msenderName, String msenderMobile, String mreceiverAccountno, String mrecievererName, String mrecieverMobile, String mbranch, String mAmount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater1 = (LayoutInflater) getContext().getApplicationContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View layout = inflater1.inflate(R.layout.quick_pay_success_popup, null);
        TextView tvbranch =  layout.findViewById(R.id.tvbranch);
        RelativeLayout rltv_share = layout.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = layout.findViewById( R.id.lay_share );
        TextView tv_sender_name =  layout.findViewById(R.id.tv_sender_name);
        TextView tv_sender_acc_no =  layout.findViewById(R.id.tv_sender_acc_no);
        TextView tv_sender_mob_no =  layout.findViewById(R.id.tv_sender_mob_no);

        TextView tv_reciever_name =  layout.findViewById(R.id.tv_reciever_name);
        TextView tv_reciever_acc_no =  layout.findViewById(R.id.tv_reciever_acc_no);
        TextView tv_reciever_mob_no =  layout.findViewById(R.id.tv_reciever_mob_no);
        ImageView img_aapicon1=  layout.findViewById(R.id.img_aapicon);
        TextView tv_msg =  layout.findViewById(R.id.txtv_msg);
        // TextView tv_msg1 =  layout.findViewById(R.id.txtv_msg1);
        // TextView tv_status =  layout.findViewById(R.id.txtv_status);
        // CardView crdSuccess =  layout.findViewById(R.id.crdSuccess);

        TextView tv_amount =  layout.findViewById(R.id.tv_amount);
        TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
        //  TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
        TextView tvdate =  layout.findViewById(R.id.tvdate);
        TextView tvtime =  layout.findViewById(R.id.tvtime);
        //TextView bt_ok1 =  layout.findViewById(R.id.bt_ok1);
        TextView txtv_typeamt =  layout.findViewById(R.id.txtv_typeamt);
        TextView txtTo =  layout.findViewById(R.id.txtTo);
        TextView txtpfrom =  layout.findViewById(R.id.txtpfrom);


        //  crdSuccess.setVisibility(View.VISIBLE);

        tv_msg.setText(status);
        tv_sender_name.setText(msenderName);
        tv_sender_mob_no.setText(msenderMobile);
        tv_sender_acc_no.setText(mAccNo);
        // tvbranch.setText(mbranch);
        txtv_typeamt.setText("Paid Amount");
        txtpfrom.setText("Sender Details");
        txtTo.setText("Reciever Details");

        tv_reciever_name.setText(mrecievererName);
        tv_reciever_acc_no.setText(mreceiverAccountno);
        tv_reciever_mob_no.setText(mrecieverMobile);

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        double num =Double.parseDouble(""+mAmount);
        String stramnt = CommonUtilities.getDecimelFormate(num);
        //  text_confirmationmsg.setText(message);
        // text_confirmationmsg.setVisibility(View.VISIBLE);
        String[] netAmountArr = mAmount.split("\\.");
        String amountInWordPop = "";
        if ( netAmountArr.length > 0 ){
            int integerValue = Integer.parseInt( netAmountArr[0] );
            amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
            if ( netAmountArr.length > 1 ){
                int decimalValue = Integer.parseInt( netAmountArr[1] );
                if ( decimalValue != 0 ){
                    amountInWordPop += " And " + NumberToWord.convertNumberToWords( decimalValue ) + " Paise" ;
                }
            }
            amountInWordPop += " Only";
        }
        tv_amount_words.setText(""+amountInWordPop);
        tv_amount.setText("₹ " + stramnt );



        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();


        layout.findViewById( R.id.rltv_footer ).setOnClickListener( view1 -> {
            try{
//                getFragmentManager().beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY") )
//                        .commit();
                Intent i=new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );
        lay_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("img_share","img_share   1170   ");
                Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                        rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                rltv_share.draw(canvas);

                try {


                    Uri bmpUri = getLocalBitmapUri(bitmap);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception","Exception   117   "+e.toString());
                }

            }
        });


    /*    bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });*/
        alertDialog.show();
    }
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        Log.e("File  ","File   142   "+file);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
