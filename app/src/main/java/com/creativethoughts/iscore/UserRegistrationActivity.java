package com.creativethoughts.iscore;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.adapters.LoginBannerAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static com.creativethoughts.iscore.IScoreApplication.FLAG_NETWORK_EXCEPTION;

public class UserRegistrationActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    private static final int PHONE_FETCHING = 200;
    private EditText mMobileNumberET;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.banner1,R.drawable.banner2,R.drawable.banner3};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    static String bank_Key, bank_Header;
    static String host_nameCommon, asset_namecommon;


    private static final String HOSTNAME_SUBJECT="TEST16";
    private static final String CERTIFICATE_ASSET_NAME="testmscore.pem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(UserRegistrationActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.phone_permission_layout);
            Button mGoToSettingsBtn = findViewById(R.id.buttonOkayPhonePermission);
            assert mGoToSettingsBtn != null;
            mGoToSettingsBtn.setOnClickListener( v -> {
                Log.e("clicked","Yes");
                goToSettings();
                finish();
            });
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserRegistrationActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                AlertDialog.Builder alertDialogBuilder  =   new AlertDialog.Builder(UserRegistrationActivity.this);
                alertDialogBuilder.setMessage("We are asking phone permission only for security purpose.Please allow this permission");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                    ActivityCompat.requestPermissions(UserRegistrationActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    dialog.dismiss();
                });
                alertDialogBuilder.show();
            } else {
                ActivityCompat.requestPermissions(UserRegistrationActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
        else {
            if (IScoreApplication.DEBUG)Log.e("Iemi", IScoreApplication.getIEMI()+"");
            if (UserCredentialDAO.getInstance().isUserAlreadyLogin()) {
                Intent pinLoginActivity =
                        new Intent(UserRegistrationActivity.this, PinLoginActivity.class);
                startActivity(pinLoginActivity);
                finish();
                return;
            }
            setContentView(R.layout.activity_register_user);
            mMobileNumberET = findViewById(R.id.phoneno);
            init();
            queryPhoneNumber();
            addListenerOnButton();
        }
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
        String strloginmobile=pref.getString("LoginMobileNo", null);

        SharedPreferences TestMobileNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF15, 0);
        String strTestmobile=TestMobileNoSP.getString("TestingMobileNo", null);

        if(strloginmobile == null || strloginmobile.isEmpty()) {

            SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
            bank_Key=bankkeypref.getString("bankkey", null);
            SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
            bank_Header=bankheaderpref.getString("bankheader", null);

            SharedPreferences hostnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
            host_nameCommon=hostnamepref.getString("hostname", null);
            SharedPreferences assetnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
            asset_namecommon=assetnamepref.getString("certificateassetname", null);
        }
        else {
            if (strTestmobile.equals(strloginmobile)) {

                SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF19, 0);
                bank_Key=bankkeypref.getString("testbankkey", null);
                SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF20, 0);
                bank_Header=bankheaderpref.getString("testbankheader", null);

                SharedPreferences hostnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF21, 0);
                host_nameCommon=hostnamepref.getString("testhostname", null);
                SharedPreferences assetnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF22, 0);
                asset_namecommon=assetnamepref.getString("testcertificateassetname", null);
            }
            else {

                SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                bank_Key=bankkeypref.getString("bankkey", null);
                SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                bank_Header=bankheaderpref.getString("bankheader", null);

                SharedPreferences hostnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                host_nameCommon=hostnamepref.getString("hostname", null);
                SharedPreferences assetnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                asset_namecommon=assetnamepref.getString("certificateassetname", null);

            }
        }
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(UserRegistrationActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(UserRegistrationActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(UserRegistrationActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new LoginBannerAdapter(UserRegistrationActivity.this,XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 4000);
    }
    private void goToSettings(){

        Log.e("reached","goToSettings");
        try {
            Intent intent = new Intent();
            intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);

        }catch (Exception e){
            if (IScoreApplication.DEBUG)Log.e("exception",e.toString()+"");
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(UserRegistrationActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserRegistrationActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if ( requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE ){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                finish();
                startActivity(getIntent());
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UserRegistrationActivity.this,
                        Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(UserRegistrationActivity.this);
                    alertDialogBuilder.setMessage("We are asking phone permission only for security purpose.Please allow this permission");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        ActivityCompat.requestPermissions(UserRegistrationActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        dialog.dismiss();

                    });
                    alertDialogBuilder.show();

                }
            }
        }

    }

    private void addListenerOnButton() {
        SharedPreferences TestMobileNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF15, 0);
        String strTestmobile=TestMobileNoSP.getString("TestingMobileNo", null);

        Button mRegisterBtn = findViewById(R.id.btnRegister);
        assert mRegisterBtn != null;
        mRegisterBtn.setOnClickListener(arg0 -> {

            String countryCode = "91"; // Need to change this hard coded value.
            String mobileNumber = mMobileNumberET.getText().toString();
            String BASE_URL;


            if(strTestmobile.equals(mobileNumber)){

                SharedPreferences pref11 =getApplicationContext().getSharedPreferences(Config.SHARED_PREF16, 0);
                String base_url11=pref11.getString("testbaseurl", null);
                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF17, 0);
                String base_url=pref.getString("testoldbaseurl", null);
                SharedPreferences imgpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF18, 0);
                String image_url=imgpref.getString("testimageurl", null);
                SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF19, 0);
                String bank_Key=bankkeypref.getString("testbankkey", null);
                SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF20, 0);
                String bank_Header=bankheaderpref.getString("testbankheader", null);
                SharedPreferences hostnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF21, 0);
                String Hostname=hostnamepref.getString("testhostname", null);
                SharedPreferences assetnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF22, 0);
                String SSLAssetname=assetnamepref.getString("testcertificateassetname", null);


                SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                baseurlEditer.putString("baseurl", base_url11);
                baseurlEditer.commit();
                SharedPreferences oldbaseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
                SharedPreferences.Editor oldbaseurlEditer = oldbaseurlSP.edit();
                oldbaseurlEditer.putString("oldbaseurl", base_url);
                oldbaseurlEditer.commit();
                SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                SharedPreferences.Editor imageurlEditer = imageurlSP.edit();
                imageurlEditer.putString("imageurl", image_url);
                imageurlEditer.commit();
                SharedPreferences bankkeySP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                SharedPreferences.Editor bankkeyEditer = bankkeySP.edit();
                bankkeyEditer.putString("bankkey", bank_Key);
                bankkeyEditer.commit();
                SharedPreferences bankheaderSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                SharedPreferences.Editor bankheaderEditer = bankheaderSP.edit();
                bankheaderEditer.putString("bankheader", bank_Header);
                bankheaderEditer.commit();
                SharedPreferences host_nameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                SharedPreferences.Editor host_nameEditer = host_nameSP.edit();
                host_nameEditer.putString("hostname", Hostname);
                host_nameEditer.commit();
                SharedPreferences asset_nameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                SharedPreferences.Editor asset_nameEditer = asset_nameSP.edit();
                asset_nameEditer.putString("certificateassetname", SSLAssetname);
                asset_nameEditer.commit();

                SharedPreferences pref1 =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
                BASE_URL=pref1.getString("oldbaseurl", null);

            }
            else{

                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
                BASE_URL=pref.getString("oldbaseurl", null);
            }

            if(mobileNumber.equalsIgnoreCase("124567")) {

                Toast.makeText(UserRegistrationActivity.this, "This functionality is not availbale",
                        Toast.LENGTH_SHORT).show();
            }

            if (mobileNumber.length() > 10 || mobileNumber.length() < 10) {
                mMobileNumberET.setError("Please enter valid 10 digit mobile number");

            }

            else {
                if (NetworkUtil.isOnline()) {

                    bank_Key = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0).getString("bankkey", null);
                    bank_Header =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0).getString("bankheader", null);
                    host_nameCommon = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0).getString("hostname", null);
                    asset_namecommon =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0).getString("certificateassetname", null);


                    try{
                        String url =
                                BASE_URL + "/PassBookAuthenticate?Mobno="+
                                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(countryCode+mobileNumber)) + "&Pin=" +
                                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart("0000"))+"&IMEI=";
