package com.creativethoughts.iscore;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Recharge.KsebActivity;
import com.creativethoughts.iscore.Recharge.RechargeActivity;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.Account;
import com.creativethoughts.iscore.money_transfer.FundTransferActivity;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FragmentMenuCard extends Fragment implements View.OnClickListener {

    public String TAG ="FragmentMenuCard";
    LinearLayout llnotification,llrecharge,llfundtransfer;
    private static boolean isFirstLaunch = true;
    String AccountNumber, maskAccountNumber;
    Fragment fragment = null;
    private static final String argHideRecharge = "";
    private static final String argHideMoneyTransfer = "";
    private String mParamHideRecharge, mParamHideMoneyTransfer, cusid,cusno;
    private ImageView imgv_customerimg, imRecharge;
    private HorizontalScrollView mHorizontalScrollVew;
    private TextView txtNeftRtgs,tvRecharge;
    private android.app.AlertDialog.Builder builder;
    LinearLayout llaccsummry,llquickView;
   // private DynamicMenuDetails dynamicMenuDetails;
    CircleImageView profileIma, profileImag,profileImg, profileIm, profileImage, profileImga;
    RelativeLayout rltv_prepaid,rltv_dth,rltv_landline,rltv_postpaid,rltv_datacard,rltv_kseb,rltv_recharge_history;
    public FragmentMenuCard() {}
    android.app.AlertDialog alertDialog         = null;
    android.app.AlertDialog alertDialogmoney_transfer = null;
    android.app.AlertDialog alertDialog1        = null;
    android.app.AlertDialog alertDialogrecharge = null;
    LinearLayout llacc_details;
    TextView tv_vwBalance,tvAccno,tv_hdBalance,tv_acc,tv_hdAccount;
    RelativeLayout rltv_passbook,rltv_statement,rltv_notice,rltv_StandingInstruction,rltv_share,rltv_ownbank,rltv_otherbank;
    String Balance;
    /* Fragment fragment = null;*/
    boolean isFABOpen;
    FloatingActionButton fab,fab1,fab2,fab3;

    private Spinner AccSpinner;
    ArrayList<Account> AccountDetails = new ArrayList<>();
    ArrayAdapter<Account> AccountAdapter = null;
    SharedPreferences RechargeSP = null;
    SharedPreferences KsebSP = null;
//    SharedPreferences ImpsSP = null;
//    SharedPreferences RtgsSP = null;
//    SharedPreferences NeftSP = null;
//    SharedPreferences OwnImpsSP = null;

    ImageView imCompanylogo;


    // TODO: Rename and change types and number of parameters
    public  static Fragment newInstance(String hideRecharge, String hideMoneyTransfer ) {
        FragmentMenuCard fragment = new FragmentMenuCard();
        Bundle args = new Bundle();
        if (!hideRecharge.equals("EMPTY") || !hideMoneyTransfer.equals("EMPTY")){
            args.putString(argHideRecharge, hideRecharge);
            args.putString(argHideMoneyTransfer, hideMoneyTransfer);
        }else {
            args.putString(argHideRecharge,"");
            args.putString(argHideMoneyTransfer, "");
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mParamHideRecharge = getArguments().getString(argHideRecharge);
            mParamHideMoneyTransfer = getArguments().getString(argHideMoneyTransfer);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_menu_card, container, false);
        AccSpinner = view.findViewById(R.id.AccSpinner);
        txtNeftRtgs             = view.findViewById( R.id.txt_neft_rtgs );
        llquickView             = view.findViewById( R.id.llquickView );
        TextView tvName         = view.findViewById(R.id.tvName);
        TextView tvCustId       = view.findViewById(R.id.tvCustId);
        TextView tvAddress      = view.findViewById(R.id.tvAddress);
        TextView tvPhone        = view.findViewById(R.id.tvPhone);
        TextView tvLoginDate    = view.findViewById(R.id.tvLoginDate);
        TextView tvRecharge     = view.findViewById(R.id.tvRecharge);
        TextView tvwallet     = view.findViewById(R.id.tvwallet);
        ImageView imwallet     = view.findViewById(R.id.imwallet);
        tvAccno                 = view.findViewById(R.id.tvAccno);
        tv_vwBalance            = view.findViewById(R.id.tv_vwBalance);
        tv_hdBalance            = view.findViewById(R.id.tv_hdBalance);
        llacc_details            = view.findViewById(R.id.llacc_details);
        tv_acc                  = view.findViewById(R.id.tv_acc);
        tv_hdAccount                  = view.findViewById(R.id.tv_hdAccount);

        llnotification            = view.findViewById(R.id.llnotification);
        llrecharge            = view.findViewById(R.id.llrecharge);
        llfundtransfer            = view.findViewById(R.id.llfundtransfer);
        imCompanylogo            = view.findViewById(R.id.imCompanylogo);
        llrecharge.setOnClickListener(this);
        llnotification.setOnClickListener(this);
        llfundtransfer.setOnClickListener(this);
        llquickView.setOnClickListener(this);

        SharedPreferences imageurlSP = getActivity().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");

        SharedPreferences CompanyLogoImageCodeSP = getActivity().getSharedPreferences(Config.SHARED_PREF4, 0);
        String companylogoPath =IMAGEURL+CompanyLogoImageCodeSP.getString("CompanyLogoImageCode","");
        PicassoTrustAll.getInstance(getActivity()).load(companylogoPath).error(R.drawable.errorlogo).into(imCompanylogo);

         RechargeSP = getActivity().getSharedPreferences(Config.SHARED_PREF44, 0);
         //ImpsSP = getActivity().getSharedPreferences(Config.SHARED_PREF45, 0);
        // RtgsSP = getActivity().getSharedPreferences(Config.SHARED_PREF46, 0);
         KsebSP = getActivity().getSharedPreferences(Config.SHARED_PREF47, 0);
       //  NeftSP = getActivity().getSharedPreferences(Config.SHARED_PREF48, 0);
       //  OwnImpsSP = getActivity().getSharedPreferences(Config.SHARED_PREF49, 0);

        try {
           // llrecharge.setVisibility(View.GONE);
            if (RechargeSP.getString("Recharge","").equals("true") && KsebSP.getString("Kseb","").equals("true")){
                llrecharge.setVisibility(View.VISIBLE);
            }else {
                llrecharge.setVisibility(View.GONE);
            }
//
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        llfundtransfer.setOnClickListener(v -> {
//            moneyTransferPopupnew();

            Intent i_own=new Intent(getContext(), FundTransferActivity.class);
            startActivity(i_own);
        });
        llrecharge.setOnClickListener(v -> {
            rechargePopupnew();
        });
        llnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

//        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//        cusid = userDetails.customerId;
//        cusno = userDetails.userCustomerNo;
//        tvName.setText(userDetails.userCustomerName);
//        tvCustId.setText("( Customer Id : " + cusno + " )");
//        tvAddress.setText(userDetails.userCustomerAddress1+","+userDetails.userCustomerAddress2);
//        tvPhone.setText(userDetails.userMobileNo);

        SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
        SharedPreferences customerNoSP = getActivity().getSharedPreferences(Config.SHARED_PREF27, 0);
        SharedPreferences customerNameSP = getActivity().getSharedPreferences(Config.SHARED_PREF28, 0);
        SharedPreferences customerAddress1SP = getActivity().getSharedPreferences(Config.SHARED_PREF29, 0);
        SharedPreferences customerAddress2SP = getActivity().getSharedPreferences(Config.SHARED_PREF30, 0);
        SharedPreferences mobileNoSP = getActivity().getSharedPreferences(Config.SHARED_PREF31, 0);
        cusid = customerIdSP.getString("customerId","");
        cusno = customerNoSP.getString("customerNo","");
        tvName.setText(customerNameSP.getString("customerName",""));
        tvCustId.setText("( Customer Id : " + cusno + " )");
        tvAddress.setText(customerAddress1SP.getString("customerAddress1","")+","+customerAddress2SP.getString("customerAddress2",""));
        tvPhone.setText(mobileNoSP.getString("mobileNo",""));


        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        tvLoginDate.setText("Last Login : "+pref.getString("logintime", null));
        imgv_customerimg = (ImageView) view.findViewById(R.id.imgv_customerimg);
        imRecharge = (ImageView) view.findViewById(R.id.imRecharge);
        SharedPreferences sperf = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
        if(sperf.getString("custimage", null) == null||sperf.getString("custimage", null).isEmpty() ) {
            getCustomerImage();
        }
        else{
            if(!sperf.getString("custimage", null).isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(sperf.getString("custimage", null), Base64.DEFAULT);
                    ByteArrayToBitmap(decodedString);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Glide.with(getActivity())
                            .load(stream.toByteArray())
                            .placeholder(R.drawable.person)
                            .error(R.drawable.person)
                            .into(imgv_customerimg);
                }catch (Exception e){e.printStackTrace();}
            }
        }
        fab                    =  view.findViewById(R.id.fab);
        fab1                   = view.findViewById(R.id.fab1);
        fab2                   = view.findViewById(R.id.fab2);
        fab3                   = view.findViewById(R.id.fab3);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });
        LinearLayout llAcccount         = view.findViewById(R.id.llAcccount);
        LinearLayout llMore             = view.findViewById(R.id.llMore);
        LinearLayout llDueReminder             = view.findViewById(R.id.llDueReminder);
        LinearLayout llEMI             = view.findViewById(R.id.llEMI);
      /*  LinearLayout llRecharge         = view.findViewById(R.id.llRecharge);
        LinearLayout llFundtransfer     = view.findViewById(R.id.llFundtransfer);*/
        LinearLayout llDashboard        = view.findViewById(R.id.llDashboard);
        LinearLayout llVirtualcard      = view.findViewById(R.id.llVirtualcard);
        LinearLayout llOthers           = view.findViewById(R.id.llOthers);
        LinearLayout llProfile           = view.findViewById(R.id.llProfile);
        llaccsummry                     = view.findViewById(R.id.llAccSummary);
        mHorizontalScrollVew        =   view.findViewById(R.id.recharge_paybill_horizontal_scroll_view);
        ImageButton mRightButton    =   view.findViewById(R.id.right_button);
        ImageButton mLeftButton     =   view.findViewById(R.id.left_button);




        SharedPreferences ewarepref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF12, 0);
        String strEwireCardService=ewarepref.getString("EwireCardService", null);
        if(strEwireCardService.equals("1")) {

            tvwallet.setText("Wallet Services");
            imwallet.setImageDrawable(getResources().getDrawable(R.drawable.walllet));
        }else {
            tvwallet.setText("More");
            imwallet.setImageDrawable(getResources().getDrawable(R.drawable.more));
        }







        llVirtualcard.setOnClickListener(this);
        // llRecharge.setOnClickListener(this);
        llEMI.setOnClickListener(this);
        llMore.setOnClickListener(this);
        llDueReminder.setOnClickListener(this);
        llaccsummry.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        llProfile.setOnClickListener(this);
        llProfile.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),ProfileActivity.class);
            startActivity(i);
            //  fragment = ProfileFragment.newInstance();

        });
        llEMI.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),EMIActivity.class);
            startActivity(i);
        });
        llDueReminder.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),DuedateActivity.class);
            startActivity(i);

        });
        llMore.setOnClickListener(v -> {
            if(strEwireCardService.equals("1")) {
                Intent i = new Intent(getContext(), WalletServiceActivity.class);
                startActivity(i);
            }else {
                Intent i = new Intent(getContext(),MoreActivity.class);
                startActivity(i);
            }

        });
        llDashboard.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),DashboardtabActivity.class);
            startActivity(i);
        });
        llOthers.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),BankActivity.class);
            startActivity(i);
        });
        llAcccount.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),AccountsummaryActivity.class);
            startActivity(i);
        });
        llaccsummry.setOnClickListener(v -> {
            accountDetailsPopup();
        });
        tv_vwBalance.setOnClickListener(v -> {
            try{

                if (tv_vwBalance.getText().equals("View Balance")) {
                    tv_hdBalance.setVisibility(View.VISIBLE);
                    double num =Double.parseDouble(Balance);

                    tv_vwBalance.setText("\u20B9 "+CommonUtilities.getDecimelFormate(num));
                }
            }
            catch (Exception e){

            }
        });
        tv_hdBalance.setOnClickListener(v -> {
            tv_hdBalance.setVisibility(View.GONE);
            tv_vwBalance.setText("View Balance");
        });
        tv_hdAccount.setOnClickListener(v -> {
            if (tv_acc.getText() == AccountNumber){
                tv_acc.setText(maskAccountNumber);
                tv_hdAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_visibility_show));
            }
            else{
                tv_acc.setText(AccountNumber);
                tv_hdAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_vicibility_hide));

            }
        });

        activeProgressDialog();
