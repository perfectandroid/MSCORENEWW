package com.creativethoughts.iscore;

import android.Manifest;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.coolerfall.download.DownloadListener;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.adapters.NewAccountExpandableListAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.gsonmodel.SyncParent;
import com.creativethoughts.iscore.ui.widget.ZaarkDialog;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.PreferenceUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SearchResultActivity extends AppCompatActivity {
    private static final String BASE_URL_KEY = "iscore_base_url_key";
    private static final int REQUEST_CODE = 54565;

    private NewAccountExpandableListAdapter listAdapter;

    private ProgressDialog pDialog;

    private String mFromDate;
    private String mToDate;
    private String mAccountNo;

    private DownloadManager mDownloadManager;

    private ProgressDialog pPDFDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mDownloadManager = new DownloadManager();

        Intent searchinfo = getIntent();
        String fromDate_Search = searchinfo.getStringExtra("fromDate");
        String[] parts = fromDate_Search.split("-");
        String days = parts[0];
        String months = parts[1];
        String years = parts[2];

        String dateReceivedFromUser = months + "/" + days + "/" + years;

        String toDateSearch = searchinfo.getStringExtra("toDate");
        String[] parts1 = toDateSearch.split("-");
        String days1 = parts1[0];
        String months1 = parts1[1];
        String years1 = parts1[2];

        String toUser1 = months1 + "/" + days1 + "/" + years1;

        String maxAmount = searchinfo.getStringExtra("maxAmount1");
        String minAmount = searchinfo.getStringExtra("minAmount1");
        String order = searchinfo.getStringExtra("order");

        if (TextUtils.isEmpty(maxAmount)) {
            maxAmount = "10000000000";
        }

        if (TextUtils.isEmpty(minAmount)) {
            minAmount = "1";
        }

        if (order.equals("Ascending")) {
            order = "A";
        } else if (order.equals("Descending")) {
            order = "D";
        }

        String transType = searchinfo.getStringExtra("transType");
        String sortField = searchinfo.getStringExtra("sortField");

        switch (transType) {
            case "Debit":
                transType = "D";
                break;
            case "Credit":
                transType = "C";
                break;
            default:
                transType = "CD";
                break;
        }

        mFromDate = dateReceivedFromUser;
        mToDate = toUser1;


        String accountNo = searchinfo.getStringExtra("acno");

        mAccountNo = accountNo;

        SearchServer searchServer =
                new SearchServer(transType, accountNo, sortField, order, dateReceivedFromUser,
                        toUser1, minAmount, maxAmount);
        searchServer.execute();



        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.listViewSearchResultAccountDetails);

        listAdapter = new NewAccountExpandableListAdapter(this);

        assert expListView != null;
        expListView.setAdapter(listAdapter);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView
                    .setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        } else {
            expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(50),
                    width - GetPixelFromDips(10));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            if (requestCode == REQUEST_CODE) {
                //noinspection StatementWithEmptyBody
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do something
                }else{
//                    DialogUtil.showAlert(SearchResultActivity.this,
//                            "Please allow storage permission from settigns.Other wise your file download may fail.");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SearchResultActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(SearchResultActivity.this);
                        alertDialogBuilder.setMessage("Please allow storage permission inorder to" +
                                " download file to sdcard.You can also allow permission from application settings");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                            ActivityCompat.requestPermissions(SearchResultActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CODE);
                            dialog.dismiss();

                        });
                        alertDialogBuilder.show();

                    } else {

                        //disable permission ever

                        final SharedPreferences preferencess=getSharedPreferences("mypref",MODE_PRIVATE);
                        if (preferencess.getBoolean("ifdenyfirststorage",true)) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchResultActivity.this);
                            alertDialogBuilder.setMessage("Your downloads may fail cause you deny the storage permission." +
                                    "Please allow storage permission from app settings if your download fail");
                            alertDialogBuilder.setCancelable(false);
                            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {

                                SharedPreferences.Editor editor=preferencess.edit();
                                editor.putBoolean("ifdenyfirststorage",false);
                                editor.apply();
                                dialog.dismiss();

                            });
                            alertDialogBuilder.show();

                        }

                    }

                }
             }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDownloadManager.release();
    }

    private int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_result, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//        String BASE_URL=pref.getString("oldbaseurl", null);

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        if (item.getItemId() == R.id.action_download) {

            if (!TextUtils.isEmpty(mAccountNo) || !TextUtils.isEmpty(mFromDate) || !TextUtils
                    .isEmpty(mToDate)) {
                UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

                String custId = user.customerId;

                UserCredential userCred = UserCredentialDAO.getInstance().getLoginCredential();

                String pin1 = userCred.pin;

                AccountInfo accountInformation =
                        PBAccountInfoDAO.getInstance().getAccountInfo(mAccountNo);

                String accountType = accountInformation.accountTypeShort;

                String IDDemandDeposit = accountInformation.fkDemandDepositID;
                String url;
                try {
                    url = BASE_URL + "/GenerateStatementOfAccount?" +
                            "Module=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType)) +
                            "&FromDate=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mFromDate))  +
                            "&ToDate=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mToDate))  +
                            "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                            "&Pin=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin1))  +
                            "&IDDemandDeposit=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(IDDemandDeposit));
                } catch (Exception e) {

                    url = BASE_URL + "/GenerateStatementOfAccount";
                }

