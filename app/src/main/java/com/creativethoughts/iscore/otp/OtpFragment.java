package com.creativethoughts.iscore.otp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.creativethoughts.iscore.FragmentMenuCard;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.neftrtgs.NeftRtgsOtpResponseModel;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */

public class OtpFragment extends Fragment implements  View.OnClickListener {

    private static final String BUNDLE_PAYMENT_MODEL = "payment_model";
    private static final String BUNDLE_REQUEST_NEFT_RTGS_OTP_RESPONSE_MODEL = "neft_rtgs_request_otp_request_response_model";
    String from="OTP";
    private static final int STATUS_RESEND_OTP = 100;
    private static final int STATUS_VERIFY_OTP = 200;

    private NeftRtgsOtpResponseModel mNeftRtgsOtpResponseModel;
    private EditText mEdtotp;
    private PaymentModel mPaymentModel;
    private SweetAlertDialog mSweetAlertDialog;
    private int maxFailedAttempt = 3;
    private TextView txtFailedAttempt;

    public OtpFragment() {
        // Required empty public constructor
    }

    public static OtpFragment newInstance(PaymentModel paymentModel, NeftRtgsOtpResponseModel neftRtgsOtpResponseModel ){
        OtpFragment otpFragment = new OtpFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable( BUNDLE_PAYMENT_MODEL, paymentModel );
        bundle.putParcelable( BUNDLE_REQUEST_NEFT_RTGS_OTP_RESPONSE_MODEL, neftRtgsOtpResponseModel );
        otpFragment.setArguments( bundle );

        return otpFragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach( context );

        Bundle bundle = getArguments();
        if ( bundle != null ){
            mPaymentModel = bundle.getParcelable(BUNDLE_PAYMENT_MODEL);
            mNeftRtgsOtpResponseModel = bundle.getParcelable( BUNDLE_REQUEST_NEFT_RTGS_OTP_RESPONSE_MODEL );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_otp, container, false );

        mEdtotp = view.findViewById( R.id.edt_txt_otp );

        view.findViewById( R.id.btn_resend ).setOnClickListener( this );
        view.findViewById( R.id.btn_submit ).setOnClickListener( this );
        txtFailedAttempt = view.findViewById( R.id.txt_failed_attempt );

        return view;
    }