//        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//        String acChange = settingsModel.customerId;

        SharedPreferences SelectedAccountSP = getActivity().getSharedPreferences(Config.SHARED_PREF40, 0);
        String acChange = SelectedAccountSP.getString("SelectedAccount",null);


        Log.e(TAG,"settingsModel  402   "+acChange+"   "+acChange);
//        if (settingsModel != null && acChange.length()!=12){
        if (acChange != null && acChange.length()!=12){
//            List<String> accountNos = PBAccountInfoDAO.getInstance().getAccountNos();
//            String account = accountNos.get(0);

//            acChange = acChange.replace(acChange.substring(acChange.indexOf(" (")+1, acChange.indexOf(")")+1), "");
//            acChange = acChange.replace(" ","");

//            AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(acChange);
//            Balance = accountInfo.availableBal;
//            String availableBal = accountInfo.availableBal;


          //  AccountNumber =settingsModel.customerId;
            AccountNumber =SelectedAccountSP.getString("SelectedAccount",null);
            maskAccountNumber =SelectedAccountSP.getString("SelectedAccount",null).replaceAll("\\w(?=\\w{4})", "*");

            tv_vwBalance.setText("View Balance");
            llacc_details.setVisibility(View.VISIBLE);

            tv_acc.setText(maskAccountNumber);
            showDepositList("1");

        }
        else {
            showDepositList("1");
        }

        return view;

    }



    private void showDepositList(String loantype) {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try {

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                String reqmode = IScoreApplication.encryptStart("14");
                final JSONObject requestObject1 = new JSONObject();
                try {


                    SharedPreferences tokenIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token = tokenIdSP.getString("Token","");
                    SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid = customerIdSP.getString("customerId","");
                    String types = loantype;

                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

//
//                    requestObject1.put("ReqMode",reqmode);
//                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
//                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
//                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
//                    requestObject1.put("LoanType",IScoreApplication.encryptStart(types));
//                    // requestObject1.put("IsShareAc", IScoreApplication.encryptStart("0"));
//
//                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
//                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));

                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1  474   "+requestObject1);



                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
              //  Call<String> call = apiService.getCustomerLoanandDeposit(body);
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try{
                            JSONObject jsonObj = new JSONObject(response.body());
                            Log.e(TAG,"response  4742   "+response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
//                                JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
//                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
//                                JSONArray Jarray  = object.getJSONArray("CustomerLoanAndDepositDetailsList");


                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
                                if(Jarray.length()!=0) {
                                    for (int i=0;i<Jarray.length();i++){
                                        JSONObject jsonobject= (JSONObject) Jarray.get(i);
                                        SharedPreferences SelectedAccountSP = getActivity().getSharedPreferences(Config.SHARED_PREF40, 0);
                                        String acChange = SelectedAccountSP.getString("SelectedAccount",null);
                                   //     Log.e(TAG,"acChange  47411   "+acChange+"   "+jsonobject.optString("AccountNumber"));
                                        if (acChange == null || acChange.isEmpty()){

                                        //    Log.e(TAG,"acChange  474112   "+acChange);

                                            JSONObject jsonobject1= (JSONObject) Jarray.get(0);
                                            AccountNumber =jsonobject1.optString("AccountNumber");
                                            maskAccountNumber = jsonobject1.optString("AccountNumber").replaceAll("\\w(?=\\w{4})", "*");

                                            Balance =jsonobject1.optString("Balance");
//                                    tvAccno.setText("Acc No : "+ AccountNumber);
                                            tv_vwBalance.setText("View Balance");
                                            llacc_details.setVisibility(View.VISIBLE);

                                            tv_acc.setText(maskAccountNumber);
                                            AccountDetails = new ArrayList<>();
                                            for (int k = 0; k < Jarray.length(); k++) {
                                                JSONObject kjsonObject = Jarray.getJSONObject(k);
                                                String mask = kjsonObject.getString("AccountNumber").replaceAll("\\w(?=\\w{4})", "*");

//                                        AccountDetails.add(new Account(mask, kjsonObject.getString("Balance")));
                                                AccountDetails.add(new Account(kjsonObject.getString("AccountNumber"), kjsonObject.getString("Balance")));

                                            }
                                        }
                                        else  if (acChange.equals(jsonobject.optString("AccountNumber"))){
                                          //  Log.e(TAG,"acChange  474113   "+acChange);
                                            AccountNumber =jsonobject.optString("AccountNumber");
                                            maskAccountNumber = jsonobject.optString("AccountNumber").replaceAll("\\w(?=\\w{4})", "*");

                                            Balance =jsonobject.optString("Balance");
//                                    tvAccno.setText("Acc No : "+ AccountNumber);
                                            tv_vwBalance.setText("View Balance");
                                            llacc_details.setVisibility(View.VISIBLE);

                                            tv_acc.setText(maskAccountNumber);
                                            AccountDetails = new ArrayList<>();
                                            for (int k = 0; k < Jarray.length(); k++) {
                                                JSONObject kjsonObject = Jarray.getJSONObject(k);
                                                String mask = kjsonObject.getString("AccountNumber").replaceAll("\\w(?=\\w{4})", "*");

//                                        AccountDetails.add(new Account(mask, kjsonObject.getString("Balance")));
                                                AccountDetails.add(new Account(kjsonObject.getString("AccountNumber"), kjsonObject.getString("Balance")));

                                            }
                                        }
                                        else {
                                            Log.e(TAG,"acChange  474114   "+acChange);
                                        }
                                    }



                                }
                                else {
                                    llacc_details.setVisibility(View.GONE);
                                }
                            }
                            else {
                                llacc_details.setVisibility(View.GONE);
                            }
                        }
                        catch (JSONException e) {
                            llacc_details.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
            catch (Exception e)
            {

            }
        }
        else {
            DialogUtil.showAlert(getActivity(),
                    "Network is currently unavailable. Please try again later.");
        }

    }



    public Bitmap ByteArrayToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    protected void activeProgressDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Rate Us");
        dialog.setMessage("Do you love this app? Please rate us.");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final String appPackageName = getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } });
        dialog.setCancelable(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() { dialog.show(); }
        }, /*2000*/(long) 2.592e+9);//30days delay
    }


    private void goingTo(View view) {
        int tempId = view.getId();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        ActionBar actionBar = activity.getSupportActionBar();
        Fragment fragment = null;
        switch (tempId) {
            case R.id.fab1:

//                Log.e(TAG,"MessagesFragment   630    ");
//                fragment = new MessagesFragment();
//                assert actionBar != null;
//                actionBar.setTitle("Message" );

                Intent im = new Intent(getActivity(),MessageActivity.class);
                startActivity(im);
                break;
            case R.id.fab2:
               // Log.e(TAG,"OffersFragment   630    ");
//                fragment = new OffersFragment();
//                assert actionBar != null;
//                actionBar.setTitle("Offer" );

                Intent ioa = new Intent(getActivity(),OffersActivity.class);
                startActivity(ioa);



                break;
            case R.id.fab3:
//                fragment = new DuedatesFragment();
//                assert actionBar != null;
//                actionBar.setTitle("Due Dates Calender" );

                Intent iDue=new Intent(getContext(),DuedateActivity.class);
                startActivity(iDue);
                break;
            case R.id.rltv_prepaid:
                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for Prepaid option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
//                        fragment = new RechargeFragment().newInstance(0);
//                        assert actionBar != null;
//                        actionBar.setTitle( getString(R.string.title_prepaid) );
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), RechargeActivity.class);
                        intent.putExtra("from", "prepaid");
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_dth:
                //DO something