//                Log.d(TAG, "onOptionsItemSelected: " + url);

                GetPdfUrlAsyncTask getPdfUrlAsyncTask = new GetPdfUrlAsyncTask(url);
                getPdfUrlAsyncTask.execute();
            }
            return true;
        }
        return false;
    }

    private void downloadFile(String url, String fileName) {
        DownloadRequest request = new DownloadRequest().setDownloadId(39)
//                .setSimpleDownloadListener(new SimpleListener())
                .setRetryTime(2).setDestDirectory(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .getAbsolutePath() + "/" + "mScore/").setFileName(fileName)
                .setAllowedNetworkTypes(this, DownloadRequest.NETWORK_WIFI)
                .setDownloadListener(new Listener())
                .setProgressInterval(1000).setUrl(url);
        mDownloadManager.add(request);
    }

    private class SearchServer extends AsyncTask<String, integer, Integer> {
        private final String mTransType;
        private final String mAccountNo;
        private final String mSortField;
        private final String mOrder;
        private final String mFromDate;
        private final String mToDate;
        private final String mMinAmt;
        private final String mMaxAmt;
        //        AlertDialog alertDialog;

        private final ArrayList<Transaction> mTransactions = new ArrayList<>();

        private SearchServer(String transType, String accountNo, String sortField, String order, String fromDate, String toDate, String minAmt, String maxAmt) {
            mTransType = transType;
            mAccountNo = accountNo;
            /*Extract account number
            mAccountNo = mAccountNo.replace(mAccountNo.substring(mAccountNo.indexOf(" (")+1, mAccountNo.indexOf(")")+1), "");
            mAccountNo = mAccountNo.replace(" ","");*/
            mSortField = sortField;
            mOrder = order;
            mFromDate = fromDate;
            mToDate = toDate;
            mMinAmt = minAmt;
            mMaxAmt = maxAmt;
        }

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog
                    .show(SearchResultActivity.this, "", "Loading Content Please wait...");

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {

            return getSearching();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub

            pDialog.dismiss();



            if (result!=IScoreApplication.FLAG_NETWORK_EXCEPTION) {

                if (IScoreApplication.checkPermissionIemi(result, SearchResultActivity.this)) {

                    if (mTransactions.size() <= 0) {

                        DialogUtil.showAlert(SearchResultActivity.this, "No transactions available",
                                new ZaarkDialog.OnPositiveButtonClickListener() {
                                    @Override
                                    public void onClick() {
                                        finish();
                                    }
                                });

                        //                alertDialog = new AlertDialog.Builder(SearchResultActivity.this).create();
                        //
                        //                alertDialog.setTitle("Filter Alert");
                        //                // alertDialog.setIcon(R.drawable.success);
                        //                alertDialog.setCanceledOnTouchOutside(false);
                        //                alertDialog.setMessage("Transactions Are Empty");
                        //                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        //                        new DialogInterface.OnClickListener() {
                        //                            public void onClick(DialogInterface dialog, int which) {
                        //                                //Intent A = new Intent(SearchResultActivity.this, Search.class);
                        //                                //startActivity(A);
                        //                                finish();
                        //                            }
                        //                        });
                        //                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        //                    @Override
                        //                    public void onDismiss(DialogInterface dialog) {
                        //                        //Intent A = new Intent(PassBookAccount1.this, Search.class);
                        //                        //startActivity(A);
                        //                        finish();
                        //                    }
                        //                });
                        //                alertDialog.show();


                    }
                    else if (mTransactions.size() > 0) {

                        HomeFragment.sortAccordingToTime(mTransactions);

                        String date = "";

                        ArrayList<Transaction> sortedList = new ArrayList<>();

                        for (int i = 0; i < mTransactions.size(); i++) {
                            final Transaction transaction = mTransactions.get(i);

                            if (transaction == null) {
                                continue;
                            }

                            String effectiveDate = transaction.effectDate;

                            if (TextUtils.isEmpty(effectiveDate)) {
                                continue;
                            }

                            String itemEffectiveDate = HomeFragment.getHeaderString(effectiveDate);

                            if (i > 0 && (!date.equalsIgnoreCase(itemEffectiveDate))) {

                                if (sortedList.size() > 0) {
                                    listAdapter.addItem(date, sortedList);

                                    sortedList = new ArrayList<>();
                                }

                                date = itemEffectiveDate;
                            } else if (i == 0) {
                                date = itemEffectiveDate;
                            }

                            sortedList.add(transaction);
                        }

                        if ((!TextUtils.isEmpty(date)) && sortedList.size() > 0) {
                            listAdapter.addItem(date, sortedList);
                        }
                    }
                }
            }

            super.onPostExecute(result);

        }

        private int getSearching() {

            try {

                UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

                String custId = user.customerId;

                UserCredential userCred = UserCredentialDAO.getInstance().getLoginCredential();

                String pin1 = userCred.pin;

                AccountInfo accountInformation =
                        PBAccountInfoDAO.getInstance().getAccountInfo(mAccountNo);

                String IDDemandDeposit = accountInformation.fkDemandDepositID;
                String accountType = accountInformation.accountTypeShort;

//                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                String BASE_URL=pref.getString("oldbaseurl", null);

                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL=pref.getString("baseurl", null);

                final String url =
                        BASE_URL +
                                "/TransactionSearch?Module="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType))+
                                "&TransType=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mTransType))  +
                                "&FromAmount=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mMinAmt))  +
                                "&ToAmount=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mMaxAmt)) +
                                "&FromDate=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mFromDate)) +
                                "&ToDate=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mToDate))  +
                                "&SortField=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mSortField)) +
                                "&SortType=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mOrder)) +
                                "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                                "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin1)) +
                                "&IDDemandDeposit=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart(IDDemandDeposit)) ;

                String text1 = ConnectionUtil.getResponse(url);

