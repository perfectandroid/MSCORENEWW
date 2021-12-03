package com.creativethoughts.iscore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.FullLenghRecyclertview;
import com.creativethoughts.iscore.Recharge.OnItemClickListener;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.OperatorSpinnerAdapter;
import com.creativethoughts.iscore.adapters.RecentHistoryAdapter;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment2;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.RechargeDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.RechargeModel;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.creativethoughts.iscore.utility.RechargeValue;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;


public class RechargeFragment extends Fragment implements View.OnClickListener {


    String TAG = "RechargeFragment";
    private static final String BUNDLE_TYPE = "bundle_type";
    private static final int PICK_CONTACT = 1;
    private View view;
    String from ="Recharge";
    String statusmessage;
    private AutoCompleteTextView mMobileNumEt;
    private EditText mAmountEt;
    private ImageButton selectContactImgBtn;
    private ImageButton browse_offer_image;
    private TextView mTitle,txt_amtinword;
    private Spinner mOperatorSpinner;
    private Spinner mCircleSpinner;
    private Spinner mAccountSpinner;
    private TextInputLayout mAccNumIpl;
    private TextInputLayout phonenumberLayout;
    private AppCompatEditText mAccNumEdt;
    private TextView tv_number_hint;
    LinearLayout lnr_offers;
    private int mSelectedType = 0;
    private static final int REACHARGE_OFFER = 10;
    private PaymentModel mPaymentModel;
    String operatorIds,BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist;
    String token,cusid,name;
    JSONArray Jarray;
    FullLenghRecyclertview rv_recarge_history;
    RecentHistoryAdapter adapter;
    ScrollView scrl_main;
    LinearLayout ll_recent_history;
    RechargeModel rechargeModel;
//    private List<String> operatorlist = new ArrayList< >();
    String operatorlist ="";
    public RechargeFragment() {
        // Required empty public constructor
    }

    public static RechargeFragment newInstance(int type) {
        RechargeFragment fragment = new RechargeFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );

        Bundle bundle = getArguments();

        if (bundle != null) {
            mSelectedType = bundle.getInt(BUNDLE_TYPE, 0);
        }
    }
    @Override
    public final void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_recharge, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        mMobileNumEt =   view.findViewById(R.id.phoneno);

        setAdapter(mMobileNumEt);
        mMobileNumEt.setOnItemClickListener((parent, viewTemp, position, id) -> {
             rechargeModel = new RechargeModel();
            rechargeModel.mobileNo = mMobileNumEt.getText().toString();
            String serveceProvider = RechargeDAO.getInstance().getServiceProvider(rechargeModel);
            int positionOnSpinner  = RechargeValue.getOperatorName(serveceProvider);
            if(positionOnSpinner < 0) positionOnSpinner = 0;
            mOperatorSpinner.setSelection(positionOnSpinner-1);

        });
        accountlist = new ArrayList<String>();
        mAmountEt = view.findViewById(R.id.amount);
        selectContactImgBtn = view.findViewById(R.id.select_contact_image);
        txt_amtinword= view.findViewById(R.id.txt_amtinword);
        selectContactImgBtn.setOnClickListener(this);
        browse_offer_image = view.findViewById(R.id.browse_offer_image);
        browse_offer_image.setOnClickListener(this);

        mAccNumIpl = view.findViewById(R.id.account_number_inputlayout);
        phonenumberLayout = view.findViewById(R.id.phoneno_layout);
        mAccNumEdt = view.findViewById(R.id.account_number);
        tv_number_hint = view.findViewById(R.id.tv_number_hint);

        mAccNumIpl.setVisibility(View.GONE);
        mAccNumEdt.setVisibility(View.GONE);

        mOperatorSpinner = view.findViewById(R.id.operator_spinner);
        mCircleSpinner = view.findViewById(R.id.circle_spinner);
        mAccountSpinner = view.findViewById(R.id.spnAccountNum);

        lnr_offers = view.findViewById(R.id.lnr_offers);
        Button mSubmitButton = view.findViewById(R.id.btn_submit);
        Button mCancelButton = view.findViewById(R.id.btn_clear);
        lnr_offers.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mTitle = view.findViewById(R.id.recharge_header_title);


        rv_recarge_history = view.findViewById(R.id.rv_recarge_history);
        scrl_main = view.findViewById(R.id.scrl_main);
        ll_recent_history = view.findViewById(R.id.ll_recent_history);
        setOperator(0,"");

        setCircle();

