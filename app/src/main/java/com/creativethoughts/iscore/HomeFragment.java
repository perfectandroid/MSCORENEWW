package com.creativethoughts.iscore;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.NewAccountExpandableListAdapter;
import com.creativethoughts.iscore.adapters.PassbookListAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.SyncAll;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    private NewAccountExpandableListAdapter mAccountExpandableListAdapter;


    private TextView mAvailableAmountTV;
    private TextView mUnclearBalanceTV;
    private TextView mAccountTypeTV;
    private TextView txtLastUpdatedAt;

    private String acChange;

    private TextView mEmptyText;

    List<Transaction> transactionList;
    ListView lv_passbook;
    PassbookListAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(int sectionNumber) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public static String getHeaderString(String inputDate) {
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
           if ( IScoreApplication.DEBUG )
               Log.e("Parse E", e.toString() );
        }


        if (date == null) {
            return "";
        }

         SimpleDateFormat convetDateFormat = new SimpleDateFormat("d MMM yyyy",Locale.ENGLISH);

        return convetDateFormat.format(date);
    }

    public static void sortAccordingToTime(ArrayList<Transaction> sortlist) {
        CountryComparator comparator = new CountryComparator();

        Collections.sort(sortlist, comparator);
    }

    private static Date getDate(String value) {
          SimpleDateFormat formatter = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
           Log.e("Parse e", e.toString() );
        }

        return date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mAvailableAmountTV  =    rootView.findViewById( R.id.available_balance );
        mAccountTypeTV      =    rootView.findViewById( R.id.Account );
        txtLastUpdatedAt    =    rootView.findViewById( R.id.txtLastUpdatedAt );

        mEmptyText =   rootView.findViewById(R.id.empty_list);

        mEmptyText.setVisibility(View.GONE);

        mUnclearBalanceTV =  rootView.findViewById(R.id.unclear_balance);

        lv_passbook =rootView.findViewById(R.id.lv_passbook);


        Spinner defaulTtrans =   rootView.findViewById(R.id.spnAccountNum);

//        try {
//            if (PBAccountInfoDAO.getInstance().isAccountInfoExits()) {
//                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//                if (settingsModel == null) {
//                    CommonUtilities.setAccountNumber(null, defaulTtrans, getActivity());
//                } else {
//                    CommonUtilities.setAccountNumberPassbook(settingsModel.customerId, defaulTtrans, getActivity());
//                }
//                defaulTtrans.setOnItemSelectedListener(new OnItemSelectListener());
//            } else {
//
//                Toast toast = Toast.makeText(getActivity(), "No Accounts Matched With respect to particular customer", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        } catch (Exception e) {
//            if ( IScoreApplication.DEBUG ){
//                Log.e( TAG, e.toString() );
//            }
//
//        }

      /*  ExpandableListView expListView =   rootView.findViewById(R.id.listViewDateAccountDetails);

        mAccountExpandableListAdapter = new NewAccountExpandableListAdapter(getActivity());

        // setting list adapter
        expListView.setAdapter(mAccountExpandableListAdapter);
*/
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //expListView
                   // .setIndicatorBounds(width - getPixelFromDips(50), width - getPixelFromDips(10));
        } else {
            //expListView.setIndicatorBoundsRelative(width - getPixelFromDips(50),
                   // width - getPixelFromDips(10));
        }
        return rootView;
    }