//                                        +"&IMEI="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart("123456789"));
                        Log.e("urll",""+url);
                        NetworkManager.getInstance().connector(url, new ResponseManager() {
                            @Override
                            public void onSuccess(String result) {
                                Log.i("REULT","1"+result);
                                Log.e("result_true","1"+result);
                                try{
                                    analyzeResult( result, mobileNumber, countryCode );
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e("result_true","2  "+error);
                                IScoreApplication.simpleAlertDialog( UserRegistrationActivity.this, new IScoreApplication.AlertProcess() {
                                    @Override
                                    public void ok() {
                                        //Do nothing
                                    }
                                    @Override
                                    public void cancel() {
                                        //Do nothing
                                    }
                                }, error);
                            }
                        }, this, "Loading");
                    }catch ( Exception e ){
                        //Do nothing
                    }

                } else {
                    DialogUtil.showAlert(UserRegistrationActivity.this,
                            "Network is currently unavailable. Please try again later.");
                }
            }

        });

    }
    private void analyzeResult( String result,String mobileNumber, String countryCode ){
        try {
            int response;
            Log.i("Response",result);
            result = result.trim();
            if ( result.equals("true")){
                response = 1;
            }else if ( result.equals("false" ) ){
                response = 6;
            }else if ( result.equalsIgnoreCase(IScoreApplication.SERVICE_NOT_AVAILABLE ) ){
                response = 7;
            }else if(IScoreApplication.containAnyKnownException(result)){
                response = IScoreApplication.getFlagException(result);
            }else {
                response = 0;
            }
            processNetworkResponse( response, mobileNumber, countryCode );
        }catch ( Exception e ){
            Toast.makeText(getApplicationContext(),"Something went wrong 1",Toast.LENGTH_LONG).show();
        }
    }
    private void processNetworkResponse( int result, String mobileNumber, String countryCode ){
        try {
            UserCredentialDAO.getInstance().deleteAllUserData();
            if (IScoreApplication.checkPermissionIemi(result,UserRegistrationActivity.this)) {

                if (result == 1) {
                    UserCredentialDAO.getInstance()
                            .insertUserDetails(mobileNumber, countryCode);

                    Intent passBookAccount =
                            new Intent(UserRegistrationActivity.this, RecieveAndValidateOTP.class);
                    passBookAccount.putExtra("changePin", "false");
                    startActivity(passBookAccount);

                    finish();

                } else if (result == 0) {
                    UserCredentialDAO.getInstance().deleteAllUserData();
                    DialogUtil.showAlert(UserRegistrationActivity.this,
                            "We are experiencing some technical issues, Please try again after some time.");

                } else if (result == 6) {

                    IScoreApplication.simpleAlertDialog(this, new IScoreApplication.AlertProcess() {
                        @Override
                        public void ok() {
                            //Do nothing
                        }
                        @Override
                        public void cancel() {
                            //Do nothing
                        }
                    }, "Please contact bank to activate mobile banking service");
                }else if ( result == 7 ){
                    showAlert( IScoreApplication.SERVICE_NOT_AVAILABLE )   ;
                }else if (result == FLAG_NETWORK_EXCEPTION) {
                    try {
                        UserCredentialDAO.getInstance().deleteAllUserData();
                        showToastMessage();
                    }catch ( IllegalStateException e ){
                        Toast.makeText(getApplicationContext(),"Something went wrong 2",Toast.LENGTH_LONG).show();
                    }
                }else {
                    IScoreApplication.simpleAlertDialog(this, new IScoreApplication.AlertProcess() {
                        @Override
                        public void ok() {
                            //Do nothing
                        }
                        @Override
                        public void cancel() {
                            //Do nothing
                        }
                    }, "Un expected error");
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Something went wrong 1",Toast.LENGTH_LONG).show();
        }

    }
    private void showAlert( String message ){
        UserCredentialDAO.getInstance().deleteAllUserData();

        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(UserRegistrationActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) ->  dialog.dismiss() );
        alertDialogBuilder.show();
    }
    private void showToastMessage() {
        Toast toast = Toast.makeText(UserRegistrationActivity.this, IScoreApplication.MSG_EXCEPTION_NETWORK, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }
    private boolean ifGooglePlayAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable( this );
        return status == ConnectionResult.SUCCESS;
    }
    private void queryPhoneNumber(){
        if ( ifGooglePlayAvailable() ){
            try{
                double v = getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0 ).versionCode;
                if ( v >= 10.2 ){
                    requestPhoneNumber();
                }
            }catch ( PackageManager.NameNotFoundException e ){
                if ( IScoreApplication.DEBUG )
                    Log.e("version_error", e.toString() );
            }

        }
    }
    private void requestPhoneNumber() {
        if ( !ifGooglePlayAvailable() )
            return;
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported( true )
                .build();

        GoogleApiClient apiClient = new GoogleApiClient.Builder( this )
                .addApi(Auth.CREDENTIALS_API).enableAutoManage( this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).build();
        PendingIntent pendingIntent = Auth.CredentialsApi.getHintPickerIntent( apiClient, hintRequest );
        try{
            startIntentSenderForResult( pendingIntent.getIntentSender(), PHONE_FETCHING, null,0,0,0);
        }catch ( IntentSender.SendIntentException e ){

        }
    }
    @Override
    public void onActivityResult( int requestCode, int responseCode, Intent data ){
        super.onActivityResult( requestCode, responseCode, data );
        if ( requestCode == PHONE_FETCHING ){
            if ( responseCode == RESULT_OK ){
                Credential credential = data.getParcelableExtra( Credential.EXTRA_KEY );
                String phoneNum = credential.getId();
                if ( phoneNum!= null && phoneNum.length() >= 10 ){
                    phoneNum = phoneNum.substring( phoneNum.length() - 10 );
                    mMobileNumberET.setText(  phoneNum );
                }
            }
        }
    }


    public static String getBankkey() {
        try {
            return bank_Key;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getBankheader() {
        try {
            return bank_Header;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getHostnameSubject() {
        try {
            return host_nameCommon;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getCertificateAssetName() {
        try {
            return asset_namecommon;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }



}