//                Log.d(TAG, "getSearching: " + url);
//                Log.d(TAG, "getSearching text1: " + text1);
//                System.out.println("Search Url : " + url);
//                System.out.println("Search text1 : " + text1);

                if (!TextUtils.isEmpty(text1)) {

                    if(IScoreApplication.containAnyKnownException(text1)){
                        return IScoreApplication.getFlagException(text1);
                    }
                    String s2 = "{\"acInfo\":null}";

                    String s3 = text1.trim();

                    if (s3.equals(s2)) {

                        //                        UserCredentialDAO.getInstance().deleteAllUserData();

                        return -1;

                    } else {
                        SyncParent syncParent = new Gson().fromJson( s2, SyncParent.class );

                        JSONObject jsonObject = new JSONObject(text1);

                        JSONArray acInfoArr = jsonObject.optJSONArray("acInfo");

                        if (acInfoArr == null || acInfoArr.length() == 0) {
                            return -1;
                        }

                        JSONObject jsonFirstAcInfo = acInfoArr.optJSONObject(0);

                        if (jsonFirstAcInfo == null) {
                            return -1;
                        }

                        UserDetails userDetails = new UserDetails();
                        int user_customerId = jsonFirstAcInfo.optInt("customerId");
                        if (user_customerId <= 0) {
                            return -1;
                        }

                        userDetails.customerId = String.valueOf(user_customerId);

                        userDetails.userCustomerNo = jsonFirstAcInfo.optString("customerNo");
                        userDetails.userCustomerName = jsonFirstAcInfo.optString("customerName");
                        userDetails.userCustomerAddress1 =
                                jsonFirstAcInfo.optString("customerAddress1");
                        userDetails.userCustomerAddress2 =
                                jsonFirstAcInfo.optString("customerAddress2");
                        userDetails.userCustomerAddress3 =
                                jsonFirstAcInfo.optString("customerAddress3");
                        userDetails.userMobileNo = jsonFirstAcInfo.optString("mobileNo");
                        userDetails.userPin = jsonFirstAcInfo.optString("pin");
                        userDetails.userDefault1 = "default1";
                        userDetails.userLogin = "1";

                        JSONArray jsonAccountsArr = jsonFirstAcInfo.optJSONArray("accounts");

                        if (jsonAccountsArr == null || jsonAccountsArr.length() == 0) {
                            return -2;
                        }

                        for (int j = 0; j < jsonAccountsArr.length(); j++) {
                            JSONObject jsonAccObj = jsonAccountsArr.optJSONObject(j);

                            AccountInfo accountInfo = new AccountInfo();
                            // String account_account = jsonAccObj.optString("account");
                            accountInfo.accountAcno = jsonAccObj.optString("acno");

                            if (!mAccountNo.equalsIgnoreCase(accountInfo.accountAcno)) {
                                continue;
                            }

                            accountInfo.accountModule       = jsonAccObj.optString("module");
                            accountInfo.accountAcType       = jsonAccObj.optString("acType");
                            accountInfo.accountTypeShort    = jsonAccObj.optString("typeShort");
                            accountInfo.accountBranchCode   = jsonAccObj.optString("branchCode");
                            accountInfo.accountBranchName   = jsonAccObj.optString("branchName");
                            accountInfo.accountBranchShort  = jsonAccObj.optString("branchShort");
                            accountInfo.accountDepositDate  = jsonAccObj.optString("depositDate");
                            accountInfo.accountOppMode      = jsonAccObj.optString("oppmode");
                            int accountFkPbDemandDeposit    = jsonAccObj.optInt("demandDeposit_id");
                            accountInfo.fkDemandDepositID   =
                                    String.valueOf(accountFkPbDemandDeposit);
                            accountInfo.accountCustomerID   = jsonAccObj.optString("FK_CustomerID");
                            accountInfo.userLastAcessDate   = jsonAccObj.optString("lastacessdate");
                            accountInfo.availableBal        = jsonAccObj.optString("AvailableBal");
                            accountInfo.unClrBal            = jsonAccObj.optString("UnClrBal");

                            String[] parts  = accountInfo.userLastAcessDate.split("/");
                            String days6    = parts[0];
                            String months7  = parts[1];
                            String years8   = parts[2];

                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                            Date date;

                            date = originalFormat.parse(days6 + "-" + months7 + "-" + years8);
                            accountInfo.lastTimeStamp = originalFormat.format(date);

                            JSONArray jsonTransArr = jsonAccObj.optJSONArray("transactions");

                            if (jsonTransArr == null || jsonTransArr.length() == 0) {
                                continue;
                            }
                            for (int k = 0; k < jsonTransArr.length(); k++) {

                                JSONObject jsonTrans = jsonTransArr.optJSONObject(k);

                                Transaction transaction = new Transaction();

                                String transactionsNo = jsonTrans.optString("transaction");

                                transaction.demandDepositNo =
                                        jsonTrans.optString("fk_DemandDeposit");

                                transaction.amount = jsonTrans.optString("amount");
                                transaction.chequeNo = jsonTrans.optString("chequeNo");
                                transaction.chequeDate = jsonTrans.optString("chequeDate");
                                transaction.narration = jsonTrans.optString("narration");
                                transaction.transType = jsonTrans.optString("transType");
                                transaction.remarks = jsonTrans.optString("remarks");
                                transaction.effectDate = jsonTrans.optString("effectDate");

                                transaction.transactionNo = transactionsNo;
                                transaction.accNo = accountInfo.accountAcno;

                                mTransactions.add(transaction);

                            }
                        }
                    }
                }else{
                    return IScoreApplication.FLAG_NETWORK_EXCEPTION;
                }
            } catch (Exception js) {
                js.printStackTrace();
            }

            return 1;

        }
    }

    class Values {
        String url;
        String fileName;
         int checkError=0;
    }

    private class GetPdfUrlAsyncTask extends AsyncTask<Void, Void, Values> {

        private final String mUrl;

        private GetPdfUrlAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pPDFDialog = new ProgressDialog(SearchResultActivity.this);
            pPDFDialog.setMessage("Downloading file. Please wait...");
            pPDFDialog.setMax(100);
            pPDFDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //noinspection deprecation
            pPDFDialog.setProgressDrawable(getResources().getDrawable(
                    R.drawable.custom_progress_bar_horizontal));
            pPDFDialog.setIndeterminate(false);
            pPDFDialog.setCancelable(false);
            pPDFDialog.show();

//            pDialog = ProgressDialog
//                    .show(SearchResultActivity.this, "", "Loading Content Please wait...");
        }


        @Override
        protected Values doInBackground(Void... params) {

//            System.out.println("mUrl : " + mUrl);
            String text = ConnectionUtil.getResponse(mUrl);

//            System.out.println("text : " + text);

            if (TextUtils.isEmpty(text)) {

                Values values=new Values();
                values.checkError=850;
                return values;
            }

            if(IScoreApplication.containAnyKnownException(text)){
                Values values=new Values();
                values.checkError= IScoreApplication.getFlagException(text);
                return values;
            }

            try {
                JSONObject jsonObject = new JSONObject(text);

                JSONArray statementOfAccountsArray = jsonObject.optJSONArray("StatementOfAccounts");

                if (statementOfAccountsArray != null && statementOfAccountsArray.length() > 0) {
                    JSONObject firstItem = statementOfAccountsArray.optJSONObject(0);

                    if (firstItem != null) {
                        String url = firstItem.optString("FilePath");

                        if (!TextUtils.isEmpty(url)) {
                            url = getBaseUrl() + "/" + url;

//                            System.out.println("url : " + url);

                            String fileName = firstItem.optString("FileName");

                            Values values = new Values();
                            values.url = url;
                            values.fileName = fileName;

                            return values;
                        }
                    }
                }
            } catch (JSONException ignored) {
                Log.e("excepion","1"+ignored);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Values value) {
            super.onPostExecute(value);
            Log.e("excepion","2"+value);
            if (value!=null) {
                if (value.checkError != 850) {
                    if (IScoreApplication.checkPermissionIemi(value.checkError, SearchResultActivity.this)) {
                        downloadFile(value.url, value.fileName);
                    }
                } else {
                    DialogUtil.showAlert(SearchResultActivity.this,
                            IScoreApplication.MSG_EXCEPTION_NETWORK);
                    pPDFDialog.dismiss();
                    Toast.makeText(SearchResultActivity.this, "two", Toast.LENGTH_SHORT).show();

                }
            }else {
                DialogUtil.showAlert(SearchResultActivity.this,
                        IScoreApplication.MSG_EXCEPTION_NETWORK);
                pPDFDialog.dismiss();
                Toast.makeText(SearchResultActivity.this, "one", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Listener implements DownloadListener {
       /* private long mStartTimestamp = 0;
        private final long mStartSize = 0;*/

        @Override
        public void onStart(int downloadId, long totalBytes) {

           /* mStartTimestamp = System.currentTimeMillis();*/
        }

        @Override
        public void onRetry(int downloadId) {
//            Log.d(TAG, "downloadId: " + downloadId);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
            int progress = (int) (bytesWritten * 100f / totalBytes);
            progress = progress == 100 ? 0 : progress;
//            long currentTimestamp = System.currentTimeMillis();

            pPDFDialog.setProgress(progress);
//            Log.d(TAG, "progress: " + progress);
//
//            mTextStatus.setText(
//                    "In progress - Total bytes : " + totalBytes + " downloaded bytes : " + bytesWritten);

//            int speed;
//            int deltaTime = (int) (currentTimestamp - mStartTimestamp + 1);
//            speed = (int) ((bytesWritten - mStartSize) * 1000 / deltaTime) / 1024;

//            switch (downloadId) {
//                case DOWNLOAD_ID0:
//                    mProgressBar.setProgress(progress);
//                    mTextSpeed.setText(speed + "kb/s");
//                    break;
//
//                //			case DOWNLOAD_ID1:
//                //				mProgressBar1.setProgress(progress);
//                //				mTextSpeed1.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID2:
//                //				mProgressBar2.setProgress(progress);
//                //				mTextSpeed2.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID3:
//                //				mProgressBar3.setProgress(progress);
//                //				mTextSpeed3.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID4:
//                //				mProgressBar4.setProgress(progress);
//                //				mTextSpeed4.setText(speed + "kb/s");
//                //				break;
//
//                default:
//                    break;
//            }
        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
            pPDFDialog.dismiss();
            Toast.makeText(SearchResultActivity.this, filePath, Toast.LENGTH_SHORT).show();
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                try{
                    final Uri data = FileProvider.getUriForFile(
                            SearchResultActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( filePath )
                    );
                    SearchResultActivity.this.grantUriPermission( SearchResultActivity.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(data, "application/pdf")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }catch ( Exception e ){
                    if ( IScoreApplication.DEBUG ){
                        Log.e("error", e.toString() );
                    }
                }
            }else {
                File file = new File(filePath);
                Intent target = new Intent( Intent.ACTION_VIEW );
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    DialogUtil.showAlert(SearchResultActivity.this,
                            "Please install App to open PDF file");
                }
            }

//            mTextStatus.setText("Success : " + filePath);
//            Log.d(TAG, "success: " + downloadId + " size: " + new File(filePath).length());
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
            pPDFDialog.dismiss();

            DialogUtil.showAlert(SearchResultActivity.this,
                    "Not able to download PDF file, Please try again later");

//            mTextStatus.setText("onFailure : " +  statusCode + " errMsg " + errMsg);
//            Log.d(TAG, "fail: " + downloadId + " " + statusCode + " " + errMsg);
        }
    }

    public String getBaseUrl() {
        String tempBaserUrl = PreferenceUtil.getInstance().getStringValue(BASE_URL_KEY, "");
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if(TextUtils.isEmpty(tempBaserUrl.trim())) {
            return BASE_URL;
        }
        return tempBaserUrl;
    }





}