//                fragment = RechargeFragment.newInstance(0);


                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {

                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for DTH option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {

//                        fragment = new RechargeFragment().newInstance(3);
//                        assert actionBar != null;
//                        actionBar.setTitle( getString(R.string.title_DTH) );
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), RechargeActivity.class);
                        intent.putExtra("from", "DTH");
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_landline:
                //DO something
                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for LandLine option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
//                        fragment = new RechargeFragment().newInstance(2);
//                        assert actionBar != null;
//                        actionBar.setTitle( getString(R.string.title_landline) );
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), RechargeActivity.class);
                        intent.putExtra("from", "Landline");
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_postpaid:
                //DO something

                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for Postpaid option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
//                        fragment = new RechargeFragment().newInstance(1);
//                        assert actionBar != null;
//                        actionBar.setTitle( getString(R.string.title_postpaid) );
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), RechargeActivity.class);
                        intent.putExtra("from", "postpaid");
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_datacard:
                //DO something
//                fragment = RechargeFragment.newInstance(0);

                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!RechargeSP.getString("Recharge","").equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for DataCard option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    } else {
//                        fragment = new RechargeFragment().newInstance(4);
//                        assert actionBar != null;
//                        actionBar.setTitle( getString(R.string.data_card) );
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), RechargeActivity.class);
                        intent.putExtra("from", "datacard");
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_kseb:
                //noinspection AccessStaticViaInstance

                builder = new android.app.AlertDialog.Builder(getContext());
                try {
                    if (!KsebSP.getString("Kseb","").equals("true")){
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setCancelable(true);
                        alertDialog.setTitle("No Access");
                        alertDialog.setMessage("No Access for KSEB option!");
                        alertDialog.setIcon(R.drawable.ic_warning);
                        alertDialog.show();
                    }else {
//                        fragment = new KsebBillFragment().newInstance();
//                        assert actionBar != null;
//                        actionBar.setTitle(getString(R.string.kseb_sub_name_bill_status_on_drawer));
//                        alertDialogrecharge.dismiss();

                        Intent intent = new Intent(getContext(), KsebActivity.class);
                        startActivity(intent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rltv_recharge_history:
                Intent rechargeHistory = new Intent(getContext(),RechargeHistoryActivity.class);
                startActivity(rechargeHistory);
                alertDialogrecharge.dismiss();
                break;


//            case R.id.rltv_ownbank:
//                Intent i_own=new Intent(getContext(), MoneyTransferActivity.class);
//                startActivity(i_own);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
//                alertDialogmoney_transfer.dismiss();
//                break;
//            case R.id.rltv_otherbank:
//
//                fragment =  new OtherBankFundTransferServiceChooserFragment();
//                assert actionBar != null;
//                actionBar.setTitle("IMPS/NEFT");
//                alertDialogmoney_transfer.dismiss();
//                break;

            case R.id.account_info:
                fragment = new AccountInfoFragment();
                assert actionBar != null;
                actionBar.setTitle( getString(R.string.title_section1) );
                break;
            case R.id.searc_h:
//                fragment = new SearchFragment();
//                assert actionBar != null;
//                actionBar.setTitle( getString( R.string.title_section2 ) );
                break;
            case R.id.rltv_notice:
                Intent i = new Intent(getContext(),NotificationPostingActivity.class);
                startActivity(i);
             /*   fragment = new NotificationPostingFragment().newInstance();
                assert actionBar != null;
                actionBar.setTitle("Intimation");
                alertDialog1.dismiss();*/

                break;
            case R.id.rltv_StandingInstruction:
                Intent ii = new Intent(getContext(),StandingInstructionActivity.class);
                startActivity(ii);
                break;
            case R.id.rltv_statement:
                Intent istate = new Intent(getContext(), StatementDownloadViewActivity.class);
                startActivity(istate);
                break;
            case R.id.rltv_share:
                Intent i2 = new Intent(getContext(),ShareActivity.class);
                startActivity(i2);
               /* fragment = new StandingInstructionFragment().newInstance();
                assert actionBar != null;
                actionBar.setTitle("Standing Instruction");
                alertDialog1.dismiss();*/

                break;


            case R.id.llquickView:
                Intent BalanceEnqActivityintnt = new Intent(getContext(),BalanceEnqActivity.class);
                startActivity(BalanceEnqActivityintnt);
                break;
            case R.id.rltv_passbook:
                Intent intnt = new Intent(getContext(),PassbookTranscationActivity.class);
                startActivity(intnt);
                break;
            case R.id.imps_neft:
//                fragment = new QuickPayMoneyTransferFragment();
//                assert actionBar != null;
//                actionBar.setTitle(getString(R.string.fund_transfer_title));
//                break;
//            case R.id.fund_transfer:
//                fragment = new OwnAccFundTransferFragment().newInstance();
//                assert actionBar != null;
//                actionBar.setTitle(getString(R.string.fund_transfer_title));
//                break;

//            case R.id.rtgs:
//                //noinspection AccessStaticViaInstance
//                fragment = new OtherBankFundTransferServiceChooserFragment();
//                assert actionBar != null;
//                actionBar.setTitle("IMPS/NEFT");
//                break;
            case R.id.standing_i:
                fragment = new StandingInstructionFragment().newInstance();
                assert actionBar != null;
                actionBar.setTitle("Standing Instruction");
                break;
            case R.id.llVirtualcard:

                Intent intn = new Intent(getContext(),VirtualActivity.class);
                startActivity(intn);
//                fragment=new VirtualcardFragment().newInstance();
//                assert actionBar != null;
//                actionBar.setTitle("Virtual Card");
                break;
            case R.id.intimation:
                fragment = new NotificationPostingFragment().newInstance();
                assert actionBar != null;
                actionBar.setTitle("Intimation");
                break;
            case R.id.branchdetails:
                fragment = new StandingInstructionFragment().newInstance();
                assert actionBar != null;
                actionBar.setTitle("Branch Details");
                break;

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
        else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

//    private void home(Fragment passfragment, ActionBar actionBar, android.app.AlertDialog alertDialog1){
//        passfragment = new HomeFragment();
//        assert actionBar != null;
//        actionBar.setTitle( "Passbook" );
//        alertDialog1.dismiss();
//        if (passfragment != null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, passfragment).commit();
//        } else {
//            // error in creating fragment
//            Log.e("MainActivity", "Error in creating fragment");
//        }
//    }

    private void horizontalViewFocus(View view){
        int id = view.getId();
        switch (id){
            case R.id.left_button:
                mHorizontalScrollVew.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                break;
            case R.id.right_button:
                mHorizontalScrollVew.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if ( id == R.id.left_button || id == R.id.right_button)
            horizontalViewFocus(view);
        else {
            goingTo(view);
        }
    }

    private void getCustomerImage() {

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("FK_Customer",/*cusid*/IScoreApplication.encryptStart(cusid));

                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getImage(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.i("Imagedetails",response.body());
                            // Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobjt = jObject.getJSONObject("CustomerImageDets");
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0"))
                            {
                                SharedPreferences custimageSP = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                SharedPreferences.Editor custimageSPEditer = custimageSP.edit();
                                custimageSPEditer.putString("custimage", jobjt.getString("CusImage"));
                                custimageSPEditer.commit();
                                try{
                                    byte[] decodedString = Base64.decode(jobjt.getString("CusImage"), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    decodedByte.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                    byte[] bitmapdata = stream.toByteArray();


                                    Glide.with(getActivity())
                                            .load(/*stream.toByteArray()*/bitmapdata)
                                            .placeholder(R.drawable.person)
                                            .error(R.drawable.person)
                                            .into(imgv_customerimg);
                                }catch (Exception e){e.printStackTrace();}
                                //Toast.makeText(getActivity(),"Image found",Toast.LENGTH_LONG).show();
                            }
                            else  if(statuscode.equals("-1"))
                            {
                                //  Toast.makeText(getActivity(),"Statuscode -1 so no image found",Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Imagedetails","Something went wrong");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
        }
    }




    private void accountDetailsPopup() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.account_submenu_popup, null);
            rltv_passbook = layout.findViewById(R.id.rltv_passbook);
            rltv_statement = layout.findViewById(R.id.rltv_statement);
            //txtAccountsummary =  layout.findViewById(R.id.rltv_notice);
            rltv_notice =  layout.findViewById(R.id.rltv_notice);
            rltv_StandingInstruction =  layout.findViewById(R.id.rltv_StandingInstruction);
            rltv_share = layout.findViewById(R.id.rltv_share);
            builder.setView(layout);
            alertDialog1 = builder.create();
            rltv_passbook.setOnClickListener(this);
            rltv_statement.setOnClickListener(this);
            rltv_notice.setOnClickListener(this);
            rltv_StandingInstruction.setOnClickListener(this);
            //   txtNotice.setOnClickListener(this);
            rltv_share.setOnClickListener(this);
            Window window = alertDialog1.getWindow();
            window.setGravity(Gravity.CENTER);
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moneyTransferPopupnew() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.moneytransfer_popup_new, null);
            rltv_ownbank = layout.findViewById(R.id.rltv_ownbank);
            rltv_otherbank =  layout.findViewById(R.id.rltv_otherbank);
            builder.setView(layout);
            alertDialogmoney_transfer = builder.create();
            rltv_ownbank.setOnClickListener(this);
            rltv_otherbank.setOnClickListener(this);
            Window window = alertDialogmoney_transfer.getWindow();
            window.setGravity(Gravity.CENTER);
            alertDialogmoney_transfer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialogmoney_transfer.show();



//            String tempRtgsNeft = IScoreApplication.decryptStart(dynamicMenuDetails.getRtgs() );
//            if(tempRtgsNeft.equals("000")){
//                if ( IScoreApplication.decryptStart(dynamicMenuDetails.getImps()).equals("0") ){
//                    rltv_otherbank.setVisibility(View.GONE);
//                    profileImg.setVisibility(View.GONE);
//                }else
//                    rltv_otherbank.setVisibility(View.VISIBLE);
//                profileImg.setVisibility(View.VISIBLE);
//            }else {
//                rltv_otherbank.setVisibility(View.VISIBLE);
//                profileImg.setVisibility(View.VISIBLE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rechargePopupnew() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout              = inflater1.inflate(R.layout.recharge_popup_new, null);
            rltv_prepaid             = layout.findViewById(R.id.rltv_prepaid);
            rltv_dth                 = layout.findViewById(R.id.rltv_dth);
            rltv_landline            = layout.findViewById(R.id.rltv_landline);
            rltv_postpaid            = layout.findViewById(R.id.rltv_postpaid);
            rltv_datacard            = layout.findViewById(R.id.rltv_datacard);
            rltv_kseb                = layout.findViewById(R.id.rltv_kseb);
            rltv_recharge_history    = layout.findViewById(R.id.rltv_recharge_history);
            builder.setView(layout);
            alertDialogrecharge      = builder.create();
            rltv_prepaid.setOnClickListener(this);
            rltv_dth.setOnClickListener(this);
            rltv_landline.setOnClickListener(this);
            rltv_postpaid.setOnClickListener(this);
            rltv_datacard.setOnClickListener(this);
            rltv_kseb.setOnClickListener(this);
            rltv_recharge_history.setOnClickListener(this);
            Window window = alertDialogrecharge.getWindow();
            window.setGravity(Gravity.CENTER);
            alertDialogrecharge.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialogrecharge.show();
            try {
                if (!RechargeSP.getString("Recharge","").equals("true")){
                    rltv_prepaid.setVisibility(View.GONE);
                    rltv_dth.setVisibility(View.GONE);
                    rltv_landline .setVisibility(View.GONE);
                    rltv_postpaid .setVisibility(View.GONE);
                    rltv_datacard.setVisibility(View.GONE);
                }else{
                    rltv_prepaid.setVisibility(View.VISIBLE);
                    rltv_dth.setVisibility(View.VISIBLE);
                    rltv_landline .setVisibility(View.VISIBLE);
                    rltv_postpaid .setVisibility(View.VISIBLE);
                    rltv_datacard.setVisibility(View.VISIBLE);
                }
                if (!KsebSP.getString("Kseb","").equals("true")){
                    rltv_kseb.setVisibility(View.GONE);
                }else {
                    rltv_kseb.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> true;
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
        String asset_Name=sslnamepref.getString("certificateassetname", null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput =  IScoreApplication.getAppContext().
                getAssets().open(asset_Name);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }
    @SuppressLint("RestrictedApi")
    private void showFABMenu(){
        isFABOpen=true;
        fab1.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
        fab3.setVisibility(View.VISIBLE);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    @SuppressLint("RestrictedApi")
    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab1.setVisibility(View.INVISIBLE);
        fab2.setVisibility(View.INVISIBLE);
        fab3.setVisibility(View.INVISIBLE);
    }

}