//        setAccountNumber();
        getAccList();
        updateType(mSelectedType);
        getHistory(mSelectedType);

        try {
            Log.e(TAG,"UID   154   "+IScoreApplication.encryptStart("6d657a687576656c6973636232353730"));
            Log.e(TAG,"PIN   154   "+IScoreApplication.encryptStart("5ed994e17bcd9f1cbf93b2e850507bd0"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        mAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                mAmountEt.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);

                    Double longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Double.parseDouble(originalString);
                    String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
                    //setting text after format to EditText
                    mAmountEt.setText(formattedString);
                    mAmountEt.setSelection(mAmountEt.getText().length());

                    String amnt = mAmountEt.getText().toString().replaceAll(",", "");
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
                    txt_amtinword.setText(""+amountInWordPop);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                mAmountEt.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() != 0) {
                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        mSubmitButton.setText( "PAY  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        mSubmitButton.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }

            }
        });
        Log.e(TAG,"mSelectedType 220   "+mSelectedType);
        String operatorName = mOperatorSpinner.getSelectedItem().toString();
        Log.e(TAG,"mSelectedType 220   "+mSelectedType+"  "+operatorName);
        switch (mSelectedType) {
            case 0:
                operatorIds = RechargeValue.getOperatorId(operatorName);

                break;
            case 1:
                operatorIds = RechargeValue.getPostPaidOperatorId(operatorName);

                break;
            case 2:
                operatorIds = RechargeValue.getLandLineOperatorId(operatorName);

                break;
            case 3:
                operatorIds = RechargeValue.getDTHOperatorId(operatorName);

                break;
            case 4:
                operatorIds = RechargeValue.getDataCardOperatorsId(operatorName);

                break;
            default:
                operatorIds = RechargeValue.getOperatorId(operatorName);

                break;
        }
        SharedPreferences operatorIdPref = getContext().getSharedPreferences(Config.SHARED_PREF10, 0);
        SharedPreferences.Editor operatorIdEdit = operatorIdPref.edit();
        operatorIdEdit.putString("operatorIds", operatorIds);
        operatorIdEdit.commit();
        return view;
    }




    private void setOperator(int type,String OperatorName) {
        String title;
        Activity activity = getActivity();
        if ( activity == null )
            return;
        Log.e(TAG,"type   365   "+type);
        OperatorSpinnerAdapter operatorSpinnerAdapter = new OperatorSpinnerAdapter(activity);
//        operatorlist = new ArrayList<>();
        operatorlist = "";

//        switch (type) {
        switch (mSelectedType) {
            case 0:
//                operatorlist.add(String.valueOf(RechargeValue.getAllOperator()));
                operatorlist = String.valueOf(RechargeValue.getAllOperator());
                operatorSpinnerAdapter.addItems(RechargeValue.getAllOperator(),OperatorName,type);
                title = "Prepaid";
                mTitle.setText( title );
                break;
            case 1:
//                operatorlist.add(String.valueOf(RechargeValue.getAllPostPaidOperator()));
                operatorlist = String.valueOf(RechargeValue.getAllPostPaidOperator());
                operatorSpinnerAdapter.addItems(RechargeValue.getAllPostPaidOperator(),OperatorName,type);
                title = "Postpaid";
                mTitle.setText( title );
                lnr_offers.setVisibility(View.GONE);
                break;
            case 2:
//                operatorlist.add(String.valueOf(RechargeValue.getAllLandLineOperator()));
                operatorlist = String.valueOf(RechargeValue.getAllLandLineOperator());
                operatorSpinnerAdapter.addItems(RechargeValue.getAllLandLineOperator(),OperatorName,type);
                title = "Landline";
                mTitle.setText( title );
                lnr_offers.setVisibility(View.GONE);
                break;
            case 3:
                title = "DTH";
//                operatorlist.add(String.valueOf(RechargeValue.getAllDTHOperator()));
                operatorlist  =String.valueOf(RechargeValue.getAllDTHOperator());
                operatorSpinnerAdapter.addItems(RechargeValue.getAllDTHOperator(),OperatorName,type);
                mTitle.setText( title );
                lnr_offers.setVisibility(View.VISIBLE);
                break;
            case 4:
                title = "Datacard";
//                operatorlist.add(String.valueOf(RechargeValue.getAllDataCardOperators()));
                operatorlist = String.valueOf(RechargeValue.getAllDataCardOperators());
                operatorSpinnerAdapter.addItems(RechargeValue.getAllDataCardOperators(),OperatorName,type);
                mTitle.setText( title );
                lnr_offers.setVisibility(View.GONE);
                break;
            default:
        }

        mOperatorSpinner.setAdapter(operatorSpinnerAdapter);
        mOperatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (IScoreApplication.DEBUG) Log.e(TAG, "position : " + position);
                if (IScoreApplication.DEBUG) Log.e(TAG, "Item : " + mOperatorSpinner.getSelectedItem().toString());

               Log.e(TAG,"mSelectedType 220   "+mSelectedType+"  "+mOperatorSpinner.getSelectedItem().toString());
                String operatorName = mOperatorSpinner.getSelectedItem().toString();
//                operatorIds = RechargeValue.getOperatorId(mOperatorSpinner.getSelectedItem().toString());
                switch (mSelectedType) {
                    case 0:
                        operatorIds = RechargeValue.getOperatorId(operatorName);

                        break;
                    case 1:
                        operatorIds = RechargeValue.getPostPaidOperatorId(operatorName);

                        break;
                    case 2:
                        operatorIds = RechargeValue.getLandLineOperatorId(operatorName);

                        break;
                    case 3:
                        operatorIds = RechargeValue.getDTHOperatorId(operatorName);

                        break;
                    case 4:
                        operatorIds = RechargeValue.getDataCardOperatorsId(operatorName);

                        break;
                    default:
                        operatorIds = RechargeValue.getOperatorId(operatorName);

                        break;
                }

                Log.e(TAG,"operatorIds  250   "+operatorIds+"   "+operatorName);
                mAccNumIpl.setVisibility(View.GONE);
                mAccNumEdt.setVisibility(View.GONE);

                if (( mSelectedType == 1 || mSelectedType == 2 ) && isCircleAccountNumberMandatory() ) {

                    mAccNumIpl.setVisibility(View.VISIBLE);
                    mAccNumEdt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });



        Log.e(TAG,"operatorlist    461     "+operatorlist+"   "+OperatorName);
        try {
            JSONArray jsonArray = new JSONArray(operatorlist);
            Log.e(TAG,"jsonArray    4611     "+jsonArray+"   "+jsonArray.getString(0));
            for (int i=0;i<jsonArray.length();i++){
                if (jsonArray.getString(i).equals(OperatorName)){
                    mOperatorSpinner.setSelection(i);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setCircle() {
        if ( getActivity() == null )
            return;
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_dark, RechargeValue.getAllCircle());

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCircleSpinner.setAdapter(spinnerAdapter);

        mCircleSpinner.setSelection(10);

        mCircleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (mSelectedType == 2) {

                    if (isCircleAccountNumberMandatory()) {
                        mAccNumIpl.setVisibility(View.VISIBLE);
                        mAccNumEdt.setVisibility(View.VISIBLE);
                    } else {
                        mAccNumIpl.setVisibility(View.GONE);
                        mAccNumEdt.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private boolean isCircleAccountNumberMandatory() {
        return (mSelectedType == 1 || mSelectedType == 2) && ((mOperatorSpinner.getSelectedItem().toString().contains("MTNL") && mCircleSpinner.getSelectedItem().toString().contains("Delhi")) || mOperatorSpinner.getSelectedItem().toString().contains("BSNL"));


    }



    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput = IScoreApplication.getAppContext().
                getAssets().open(Common.getCertificateAssetName());
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

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private void getAccList() {
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    requestObject1.put("BankKey",       IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));

                    requestObject1.put("BankHeader",    IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray newJArray = object.getJSONArray("OwnAccountdetailsList");

                                for(int i=0;i<newJArray.length();i++){
                                    try {
                                        JSONObject json = newJArray.getJSONObject(i);
//                                        if (json.getString("IsShowBalance").equals("1")){
                                            jresult.put(json);
                                            accountlist.add(json.getString("AccountNumber"));
//                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));

                                mAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            BranchName = jresult.getJSONObject(position).getString("BranchName");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                })
                                ;
                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));


                            }
                            else {

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }
                        }
                        catch (JSONException e) { }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) { }
        }
        else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
          //  DialogUtil.showAlert(getContext(),"Network is currently unavailable. Please try again later.");
        }

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
    private void updateType(int position) {
        switch (position) {
            case 0: // Mobile Recharge
                phonenumberLayout.setHint("Mobile number");
                tv_number_hint.setText("Mobile number");
                mSelectedType = 0;


                break;
            case 1: // Postpaid Mobile
                phonenumberLayout.setHint("Mobile number");
                tv_number_hint.setText("Mobile number");

                mSelectedType = 1;
                break;
            case 2: // Landlines Bill Payment
                phonenumberLayout.setHint("Phone Number");
                tv_number_hint.setText("Phone number");
                selectContactImgBtn.setVisibility(View.GONE);
                mSelectedType = 2;
                break;
            case 3: // DTH Recharge
                phonenumberLayout.setHint("SUBSCRIBER ID");
                tv_number_hint.setText("SUBSCRIBER ID");
                selectContactImgBtn.setVisibility(View.GONE);
                mSelectedType = 3;
                break;
            case 4: //Datacard recharge
                phonenumberLayout.setHint("SUBSCRIBER ID");
                tv_number_hint.setText("SUBSCRIBER ID");
                selectContactImgBtn.setVisibility(View.VISIBLE);
                mSelectedType = 4;

                break;

                default:
        }

//        setOperator(mSelectedType);
        setOperator(0,"");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_submit:
                onClickSubmit();
                break;
            case R.id.btn_clear:
                mMobileNumEt.setText("");
                txt_amtinword.setText("");
                mAmountEt.setText("");
                break;
            case R.id.select_contact_image:
                contactSelect();
                break;
            case R.id.lnr_offers:
               Intent i = new Intent(getContext(),ReachargeOfferActivity.class);
               startActivity(i);
                break;
            case R.id.browse_offer_image:
                Log.e(TAG,"operatorIds  362   "+operatorIds);
                SharedPreferences operatorIdPref = getContext().getSharedPreferences(Config.SHARED_PREF10, 0);
                SharedPreferences.Editor operatorIdEdit = operatorIdPref.edit();
                operatorIdEdit.putString("operatorIds", operatorIds);
                operatorIdEdit.commit();

                Intent intent = new Intent(getActivity(), ReachargeOfferActivity.class);
                intent.putExtra("operatorIds", operatorIds);
                startActivityForResult(intent, REACHARGE_OFFER);
                break;
            default:
                break;
        }
    }

    private void showToast(String value) {
        if (  getActivity()  == null )
            return;
        Toast toast = Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View toastView = layoutInflater.inflate(R.layout.custom_toast, view.findViewById(R.id.container));
        TextView textView = toastView.findViewById(R.id.text);
        textView.setText(value);
        toast.setView(toastView);
        toast.show();
    }



    private void onClickSubmit() {
        final String mobileNumber   = mMobileNumEt.getText().toString();
        final String amount         = mAmountEt.getText().toString();


        final String operatorName = mOperatorSpinner.getSelectedItem().toString();
        final String operatorId;
        final String message;

        int imageResource;
        String tempAmountInr = " And Amount INR.";
        String tempForRecharging = " For Recharging";
        String confirmSubscriberId = "Please Confirm The SUBSCRIBER ID ";

        switch (mSelectedType) {
            case 0:
                operatorId = RechargeValue.getOperatorId(operatorName);
//                message = "Country : India\n Mobile Number : " + mobileNumber +"\nOperetar - Circle : \nConnection Type : \nRecharge Amount :"+ tempAmountInr + amount + tempForRecharging;
                message = "Please Confirm The Mobile Number " + mobileNumber + tempAmountInr + amount + tempForRecharging;
                imageResource = R.mipmap.phone;
                break;
            case 1:
                operatorId = RechargeValue.getPostPaidOperatorId(operatorName);
                message = "Please Confirm The Mobile Number " + mobileNumber + tempAmountInr + amount + tempForRecharging;
                imageResource = R.mipmap.phone;
                break;
            case 2:
                operatorId = RechargeValue.getLandLineOperatorId(operatorName);
                message = "Please Confirm The Phone Number " + mobileNumber + tempAmountInr + amount + tempForRecharging;
                imageResource = R.mipmap.landline;
                break;
            case 3:
                operatorId = RechargeValue.getDTHOperatorId(operatorName);
                message = confirmSubscriberId + mobileNumber + tempAmountInr + amount + tempForRecharging;
                imageResource = R.mipmap.dish;
                break;
            case 4:
                operatorId = RechargeValue.getDataCardOperatorsId(operatorName);
                message = confirmSubscriberId + mobileNumber + tempAmountInr + amount + tempForRecharging;

                imageResource = R.mipmap.datacard;
                break;
            default:
                operatorId = RechargeValue.getOperatorId(operatorName);
                message = confirmSubscriberId + mobileNumber + tempAmountInr + amount + tempForRecharging;

                imageResource = R.mipmap.phone;
                break;
        }

        final String circleName = mCircleSpinner.getSelectedItem().toString();
        final String circleId = RechargeValue.getCircleId(circleName);

        final String accountNumber = mAccountSpinner.getSelectedItem().toString();
        final String circleAccountNo = mAccNumEdt.getText().toString();


        try{
            Long tempMobile = Long.parseLong(mobileNumber);
            if ( IScoreApplication.DEBUG )
                Log.d("parse long", tempMobile.toString() );

        }catch (Exception e){
            showToast("Please enter valid  mobile number");
            return ;
        }
        if( (mSelectedType == 0 || mSelectedType == 1) && mobileNumber.length() != 10 ){
            showToast("Please enter valid 10 digit mobile number");
        }
        else if( (mSelectedType == 2) && ( (mobileNumber.length() <10 ) || (mobileNumber.length() > 15  ) ) ){
            showToast("Please enter valid land line number");
        }
        else if( (mSelectedType == 3) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else if( (mSelectedType == 4) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else{
            if(amount.length() > 0){
                String Finamount = amount.replace(",","");

                try {
                    Integer.parseInt(Finamount);

                }catch (Exception e){
                    showToast("Please enter valid amount");
                    return;
                }
                if(TextUtils.isDigitsOnly(Finamount.trim())){
                     long value = Long.parseLong(Finamount.trim());
                    if( value < 10 || value > 10000){
                        showToast("Please enter the amount 10 to 10000 to recharge");
                        return;
                    }
                }
                else return;
            }
            else{
                showToast("Please enter the amount to recharge");
                return;
            }


            if (NetworkUtil.isOnline() && getContext() != null ) {

//                new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
////                        .setTitleText("Are you sure?")
//                        .setContentText(message)
//                        .setCancelText("No")
//                        .setConfirmText("Yes")
//                        .showCancelButton(true)
//                        .setCustomImage(imageResource)
//                        .setCancelClickListener(SweetAlertDialog::cancel)
//                        .setConfirmClickListener(sDialog -> {
//                            sDialog.dismissWithAnimation();
//                            recharge( accountNumber, mSelectedType, mobileNumber, circleId, operatorId, amount, circleAccountNo );
//                        })
//                        .show();

                RechargeConfirmation(accountNumber, mSelectedType, mobileNumber, circleId, operatorId, amount, circleAccountNo, operatorName );




             //   DialogUtil.showAlert(getActivity(),
                        //"Network is currently unavailable. Please try again later.");
            }
        }
    }

    private void RechargeConfirmation(String mAccountNumber,int type, String mMobileNumber, String mCircleId, String mOperatorId, String mAmount, String mCircleAccNo , String operatorName ) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getApplicationContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.recharge_confirmation_popup, null);
            TextView tvAcntno =  layout.findViewById(R.id.tvAcntno);
            TextView tvbranch =  layout.findViewById(R.id.tvbranch);
            TextView tv_mob =  layout.findViewById(R.id.tv_mob);
            TextView tv_oper =  layout.findViewById(R.id.tv_oper);
            TextView tv_cir =  layout.findViewById(R.id.tv_cir);
            TextView tv_amount =  layout.findViewById(R.id.tv_amount);
            TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
            TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
            TextView bt_ok =  layout.findViewById(R.id.bt_ok);
            TextView bt_cancel =  layout.findViewById(R.id.bt_cancel);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();

            tvAcntno.setText(""+mAccountNumber);
            tvbranch.setText(BranchName);
            tv_mob.setText(""+mMobileNumber);
            tv_oper.setText(mOperatorSpinner.getSelectedItem().toString());
            tv_cir.setText(mCircleSpinner.getSelectedItem().toString());

          /*  rechargeModel.accno = tvAcntno.getText().toString();
            rechargeModel.branch = BranchName;
            rechargeModel.operator = mOperatorSpinner.getSelectedItem().toString();
            rechargeModel.circle = mCircleSpinner.getSelectedItem().toString();
            rechargeModel.amount=mAmount;*/


            PaymentModel paymentModel = new PaymentModel( );
            paymentModel.setAccNo( tvAcntno.getText().toString()  );
            paymentModel.setOperator( mOperatorSpinner.getSelectedItem().toString()  );
            paymentModel.setCircle(mCircleSpinner.getSelectedItem().toString());
            paymentModel.setAmount( mAmount  );
            paymentModel.setBranch( BranchName  );


            Log.e(TAG,"operatorName     955    "+operatorName+"   "+mOperatorId+"   "+mAccountNumber+"   "+mCircleAccNo);

//            double num =Double.parseDouble(""+mAmount);
//            String stramnt = CommonUtilities.getDecimelFormate(num);
            String stramnt = mAmount.replace(",","");
            text_confirmationmsg.setText("Proceed Recharge With Above Amount"+ "..?");
            String[] netAmountArr = stramnt.split("\\.");
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
            tv_amount.setText("₹ " + CommonUtilities.getDecimelFormate(Double.parseDouble(stramnt)) );
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recharge( mAccountNumber, type, mMobileNumber, mCircleId, mOperatorId, mAmount, mCircleAccNo ,operatorName);
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void recharge(String mAccountNumber,int type, String mMobileNumber, String mCircleId, String mOperatorId, String mAmount, String mCircleAccNo, String operatorName ) {
        try {
            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
            String BASE_URL=pref.getString("oldbaseurl", null);
            UserCredential loginCredential = UserCredentialDAO.getInstance().getLoginCredential();
            String url = BASE_URL;
            mAccountNumber = mAccountNumber.replace(mAccountNumber.substring(mAccountNumber.indexOf(" (")+1, mAccountNumber.indexOf(')')+1), "");
            mAccountNumber = mAccountNumber.replace(" ","");
            AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(mAccountNumber);
            String accountType = accountInfo.accountTypeShort;


            Log.e(TAG,"mOperatorId   1013   "+mOperatorId);
            String accNo =mAccountNumber;
//            String reslts = CommonUtilities.result;
            String amt =mAmountEt.getText().toString();
            PaymentModel paymentModel = new PaymentModel( );
            paymentModel.setAccNo( accNo  );
            paymentModel.setBranch( BranchName  );
            paymentModel.setAmount( amt  );
            mPaymentModel = paymentModel;


            String operatorString = "&Operator=";
            String circleString = "&Circle=";
            String amountString = "&Amount=";
            String accountNoString = "&AccountNo=";
            String moduleString = "&Module=";
            String pinString = "&Pin=";
            switch (type) {
                case 0:
                    url +=
                            "/MobileRecharge?MobileNumer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mMobileNumber)) +
                                    operatorString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mOperatorId)) +
                                    circleString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mCircleId)) +
                                    amountString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAmount))  +
                                    accountNoString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAccountNumber))  +
                                    moduleString +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType))  +
                                    pinString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin)) +
                                    "&Type=" +  IScoreApplication.encodedUrl(IScoreApplication.encryptStart(String.valueOf(type))) +
                                    "&OperatorName=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(operatorName));
                    break;
                case 1:

                case 2:
                    url +=
                            "/POSTPaidBilling?MobileNumer=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mMobileNumber)) +
                                    operatorString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mOperatorId)) +
                                    circleString+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mCircleId)) +
                                    "&Circleaccount=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mCircleAccNo)) +
                                    amountString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAmount)) +
                                    accountNoString+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAccountNumber)) +
                                    moduleString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType)) +
                                    pinString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin)) +
                                    "&Type=" +  IScoreApplication.encodedUrl(IScoreApplication.encryptStart(String.valueOf(type))) +
                                    "&OperatorName=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(operatorName));
                    break;
                case 3:
                case 4: // Datacard Recharge

                    url +=
                            "/DTHRecharge?SUBSCRIBER_ID=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mMobileNumber)) +
                                    operatorString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mOperatorId)) +
                                    circleString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mCircleId)) +
                                    amountString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mAmount)) +
                                    accountNoString+ IScoreApplication.encodedUrl (IScoreApplication.encryptStart(mAccountNumber)) +
                                    moduleString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType)) +
                                    pinString + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin)) +
                                    "&Type=" +  IScoreApplication.encodedUrl(IScoreApplication.encryptStart(String.valueOf(type))) +
                                    "&OperatorName=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(operatorName));

                    break;

                default:
                    break;
            }

            Log.e(TAG,"url   538    "+url);
            NetworkManager.getInstance().connector(url, new ResponseManager() {
                @Override
                public void onSuccess(String result) {

                    parseResponse( result, mMobileNumber, mOperatorId );
                }

                @Override
                public void onError(String error) {
                    Toast.makeText( getContext(), error, Toast.LENGTH_SHORT ).show();
                }
            }, getActivity(), "Recharge processing. Please wait...");


        } catch (Exception e) {
            if ( IScoreApplication.DEBUG )
                Log.d("exc", e.toString() );
        }

    }
    private void parseResponse( String result , String mMobileNumber, String mOperatorId){
        Gson gson = new Gson();
        rechargeModel = new RechargeModel();
        rechargeModel.mobileNo = mMobileNumber;
        //rechargeModel.referenceNo = refid;
        rechargeModel.type = mSelectedType;
        rechargeModel.serviceProvider = mOperatorId;
        try {
            RechargeResult rechargeResult = gson.fromJson( result, RechargeResult.class );
            int statusCode = rechargeResult.getStatusCode();
            statusmessage= rechargeResult.getStatusMessage();

            Context context = getContext();
            assert context != null;
            if ( statusCode == 0 ){
//                alertMessage("Oops...!", new ArrayList<>(), "Failed. Does not have sufficient balance in selected account", false, false);
                alertMessage1(IScoreApplication.OOPS, statusmessage);

            }else if ( statusCode == 1 ){
                insertDb( rechargeModel );
                showSuccess( rechargeResult );
            }else if ( statusCode == 2 ){
                showFailure();
            }else if ( statusCode == 3 ){
                insertDb( rechargeModel );
                showPending();
            }else {
//                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
//                        .setTitleText(IScoreApplication.OOPS)
//                        .setContentText("Service not available")
//                        .show();
               alertMessage1(IScoreApplication.OOPS, statusmessage);
             //   alertMessage( "Success", keyValuePairs, message,true, false );

            }
        }catch ( Exception e){
            Toast.makeText( getContext(),"An error occured", Toast.LENGTH_SHORT ).show();
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
                alertDialog.dismiss();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
               // alertDialog.dismiss();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        alertDialog.show();
    }
    private void insertDb(RechargeModel rechargeModel)  {
        RechargeDAO rechargeDAO = new RechargeDAO();
        rechargeDAO.insertValues(rechargeModel);
    }
    private void showSuccess( RechargeResult rechargeResult ) {
        if ( getContext() == null)
            return;
       // String message = "Recharge Success. Amount: Rs."+rechargeResult.getAmount()  +"Ref.No:"+rechargeResult.getRefId();
        String message =statusmessage;
        ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
        KeyValuePair keyValuePair = new KeyValuePair();
        keyValuePair.setKey("Amount");
        keyValuePair.setValue(rechargeResult.getAmount() );
        keyValuePairs.add( keyValuePair );
        keyValuePair = new KeyValuePair();
        keyValuePair.setKey("Ref.Id");
        keyValuePair.setValue(rechargeResult.getRefId() );
        keyValuePair = new KeyValuePair();
        keyValuePair.setKey("Mobile No");
        keyValuePair.setValue( rechargeResult.getMobileNumber() );
        keyValuePairs.add( keyValuePair );

        switch (mSelectedType) {
            case 0:
               // message = "Mobile Recharge success.";
                message = statusmessage;
                break;
            case 1:
                message = statusmessage;
              //  message = "PostPaid Bill Payment successfully.";
                break;
            case 2:
                message = statusmessage;
              //  message = "Land line Bill Payment successfully.";
                break;
            case 3:
                message = statusmessage;
              //  message = "DTH Bill Payment successfully .";
                break;
            default:
                break;
        }
        alertMessage( "Success", keyValuePairs, message,true, false );


    }
    public void alertMessage(String title, ArrayList<KeyValuePair> keyValueList,String message ,boolean isHappy, boolean isBackButtonEnabled ){
      /*  getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
                mPaymentModel, isHappy, isBackButtonEnabled ) ).commit();*/
//        getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
//                 isHappy, isBackButtonEnabled ) ).commit();

        getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment2.getInstance( keyValueList  ,title, message, mPaymentModel, isHappy, isBackButtonEnabled, from) ).commit();

    }
    private void showFailure() {
        if ( getContext() == null)
            return;
    //    String message = "Recharge Processing, Please Check Status And Try Again Later.";
        String message =statusmessage;
        switch (mSelectedType) {
            case 0:
                message = statusmessage;
              //  message = "Mobile Recharge Processing, Please Check Status And Try Again Later.";
                break;
            case 1:
                message = statusmessage;
               // message = "Postpaid Recharge Processing, Please Check Status And Try Again Later.";
                break;
            case 2:
                message = statusmessage;
               // message = "LandLine Recharge Processing, Please Check Status And Try Again Later.";
                break;
            case 3: // DTH Recharge
                message = statusmessage;
             //   message = "DTH Recharge Processing, Please Check Status And Try Again Later.";
                break;
            default:
                break;
        }


       ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
       KeyValuePair keyValuePair = new KeyValuePair();
       keyValuePair.setKey("Message");
       keyValuePair.setValue(message);
       keyValuePairs.add( keyValuePair );

        alertMessage1(message, "");

//        alertMessage(  message, keyValuePairs, "",false, false);
    }

    private void showPending() {
        String message  ;
        switch (mSelectedType) {
            case 0: // Mobile Recharge
                message = statusmessage;
              //  message = "Mobile Recharge Pending";
                break;
            case 1: // Postpaid Mobile
             //   message = "PostPaid Bill Payment Pending";
                message = statusmessage;
                break;
            case 2: // Landlines Bill Payment
               // message = "Land line Bill Pay Request Pending";
                message = statusmessage;
                break;
            case 3: // DTH Recharge
               // message = "DTH Recharge Pending";
                message = statusmessage;
                break;
            default:
                message = statusmessage;
              //  message = "";
                break;
        }
        alertMessage2("Pending",message);

//        alertMessage( "Pending", new ArrayList<>(), message, false, false);
    }

    private void setAdapter(AutoCompleteTextView autoCompleteTextView){
        if ( getContext() == null )
            return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.select_dialog_item, RechargeDAO.getInstance().getPhoneDthNumber(mSelectedType));
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
    }




    private void contactSelect(){

        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        intent.setType( ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE );
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK && getActivity() != null ){
           try{
               Uri uriContact = data.getData();
               assert uriContact != null;
               Cursor cursor =
                       getActivity().getContentResolver().query(
                               uriContact, null, null, null, null);

               assert cursor != null;
               cursor.moveToFirst();
               String tempContact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               mMobileNumEt.setText(extractPhoneNumber(tempContact));

               closeCursor(cursor);
           }catch (Exception e){
               if(IScoreApplication.DEBUG)Log.e("contact ex", e.toString());
           }
        }
        if (requestCode == 10) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                Log.e(TAG,"strEditText  770   "+strEditText);
                mAmountEt.setText(""+strEditText);
            }
        }
    }

    private String extractPhoneNumber(String resultPhoneNumber){
        String result;
        try{
            result = resultPhoneNumber.replaceAll("\\D+","");
            if(result.length() > 10){
                result = result.substring( result.length()-10,result.length());
            }
        }catch (Exception e){
            result = "";
        }
        return  result;
    }

    private void closeCursor(Cursor cursor){
        try {
            cursor.close();
        }catch (Exception e){
            if(IScoreApplication.DEBUG) Log.e("Null pointer ex", e.toString());
        }
    }
    private class RechargeResult{

        @SerializedName("StatusCode")
        private int statusCode;
        @SerializedName("RefID")
        private String refId;
        @SerializedName("MobileNumber")
        private String mobileNumber;
        @SerializedName("Amount")
        private String amount;
        @SerializedName("AccNumber")
        private String accNo;
        @SerializedName("StatusMessage")
        private String statusMessage;

        private int getStatusCode() {
            return statusCode;
        }

        private String getRefId() {
            return refId;
        }

        private String getMobileNumber() {
            return mobileNumber;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAccNo() {
            return accNo;
        }

        public void setAccNo(String accNo) {
            this.accNo = accNo;
        }


        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    // handle back button's click listener
//                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(getActivity(),HomeActivity.class);
//                    startActivity(i);
//                    getActivity().finish();
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* Enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            // Handle the back button event

            Log.e(TAG,"HLLOOOO     910");

            Intent i = new Intent(getActivity(),HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }


    };

    private void getHistory(int mSelectedType) {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    cusid = userDetails.customerId;
                    name = userDetails.userCustomerName;
//                    AccountSummary/RechargeHistory
//
//                    { "ReqMode":21, "Token":"kdydnsf","FK_Customer","1253","BranchCode":"1"}

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("BranchCode", IScoreApplication.encryptStart("0"));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
                    requestObject1.put("RechargeType", IScoreApplication.encryptStart(String.valueOf(mSelectedType)));

                    Log.e(TAG,"requestObject1     1341   "+requestObject1);


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRechargeHistory(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try{
                            ll_recent_history.setVisibility(View.GONE);
                            Log.e(TAG," getRechargeHistory    1360       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeHistory");
                                Jarray  = jsonObj1.getJSONArray("RechargeHistoryList");
                                if(Jarray.length()!=0) {
                                    ll_recent_history.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
                                    rv_recarge_history.setLayoutManager(lLayout);
                                    rv_recarge_history.setHasFixedSize(true);
                                    adapter = new RecentHistoryAdapter(getContext(),Jarray);
                                    rv_recarge_history.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position, String data, String mode) {


                                            Log.e(TAG,"onItemClick     1375    "+position);
                                            try {
                                                JSONObject jsonObject = Jarray.getJSONObject(position);
                                                Log.e(TAG,"onItemClick     1375    "+jsonObject.getString("MobileNo")+"     "+jsonObject.getString("Operator"));
                                                mMobileNumEt.setText(""+jsonObject.getString("MobileNo"));
//                                                setOperator(Integer.parseInt(jsonObject.getString("Operator")));
                                                setOperator(1,jsonObject.getString("OperatorName"));
                                                operatorIds = jsonObject.getString("Operator");
                                                setCircle();
                                                mAmountEt.setText(""+jsonObject.getInt("RechargeRs"));
                                                scrl_main.scrollTo(0,0);


//                                                Log.e(TAG,"operatorlist    461     "+operatorlist+"   "+jsonObject.getString("OperatorName"));
//                                                try {
//                                                    JSONArray jsonArray = new JSONArray(operatorlist);
//                                                    Log.e(TAG,"jsonArray    4611     "+jsonArray+"   "+jsonArray.getString(0));
//                                                    for (int i=0;i<jsonArray.length();i++){
//                                                        if (jsonArray.getString(i).equals(jsonObject.getString("OperatorName"))){
//                                                            mOperatorSpinner.setSelection(i);
//                                                        }
//                                                    }
//
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            }
                            else {
                                ll_recent_history.setVisibility(View.GONE);

                                try{
//                                    JSONObject jobj = jsonObj.getJSONObject("RechargeHistory");
//                                    String ResponseMessage = jobj.getString("ResponseMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                                    builder.setMessage(ResponseMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();

                                }
                                catch (Exception e){
//                                    String EXMessage = jsonObj.getString("EXMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();

                                }
                            }

                        }
                        catch (JSONException e)
                        {
                            ll_recent_history.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        ll_recent_history.setVisibility(View.GONE);
                    }
                });

            }
            catch (Exception e)
            {
                ll_recent_history.setVisibility(View.GONE);
            }
        }
        else {
//            DialogUtil.showAlert(RechargeHistoryActivity.this,
//                    "Network is currently unavailable. Please try again later.");
            ll_recent_history.setVisibility(View.GONE);
        }
    }

}