/*
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }*/

    private int getPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    private void updateTopView() {
        AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(acChange);

        String unClrBal = accountInfo.unClrBal;

        String availableBal = accountInfo.availableBal;


        if (!TextUtils.isEmpty(unClrBal)) {

            double dblUnclrbal = /*Double.valueOf(unClrBal)*/ Double.parseDouble( unClrBal );


            if (dblUnclrbal <= 0) {
                mUnclearBalanceTV.setTextColor(Color.RED);

                if(dblUnclrbal == 0) {
                    unClrBal = "" + Math.abs(dblUnclrbal);
                }
                String unclearBalance = "Rs " + unClrBal;

                double num = Double.parseDouble(unClrBal);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                mUnclearBalanceTV.setText( "\u20B9 "+ fmt.format(num) );
                mUnclearBalanceTV.setText( "\u20B9 "+ CommonUtilities.getDecimelFormate(num) );
            } else {
                mUnclearBalanceTV.setTextColor(Color.parseColor("#7E5858"));

                String[] parts = unClrBal.split(Pattern.quote("."));
                String d1 = parts[0];
                String d2 = parts[1];
                String unclearBalance;
                if (d2.length() <= 1) {


                    double num = Double.parseDouble(d1 + "." + d2 + "0");
                    DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                    mUnclearBalanceTV.setText( "\u20B9 "+ fmt.format(num) );
                    mUnclearBalanceTV.setText( "\u20B9 "+ CommonUtilities.getDecimelFormate(num) );
                } else {
                    unclearBalance = "Rs " + unClrBal;

                    double num = Double.parseDouble(unClrBal);
                    DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                    mUnclearBalanceTV.setText( "\u20B9 "+ fmt.format(num) );
                    mUnclearBalanceTV.setText( "\u20B9 "+ CommonUtilities.getDecimelFormate(num) );
                }
            }
        }
        else {
            String unclearBal = "\u20B9 " + 0.00;
            mUnclearBalanceTV.setText( unclearBal );
        }

        if (!TextUtils.isEmpty(availableBal)) {
            String[] parts = availableBal.split(Pattern.quote("."));
            String d1 = parts[0];
            String d2 = parts[1];
            String balanceStatus ;
            if (Long.parseLong(d1) < 0){
                balanceStatus = " (Dr)";
                Long temp = Long.parseLong(d1);
                temp = -1* temp;
                d1 = Long.toString(temp);
            }
            else balanceStatus = " (Cr)";
            if (d2.length() <= 1) {

                double num = Double.parseDouble( d1 + "." + d2 + "0");
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                mAvailableAmountTV.setText("Rs " + fmt.format(num)+ balanceStatus);
                mAvailableAmountTV.setText("Rs " + CommonUtilities.getDecimelFormate(num)+ balanceStatus);
            } else {

                double num = Double.parseDouble(availableBal);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                mAvailableAmountTV.setText("Rs " + fmt.format(num) + balanceStatus);
                mAvailableAmountTV.setText("Rs " + CommonUtilities.getDecimelFormate(num) + balanceStatus);
            }
        }
        else {
            mAvailableAmountTV.setText("Rs " + 0.00);
        }

        mAccountTypeTV.setText(accountInfo.accountAcType);

        updateView();
    }

    private void updateView() {
      //  adapter.clearAll();
//        mAccountExpandableListAdapter.clearAll();

     //   SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

//        if (settingsModel != null && settingsModel.lastSyncTime > 0) {
//            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy hh:mma z", Locale.ENGLISH);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(settingsModel.lastSyncTime);
//
//            txtLastUpdatedAt.setText(formatter.format(calendar.getTime()));
//        }
//        else {
//            txtLastUpdatedAt.setText("");
//        }

      //  prepareListData();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    private void prepareListData() {

//        ArrayList<Transaction> transactions = NewTransactionDAO.getInstance().getTransactions1(acChange);
//
//        if(transactions.size() == 0) {
//         //   SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//            SharedPreferences SelectedDaysSP = getActivity().getSharedPreferences(Config.SHARED_PREF37, 0);
//            String SelectedDays = SelectedDaysSP.getString("SelectedDays",null);
//            final int days;
//            if (SelectedDays == null || Integer.parseInt(SelectedDays) <= 0) {
//                days = 30;
//            } else {
//                days = Integer.parseInt(SelectedDays);
//            }
//            mEmptyText.setVisibility(View.VISIBLE);
//            lv_passbook.setVisibility(View.INVISIBLE);
//            mEmptyText.setText("There are no transactions to display for the last " + days + " days");
//         //   mAccountExpandableListAdapter.clearAll();
//            return;
//        }

        lv_passbook.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.GONE);


//        sortAccordingToTime(transactions);
//        adapter = new PassbookListAdapter(getActivity(), R.layout.passbook_list_row, transactions);
//        lv_passbook.setAdapter(adapter);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        /*
          Inflate menu for sms list screen.
         */
        inflater.inflate(R.menu.home, menu);

    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }*/

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (NetworkUtil.isOnline()) {
                final ProgressDialog pDialog =
                        ProgressDialog.show(getActivity(), "", "Refreshing Accounts...");
                getCustomerImage();
                SyncAll.syncAllAccounts(new SyncAll.OnSyncStateListener() {
                    @Override
                    public void onCompleted() {
                        updateTopView();
                        pDialog.dismiss();
                    }
                    @Override
                    public void onFailed() {
                        updateTopView();
                        pDialog.dismiss();
                    }
                }, true, getContext());
            }
            else {
                DialogUtil.showAlert(getActivity(),
                        "Network is currently unavailable. Please try again later.");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private static class CountryComparator implements Comparator<Transaction> {
        public int compare(Transaction left, Transaction right) {

            String leftEffectiveDate = left.effectDate;
            String rightEffectiveDate = right.effectDate;

            if (TextUtils.isEmpty(leftEffectiveDate) || TextUtils.isEmpty(rightEffectiveDate)) {
                return 0;
            }

            Date leftDate = getDate(leftEffectiveDate);
            Date rightDate = getDate(rightEffectiveDate);

            if(leftDate == null || rightDate == null) {
                return 0;
            }

            return (int)((rightDate.getTime() / 100000) - (leftDate.getTime() / 100000));

        }
    }

    private class OnItemSelectListener implements AdapterView.OnItemSelectedListener {

        private OnItemSelectListener() {

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object item = parent.getItemAtPosition(position);
            acChange = (String) item;
            acChange = acChange.replace(acChange.substring(acChange.indexOf(" (")+1, acChange.indexOf(")")+1), "");
            acChange = acChange.replace(" ","");
            updateTopView();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener
                //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
                getActivity().finish();
                return true;
            }
            return false;
        });

    }




    private void getCustomerImage() {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
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

                    SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid = customerIdSP.getString("customerId","");
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));


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
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobjt = jObject.getJSONObject("CustomerImageDets");
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0"))
                            {
                                SharedPreferences custimageSP = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                SharedPreferences.Editor custimageSPEditer = custimageSP.edit();
                                custimageSPEditer.putString("custimage", jobjt.getString("CusImage"));
                                custimageSPEditer.commit();
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

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
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

}