    @Override
    public void onClick( View view ){
        String url ;

        int count = getFragmentManager().getBackStackEntryCount();

        int id = view.getId();
        if ( id == R.id.btn_resend ){
            url = prepareUrlForOtpResend();
            if ( !url.isEmpty() )
                getObservable( url ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( getObserver( STATUS_RESEND_OTP ));
        }
        else if ( id == R.id.btn_submit ){
            url = prepareUrlForVerifyOtp();
            if ( !url.isEmpty() )
                getObservable( url ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( getObserver( STATUS_VERIFY_OTP ));
        }

    }

    private String prepareUrlForOtpResend(){

        String url = "";
        try{
            url = CommonUtilities.getUrl();
            url += "/NEFTRTGSPayment?AccountNo="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getAccNo() ))+
                    "&Module="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getModule() ))+
                    "&BeneName="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryName() ))+
                    "&BeneIFSC="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getIfsc() ))+
                    "&BeneAccountNumber="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryAccNo() ))+
                    "&amount="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getAmount() ))+
                    "&EftType="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getMode() ))+
                    "&BeneAdd="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryAdd()))+
                    "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getPin() )) +
                    "&OTPRef=&OTPCode=";

        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG )
                Log.e("encryption error", e.toString() );
        }
        return url;
    }

    private String prepareUrlForVerifyOtp(){
        String url = "";
        String otp = mEdtotp.getText().toString();
        if ( otp.length() < 6 ){
            Toast.makeText( getContext(), "Please enter atleast 6 digits", Toast.LENGTH_LONG ).show();
            return url;
        }
        if ( otp.isEmpty() ){
            Toast.makeText( getContext(), "Please enter OTP that you have received", Toast.LENGTH_LONG ).show();
            return url;
        }
        try{
            url = CommonUtilities.getUrl();
            url += "/NEFTRTGSPayment?AccountNo="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getAccNo() ))+
                    "&Module="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getModule() ))+
                    "&BeneName="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryName() ))+
                    "&BeneIFSC="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getIfsc() ))+
                    "&BeneAccountNumber="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryAccNo() ))+
                    "&amount="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getAmount() ))+
                    "&EftType="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getMode() ))+
                    "&BeneAdd="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getBeneficiaryAdd()))+
                    "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart( mPaymentModel.getPin() )) +
                    "&OTPRef="+IScoreApplication.encodedUrl( IScoreApplication.encryptStart( mNeftRtgsOtpResponseModel.getOtpRefNo() ) )+
                    "&OTPCode="+IScoreApplication.encodedUrl( IScoreApplication.encryptStart( otp ));

        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG )
                Log.e("encryption error", e.toString() );
        }
        return url;
    }

    private Observable<String> getObservable(String url){

        if ( getContext() != null ){
            mSweetAlertDialog = new SweetAlertDialog( getContext(), SweetAlertDialog.PROGRESS_TYPE );
            mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mSweetAlertDialog.setTitleText( "Loading" );
            mSweetAlertDialog.setCancelable( false );
            mSweetAlertDialog.show();
        }
        return Observable.fromCallable( ()->talkingToServer( url ) );
    }

    private String talkingToServer( String url ){

        return ConnectionUtil.getResponse( url );
    }

    private Observer< String > getObserver(int status ){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Do nothing
            }

            @Override
            public void onNext(String response) {
                if ( mSweetAlertDialog != null )
                    mSweetAlertDialog.dismiss();
                if ( status == STATUS_RESEND_OTP ){
                    processResendResponse( response );
                }else if ( status == STATUS_VERIFY_OTP ){
                    processOtvVerifyRespnse( response);
                }
            }

            @Override
            public void onError(Throwable e) {
                if ( mSweetAlertDialog != null )
                    mSweetAlertDialog.dismiss();
            }

            @Override
            public void onComplete() {
                if ( mSweetAlertDialog != null )
                    mSweetAlertDialog.dismiss();
            }
        };
    }

    private void processResendResponse( String response ){
        //Do nothing
    }
    private void processOtvVerifyRespnse( String response ){
       try {
           NeftRtgsOtpResponseModel neftRtgsOtpResponseModel = new Gson().fromJson( response, NeftRtgsOtpResponseModel.class );
           if ( neftRtgsOtpResponseModel.getStatusCode() == 200 && neftRtgsOtpResponseModel.getOtpRefNo().equals( "200" ) ){
               /*new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                       .setTitleText("Success")
                       .setContentText(neftRtgsOtpResponseModel.getMessage())
                       .setCustomImage(R.mipmap.thumbs_up)
                       .setConfirmClickListener(sweetAlertDialog -> {
                           sweetAlertDialog.dismiss();
                           FragmentManager manager = getActivity().getSupportFragmentManager();
                           manager.beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY", "EMPTY") ).commit();

                       })

                       .show();*/
               ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
               KeyValuePair keyValuePair = new KeyValuePair();
               keyValuePair.setKey("Amount");
               String rupee = getString( R.string.rupee );
               keyValuePair.setValue( rupee + "."+neftRtgsOtpResponseModel.getAmount() );
               keyValuePairs.add( keyValuePair );
               keyValuePair = new KeyValuePair();
               keyValuePair.setKey("Ref.Id");
               keyValuePair.setValue( neftRtgsOtpResponseModel.getRefId()  );
               keyValuePairs.add( keyValuePair );
               getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance(  keyValuePairs  ,"Success",
                       neftRtgsOtpResponseModel.getMessage() , mPaymentModel, true, false, from) ).commit();
             /*  getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance(  keyValuePairs  ,"Success",
                       neftRtgsOtpResponseModel.getMessage() ,mPaymentModel,true, false) ).commit();*/
           }else {
               new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                       .setTitleText("Oops...")
                       .setContentText(neftRtgsOtpResponseModel.getExMessage())
                       .setConfirmClickListener(sweetAlertDialog -> {
                           sweetAlertDialog.dismiss();
                           maxFailedAttempt--;
                           sweetAlertDialog.dismiss();
                           if ( maxFailedAttempt == 0 ){
                               /*FragmentManager manager = getActivity().getSupportFragmentManager();
                               FragmentTransaction trans = manager.beginTransaction();
                               trans.remove(new OtpFragment() OtpFragment.newInstance( new PaymentModel(), new NeftRtgsOtpResponseModel()));
                               manager.popBackStack();
                               trans.commit();*/
                               FragmentManager manager = getActivity().getSupportFragmentManager();
                               FragmentTransaction trans = manager.beginTransaction();
                               trans.replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY"));
                               trans.commit();
                           }
                           String attempString = "You are left with "+maxFailedAttempt +" more attempt" ;
                           if ( maxFailedAttempt == 2 )
                               attempString += "s";
                           txtFailedAttempt.setText( attempString );

                       })
                       .show();
           }
       }catch ( Exception e ){
           if ( IScoreApplication.DEBUG )
               Log.e("error", e.toString() );
       }
    }


}
