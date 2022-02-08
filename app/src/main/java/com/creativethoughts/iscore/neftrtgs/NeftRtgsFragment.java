package com.creativethoughts.iscore.neftrtgs;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.OtherfundTransferHistoryIMPS;
import com.creativethoughts.iscore.OtherfundTransferHistoryNEFT;
import com.creativethoughts.iscore.OtherfundTransferHistoryRTGS;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.otp.OtpFragment;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class NeftRtgsFragment extends Fragment implements View.OnClickListener {


    private EditText mEdtTxtBeneficiaryName;
    private EditText  mEdtTxtBeneficiaryAccNo;
    private EditText  mEdtTxtBeneficiaryConfirmAccNo;
    private EditText mEdtTxtIfscNo;
    private TextView txtTrans,txt_header,txt_amtinword;
    private EditText  mEdtTxtAmount ;
    private Spinner mSpinnerAccountNo;
    private Button mBtnClear;
    private CheckBox mCheckSaveBeneficiary;
    private RelativeLayout mLinearParent;
    private ScrollView mScrollView;
    public static final String result ="";
    public static String balnce ="";
    String from="NEFT";
    String reslts;
    private int mModeNeftRtgs;
    private PaymentModel mPaymentModel;
    private static final String BENEFICIARY_DETAILS = "beneficiary details";
    private static final String MODE = "MODE";

    String BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();
    public NeftRtgsFragment( ) {
        //Do nothing
    }

    public static NeftRtgsFragment newInstance( String mode ) {
        Bundle bundle = new Bundle();
        bundle.putString( MODE, mode );
        NeftRtgsFragment neftRtgsFragment =  new NeftRtgsFragment();
        neftRtgsFragment.setArguments( bundle );
        return neftRtgsFragment;
    }
    public static NeftRtgsFragment newInstance( BeneficiaryDetailsModel beneficiaryDetailsModel, String mode ){
        Bundle bundle = new Bundle();
        bundle.putParcelable( BENEFICIARY_DETAILS, beneficiaryDetailsModel );
        bundle.putString( MODE, mode );
        NeftRtgsFragment neftRtgsFragment =  new NeftRtgsFragment();
        neftRtgsFragment.setArguments( bundle );
        return neftRtgsFragment;
    }
    private InputFilter inputFilterAccountNumber =   new InputFilter() {
        @Override
        public CharSequence filter( CharSequence source, int start, int end, Spanned dest, int dstart, int dend ) {
            String accNo = mEdtTxtBeneficiaryConfirmAccNo.getText().toString();
            /*if ( accNo.contains( "4" ) ){
                return "s";
            }*/
            return null;
        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.fragment_neft_rtgs, container, false );

        mEdtTxtBeneficiaryName              =   view.findViewById( R.id.edt_txt_neft_rtgs_benificiary_name );
        mEdtTxtBeneficiaryAccNo             =   view.findViewById( R.id.edt_txt_neft_rtgs_benificiary_acc_no );
        mEdtTxtBeneficiaryConfirmAccNo      =   view.findViewById( R.id.edt_txt_neft_rtgs_confirm_benificiary_acc_no );
        mEdtTxtAmount                       =   view.findViewById( R.id.edt_txt_neft_rtgs_amount );
        mEdtTxtIfscNo                       =   view.findViewById( R.id.edt_txt_neft_rtgs_ifsc_code );
        mSpinnerAccountNo                   =   view.findViewById( R.id.spinner_neft_rtgs_acc_no );
        mLinearParent                       =   view.findViewById( R.id.lnear_impes_rtgs_parent );
        mScrollView                         =   view.findViewById( R.id.scroll_view_rtgs_neft );
        mBtnClear                           =   view.findViewById( R.id.btn_neft_rtgs_clear );
        txtTrans                           =   view.findViewById( R.id.txtTrans );
        txt_header                           =   view.findViewById( R.id.txt_header );
        txt_amtinword                         = view.findViewById(R.id.txt_amtinword);

        Button btnSubmit                    =   view.findViewById( R.id.btn_neft_rtgs_submit );
        TextView txtViewChooseBeneficiary   =   view.findViewById( R.id.txt_view__neft_rtgs_choose_benefeciary );
        TextView mTxtHeader = view.findViewById(R.id.txt_header);
        mCheckSaveBeneficiary               =   view.findViewById( R.id.chk_save_ben );
        mLinearParent.setOnClickListener( this );
        btnSubmit.setOnClickListener(this );
        mBtnClear.setOnClickListener(this );
        txtTrans.setOnClickListener(this );

        txtViewChooseBeneficiary.setOnClickListener(this );
        mEdtTxtBeneficiaryConfirmAccNo.setFilters( new InputFilter[]{ inputFilterAccountNumber } );


        Log.e("NeftRtgsFragment  ","Start");
       try{
           Bundle bundle = getArguments();
           assert bundle != null;

           String title = bundle.getString( MODE );
           switch (title) {
               case IScoreApplication.OTHER_FUND_TRANSFER_MODE_NEFT:
                   mModeNeftRtgs = 2;
                   break;
               case IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS:
                   mModeNeftRtgs = 1;
                   break;
               case IScoreApplication.OTHER_FUND_TRANSFER_MODE_IMPS:
                   mModeNeftRtgs = 3;
                   break;
               default:
                   mModeNeftRtgs = 0;
                   break;
           }
           mTxtHeader.setText( title );

           BeneficiaryDetailsModel beneficiaryDetailsModel = bundle.getParcelable(BENEFICIARY_DETAILS);

           txtViewChooseBeneficiary.setVisibility( View.GONE );
           if ( beneficiaryDetailsModel != null ){
               mCheckSaveBeneficiary.setVisibility( View.GONE );
           }
           mBtnClear.setVisibility( View.GONE );
           mBtnClear.setOnClickListener( null );

           //assert beneficiaryDetailsModel != null;
           mEdtTxtBeneficiaryName.setText( beneficiaryDetailsModel.getBeneficiaryName( ) );
           mEdtTxtIfscNo.setText( beneficiaryDetailsModel.getBeneficiaryIfsc( )  );
           mEdtTxtBeneficiaryAccNo.setText( beneficiaryDetailsModel.getBeneficiaryAccNo( ) );
           mEdtTxtBeneficiaryConfirmAccNo.setText( beneficiaryDetailsModel.getBeneficiaryAccNo( )  );

           disableField();

        }catch ( NullPointerException e ){
            //Do nothing
       }
        setAccountNo();

        mEdtTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mEdtTxtAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();


                    Double longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Double.parseDouble(originalString);
                    String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    mEdtTxtAmount.setText(formattedString);
                    mEdtTxtAmount.setSelection(mEdtTxtAmount.getText().length());


                    String amnt = mEdtTxtAmount.getText().toString().replaceAll(",", "");
                    String[] netAmountArr = amnt.split("\\.");
                    String amountInWordPop = "";
                    if ( netAmountArr.length > 0 ){
                        try {
                            int integerValue = Integer.parseInt( netAmountArr[0] );
                            amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
                            if ( netAmountArr.length > 1 ){
                                int decimalValue = Integer.parseInt( netAmountArr[1] );
                                if ( decimalValue != 0 ){
                                    amountInWordPop += " and " + NumberToWord.convertNumberToWords( decimalValue ) + " paise" ;
                                }
                            }
                            amountInWordPop += " only";
                        }
                        catch (Exception e)
                        {

                        }

                    }
                    txt_amtinword.setText(""+amountInWordPop);


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                mEdtTxtAmount.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length()!= 0) {

                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        btnSubmit.setText( "PAY  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                        //  txt_amtinword.setText(CommonUtilities.getDecimelFormate(num));




                    }
                    else{
                        btnSubmit.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }

            }
        });
        return view;
    }

    @Override
    public void onClick(View view ){
        int id = view.getId( );

        switch (id ){
            case R.id.btn_neft_rtgs_clear:
                clearAll( );
                break;
            case R.id.txtTrans:
                String module = txt_header.getText().toString();
                if(module.equals("IMPS"))
                {
                    Intent im = new Intent(getActivity(), OtherfundTransferHistoryIMPS.class);
                    //im.putExtra("submode","1");
                    getActivity().startActivity(im);
                }
                else if(module.equals("NEFT"))
                {
                    Intent im = new Intent(getActivity(), OtherfundTransferHistoryNEFT.class);
                    //  im.putExtra("submode","2");
                    getActivity().startActivity(im);
                }
                if(module.equals("RTGS"))
                {
                    Intent im = new Intent(getActivity(), OtherfundTransferHistoryRTGS.class);
                    //   im.putExtra("submode","3");
                    getActivity().startActivity(im);
                }
                break;
            case R.id.btn_neft_rtgs_submit:
                
                if (isValid() ) {
                    confirmationPopup();
                }
//                if(mEdtTxtAmount.getText().toString().equals(""))
//                {
//                    Toast.makeText(getActivity(),"please enter amount",Toast.LENGTH_LONG).show();
//                }
//                 else {
//                confirmationPopup();
//
//                 }


                break;

//            case R.id.txtTrans:
//                String module = txt_header.getText().toString();
//                Intent im = new Intent(getActivity(), OtherfundTransferType.class);
//                if(module.equals("IMPS"))
//                {
//                    im.putExtra("submode","1");
//                }
//                else if(module.equals("NEFT"))
//                {
//                    im.putExtra("submode","2");
//                }
//                if(module.equals("RTGS"))
//                {
//                    im.putExtra("submode","3");
//                }
//                getActivity().startActivity(im);
//                break;

            case R.id.txt_view__neft_rtgs_choose_benefeciary:
                break;
            default:
                break;
        }
    }

    private void confirmationPopup() {


     try {
         AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
         LayoutInflater inflater = this.getLayoutInflater();
         View dialogView = inflater.inflate(R.layout.confirmation_msg_popup, null);
         dialogBuilder.setCancelable(false);
         dialogBuilder.setView(dialogView);


         String amnt = mEdtTxtAmount.getText().toString().replaceAll(",", "");
         TextView text_confirmationmsg = dialogView.findViewById(R.id.text_confirmationmsg);
         TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
         TextView txtvAcntno = dialogView.findViewById(R.id.txtvAcntno);
         TextView txtvbranch = dialogView.findViewById(R.id.txtvbranch);
         TextView txtvbalnce = dialogView.findViewById(R.id.txtvbalnce);

         TextView txtvAcntnoto = dialogView.findViewById(R.id.txtvAcntnoto);
         TextView txtvbranchto = dialogView.findViewById(R.id.txtvbranchto);
         TextView txtvbalnceto = dialogView.findViewById(R.id.txtvbalnceto);



         txtvAcntno.setText("A/C No : "+mSpinnerAccountNo.getSelectedItem().toString());
         reslts = CommonUtilities.result;
         balnce = CommonUtilities.bal;
         txtvbranch.setText("Branch :"+reslts);
         // txtvbranch.setVisibility(View.GONE);
         double num1 =Double.parseDouble(balnce);
         DecimalFormat fmt = new DecimalFormat("#,##,###.00");

         txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));

         TextView tv_amount_words = dialogView.findViewById(R.id.tv_amount_words);
         Button butOk = dialogView.findViewById(R.id.btnOK);
         Button butCan = dialogView.findViewById(R.id.btnCncl);

         txtvAcntnoto.setText("A/C No: "+ mEdtTxtBeneficiaryConfirmAccNo.getText().toString());
         txtvbranchto.setText("Branch :"+"");
         txtvbranchto.setVisibility(View.GONE);
         // txtvbalnceto.setText("Transfer Amount: ");



         if(amnt!=null)
         {
             double num =Double.parseDouble(""+amnt);
             String stramnt = CommonUtilities.getDecimelFormate(num);

             text_confirmationmsg.setText("Proceed Transaction with above receipt amount"+ "..?");

             // text_confirmationmsg.setText("Proceed Transaction with above receipt amount to A/C no " + accNumber + " ..?");
             String[] netAmountArr = amnt.split("\\.");
             String amountInWordPop = "";
             if ( netAmountArr.length > 0 ){
                 int integerValue = Integer.parseInt( netAmountArr[0] );
                 amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
                 if ( netAmountArr.length > 1 ){
                     int decimalValue = Integer.parseInt( netAmountArr[1] );
                     if ( decimalValue != 0 ){
                         amountInWordPop += " and " + NumberToWord.convertNumberToWords( decimalValue ) + " paise" ;
                     }
                 }
                 amountInWordPop += " only";
             }
             tv_amount_words.setText(""+amountInWordPop);


             tv_amount.setText("₹ " + stramnt );
         }



         AlertDialog alertDialog = dialogBuilder.create();
         alertDialog.show();
         butOk.setOnClickListener(v -> {

             //   btn_submit.setEnabled(false);
             alertDialog.dismiss();
             // submit(alertDialog);
             submit();
         });

         butCan.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 alertDialog.dismiss();
             }
         });
     }catch (Exception e){
         Log.e("Exception  446    ",""+e.toString());
     }



    }

    private boolean isValid(){
        changeBackground(mEdtTxtBeneficiaryName, false );
        changeBackground(mEdtTxtBeneficiaryAccNo, false );
        changeBackground(mEdtTxtBeneficiaryConfirmAccNo, false );
        changeBackground(mEdtTxtIfscNo,false );

       if (mEdtTxtBeneficiaryName.getText( ).toString( ).isEmpty( ) ){
           showSnackBar("Please enter Beneficiary name" );
           changeBackground(mEdtTxtBeneficiaryName, true );
           focusScrollView(mEdtTxtBeneficiaryName );
           return false;
       }
        String tempBeneficiaryAccNo = mEdtTxtBeneficiaryAccNo.getText( ).toString( );
        String tempBeneficiaryConfirmAccNo = mEdtTxtBeneficiaryConfirmAccNo.getText( ).toString( );

        if (tempBeneficiaryAccNo.isEmpty( )    ){
            showSnackBar("Beneficiary account number is required" );
            changeBackground(mEdtTxtBeneficiaryAccNo, true );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryAccNo );
            return false;
        }
        if ( tempBeneficiaryConfirmAccNo.isEmpty( )   ){
            showSnackBar("Confirm Beneficiary account number is required" );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryConfirmAccNo );
            return false;
        }

        if (! tempBeneficiaryAccNo.matches("^\\d+$" ) ){
            showSnackBar("Invalid beneficiary account numbers " );
            changeBackground(mEdtTxtBeneficiaryAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryAccNo );
            return false;
        }
        if ( ( !tempBeneficiaryAccNo.equals(tempBeneficiaryConfirmAccNo )  )  ){
            showSnackBar("Beneficiary account numbers don't match" );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryConfirmAccNo );
            return false;
        }
        try {
            String ifscNumber = mEdtTxtIfscNo.getText( ).toString( );
            if (ifscNumber.isEmpty( ) || ifscNumber.length() != 11){
                showSnackBar("Please enter valid IFSC" );
                changeBackground(mEdtTxtIfscNo, true );
                focusScrollView(mEdtTxtIfscNo );
                return false;
            }


        }catch (Exception e ){

            if (IScoreApplication.DEBUG ) Log.d("ifscParseExc", e.toString( ) );
            focusScrollView(mEdtTxtIfscNo );
            return false;

        }
        try {
            String amount = mEdtTxtAmount.getText( ).toString( );
            amount = amount.replace(",","");
            int tempAmount = Integer.parseInt(amount );
            if (tempAmount < 0  ){
                showSnackBar("please enter amount" );
//                showSnackBar("Invalid amount " );
                changeBackground(mEdtTxtAmount, true );
                focusScrollView(mEdtTxtAmount );
                return false;
            }
        }catch (Exception e ){
            showSnackBar("Invalid amount " );
            changeBackground(mEdtTxtAmount, true );
            focusScrollView(mEdtTxtAmount );
            if (IScoreApplication.DEBUG ) Log.d("AmountExc", e.toString( ) );
            return false;

        }

        return true;
    }
    private void changeBackground(EditText editText, boolean errorStatus ){
        if (errorStatus )
            editText.setBackgroundResource( R.drawable.custom_edt_txt_error_back_ground );
        else
            editText.setBackgroundResource( R.drawable.custom_edt_txt_account_border );
    }
    private void focusScrollView(final View view ){
        mScrollView.post(( ) -> mScrollView.scrollTo(0, view.getBottom( ) ) );
    }

    private void  submit( ){
        if (!NetworkUtil.isOnline( ) ){
            alertMessage1("", " Network is currently unavailable. Please try again later.");

           /* DialogUtil.showAlert(getActivity( ),
                    "Network is currently unavailable. Please try again later." );*/
            return;
        }
        SharedPreferences pinIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF36, 0);
        String pin = pinIdSP.getString("pin","");
        if (isValid( ) ){
            String tempAccNo = mSpinnerAccountNo.getSelectedItem( ).toString( );
            tempAccNo = tempAccNo.replace(tempAccNo.substring(tempAccNo.indexOf(" (" )+1, tempAccNo.indexOf( ')' )+1 ), "" );
            tempAccNo = tempAccNo.replace(" ","" );

            AccountInfo accountInfo = PBAccountInfoDAO.getInstance( ).getAccountInfo(tempAccNo );
            final String module = accountInfo.accountTypeShort;

            final String accNo = tempAccNo;

            final String beneficiaryName = mEdtTxtBeneficiaryName.getText( ).toString( );
            final String ifscNumber = mEdtTxtIfscNo.getText( ).toString( );
            final String amount = mEdtTxtAmount.getText( ).toString( );
            final String beneficiaryAccNo = mEdtTxtBeneficiaryAccNo.getText( ).toString( );

            reslts = CommonUtilities.result;

            PaymentModel paymentModel = new PaymentModel( );
            paymentModel.setAccNo( accNo  );
            paymentModel.setBeneficiaryAccNo( beneficiaryAccNo  );
            paymentModel.setBeneficiaryName( beneficiaryName  );
            paymentModel.setModule( module  );
            paymentModel.setIfsc(ifscNumber );
            paymentModel.setAmount( amount  );
            paymentModel.setBranch( reslts  );
            paymentModel.setBalance( balnce  );
            paymentModel.setMode(Integer.toString(mModeNeftRtgs ) );
            paymentModel.setPin( pin  );
            if ( mCheckSaveBeneficiary.isChecked() ){
                paymentModel.setBeneficiaryAdd("1" );
            }else
                paymentModel.setBeneficiaryAdd("0" );

            startPayment( paymentModel );
        }
    }

    private void startPayment( PaymentModel paymentModel ){
        NetworkManager.getInstance().connector(prepareUrlForPayment(paymentModel), new ResponseManager() {
            @Override
            public void onSuccess(String result) {
                processResult(result);
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Oops. Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }, getActivity(), "Talking to server");
    }

    private String prepareUrlForPayment( PaymentModel paymentModel  ){
//        mPaymentModel = paymentModel;
//        String url = CommonUtilities.getUrl( );

        mPaymentModel = paymentModel;
//        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//        String BASE_URL=pref.getString("oldbaseurl", null);

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);


        String url = BASE_URL;
        try{

            url += "/NEFTRTGSPayment?AccountNo="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getAccNo( )  ) )+
                    "&Module="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getModule( )  ) )+
                    "&BeneName="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getBeneficiaryName( )  ) )+
                    "&BeneIFSC="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getIfsc( )  ) )+
                    "&BeneAccountNumber="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getBeneficiaryAccNo( )  ) )+
                    "&amount="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getAmount( )  ) )+
                    "&EftType="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( Integer.toString(  mModeNeftRtgs )  ) )+
                    "&BeneAdd="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getBeneficiaryAdd( ) ) )+
                    "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart( paymentModel.getPin( )  ) ) +
                    "&OTPRef=&OTPCode=";


            Log.e("encryption error", url  );

        }catch ( Exception e  ){
            if ( IScoreApplication.DEBUG  )
                Log.e("encryption error", e.toString( )  );
        }
        return url;
    }

    private void showSnackBar(String message ){
        Snackbar snackbar = Snackbar.make(mLinearParent, message, Snackbar.LENGTH_SHORT );
        View snackBarView = snackbar.getView( );
        snackBarView.setBackgroundResource( R.color.red_error_snack_bar );
        snackbar.show( );
    }
    private void clearAll( ){
        mEdtTxtBeneficiaryName.setText(null );
        mEdtTxtBeneficiaryAccNo.setText(null );
        mEdtTxtBeneficiaryConfirmAccNo.setText(null );
        mEdtTxtAmount.setText(null );
        mEdtTxtIfscNo.setText(null );
        mBtnClear.setVisibility( View.GONE );
        if ( mCheckSaveBeneficiary.isChecked() )
            mCheckSaveBeneficiary.toggle();

    }
    private void setAccountNo(  ){
        SettingsModel settingsModel = SettingsDAO.getInstance( ).getDetails( );
        if (settingsModel.customerId.isEmpty( ) )
            return;
        CommonUtilities.transactionActivitySetAccountNumber( settingsModel.customerId, mSpinnerAccountNo, getActivity() );
    }



    private void processResult(String result ){
        clearAll( );
        if ( getContext( ) == null  )
            return;

        Gson gson = new Gson( );

        try{
          //  Log.e("TAG","result  654     "+result);
            NeftRtgsOtpResponseModel neftRtgsOtpResponseModel = gson.fromJson( result, NeftRtgsOtpResponseModel.class  );

            if ( getFragmentManager( ) == null || mPaymentModel == null  )
                return;
            if ( neftRtgsOtpResponseModel.getStatusCode( ) == 1 && !neftRtgsOtpResponseModel.getOtpRefNo( ).isEmpty( )  ){

                OtpFragment otpFragment = OtpFragment.newInstance( mPaymentModel, neftRtgsOtpResponseModel  );
                FragmentTransaction fragmentTransaction = getFragmentManager( ).beginTransaction( );
                fragmentTransaction.replace( R.id.container, otpFragment  );
                fragmentTransaction.addToBackStack("aha");
                fragmentTransaction.commit( );

            }
            else if ( neftRtgsOtpResponseModel.getStatusCode( ) < 0  ){
                Log.e("TAG","667   "+neftRtgsOtpResponseModel.getMessage());
                alertMessage1( "Oops...!",  neftRtgsOtpResponseModel.getMessage());

//                alertMessage( "Oops...!", new ArrayList<>(), neftRtgsOtpResponseModel.getMessage(), false, false);
            }else if ( neftRtgsOtpResponseModel.getStatusCode( ) == 3  ){
                Log.e("TAG","6671   "+neftRtgsOtpResponseModel.getMessage());
                FragmentTransaction fragmentTransaction = getFragmentManager( ).beginTransaction( );
                fragmentTransaction.remove( this  ).commit( );
            }else{
                Log.e("TAG","6672   "+neftRtgsOtpResponseModel.getMessage());
//                alertMessage( "Oops...!", new ArrayList<>(), neftRtgsOtpResponseModel.getMessage(), false, false);
                alertMessage1( "Oops...!",  neftRtgsOtpResponseModel.getMessage());
            }

        }catch ( Exception ignored  ){
            if ( IScoreApplication.DEBUG  )
                Log.e("erro", ignored.toString( )  );
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

    private void alertMessage( String title, ArrayList<KeyValuePair> keyValueList, String message , boolean isHappy, boolean isBackButtonEnabled ){
        getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
                mPaymentModel, isHappy, isBackButtonEnabled, from) ).commit();


       /* getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
                 isHappy,isBackButtonEnabled ) ).commit();*/
    }
    private void disableField(){
        mEdtTxtBeneficiaryName.setEnabled( false );
        mEdtTxtIfscNo.setEnabled( false );
        mEdtTxtBeneficiaryAccNo.setEnabled( false );
        mEdtTxtBeneficiaryConfirmAccNo.setEnabled( false );
    }

}
//