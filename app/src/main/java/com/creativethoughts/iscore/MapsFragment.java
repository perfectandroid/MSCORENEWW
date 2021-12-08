package com.creativethoughts.iscore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.creativethoughts.iscore.Helper.Common;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    ProgressDialog progressDialog;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String bank;
    String id;
    AlertDialog alert;
    int clickcount=0;
    RelativeLayout mRoot;
    SupportMapFragment mapFragment;
    String strlatitude, strlongitude;
    public MapsFragment() {
        // Required empty public constructor
    }
    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    static final LatLng CHALAPPURAM = new LatLng(11.2406, 75.7909);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_maps, null, false);

        mRoot = (RelativeLayout) v.findViewById(R.id.mainrl);


        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
       map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
        if (NetworkUtil.isOnline()) {

            progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
            progressDialog.show();


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
                String reqmode = IScoreApplication.encryptStart("1");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("ReqMode", reqmode);
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
                Call<String> call = apiService.getBankLocation(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        Log.i("Locationdetails", response.body());
                        progressDialog.dismiss();
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("BranchLocationDetailsListInfo");
                                Log.i("First ", String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ", String.valueOf(object));
                                JSONArray Jarray = object.getJSONArray("BranchLocationDetails");

                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject jObj = Jarray.getJSONObject(i);

                                    if (!jObj.getString("LocationLatitude").equals("") &&
                                            !jObj.getString("LocationLongitude").equals(""))
                                    {
                                        double latitude = Double.parseDouble(jObj.getString("LocationLatitude"));
                                        double longitude = Double.parseDouble(jObj.getString("LocationLongitude"));
                                        strlatitude=jObj.getString("LocationLatitude");
                                        strlongitude=jObj.getString("LocationLongitude");
                                        id = jObj.getString("ID_Branch");
                                        bank = jObj.getString("BankName");
                                        //  Toast.makeText(getApplicationContext(), latitude+"\n"+longitude, Toast.LENGTH_LONG).show();
                                        Log.i("LocationDetails", latitude + "" + longitude);
                                        MarkerOptions marker = new MarkerOptions().position(
                                                new LatLng(latitude, longitude)).title(id + ") " + bank);
                                        try {
                                            // marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bankicon));
                                            googleMap.addMarker(marker);
                                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                                    .target(new LatLng(11.2406, 75.7909)).zoom(0).build();
                                            googleMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(cameraPosition));
                                            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                @Override
                                                public void onInfoWindowClick(Marker marker1) {
                                                    String title = marker1.getTitle();
                                                    String[] namesList = title.split("\\)");
                                                    String id = namesList[0];
                                                    String bank = namesList[1];
                                                    //  Toast.makeText(getActivity(), "Marker Clicked"+"\n"+title, Toast.LENGTH_SHORT).show();
                                                    showBranchDetails(id);
                                                    // return false;
                                                }
                                            });
                                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker1) {
                                                    String title = marker1.getTitle();
                                                    String[] namesList = title.split("\\)");
                                                    String id = namesList[0];
                                                    String bank = namesList[1];
                                                    // Toast.makeText(MapsActivity.this, "Marker Clicked"+"\n"+title, Toast.LENGTH_SHORT).show();
                                                    showBranchDetails(id);
                                                    return false;
                                                }
                                            });
                                        } catch (Exception e) {
                                        }
                                    }
                                    else {
                                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker1) {
                                                String title = marker1.getTitle();
                                                String[] namesList = title.split("\\)");
                                                String id = namesList[0];
                                                String bank = namesList[1];
                                                //  Toast.makeText(getActivity(), "Marker Clicked"+"\n"+title, Toast.LENGTH_SHORT).show();
                                                showBranchDetails(id);
                                                return false;
                                            }
                                        });
                                    }

                                }
                                if ((strlatitude != null && !strlatitude.isEmpty() && !strlatitude.equals("null"))&&
                                        (strlongitude != null && !strlongitude.isEmpty() && !strlongitude.equals("null"))){
                                }else{
                                    showNoLocation();
                                }
                            }
                            else{
                                try{
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("BranchLocationDetailsListInfo");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
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
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {

            }
        }
        else {
            DialogUtil.showAlert(getActivity(),
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private void showNoLocation() {
        Snackbar snackbar = Snackbar
                .make(mRoot, "No branches marked on Map!", 80000)
                .setAction("Branch List", (View.OnClickListener) view -> ((BankActivity)getActivity()).selectTab(1));

        snackbar.show();
    }

    private void cancelPopup(AlertDialog dialog) {

        dialog.dismiss();

    }

   /* private void loadFragment(BankBranchFragment bankBranchFragment) {

        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.content_frame, bankBranchFragment);
        fragmentTransaction.commit(); // save the changes
    }*/

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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        mCurrLocationMarker = map.addMarker(markerOptions);

        //move map camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

      //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(CHALAPPURAM, 15));

        // Zoom in, animating the camera.
       // map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    public void  showBranchDetails(String branchID){
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
        progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
        progressDialog.show();


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
            String reqmode = IScoreApplication.encryptStart("1");
            String id2 = IScoreApplication.encryptStart(branchID);
            //Toast.makeText(getApplicationContext(),"id2"+"\n"+branchID,Toast.LENGTH_LONG).show();
            final JSONObject requestObject1 = new JSONObject();
            try {

                requestObject1.put("ReqMode",reqmode);
                requestObject1.put("ID_Branch",id2);
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
            Call<String> call = apiService.getBranchDetail(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.i("Branchdetails",response.body());

                    //   Toast.makeText(getApplicationContext(),"BranchDetails"+"\n"+response.body(),Toast.LENGTH_LONG).show();
                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObj = new JSONObject(response.body());
                        if(jsonObj.getString("StatusCode").equals("0")) {

                            JSONObject jobjt = jsonObj.getJSONObject("BankBranchDetailsListInfo");
                            String branch = jobjt.getString("BranchName");
                            String bankname = jobjt.getString("BankName");
                            String address = jobjt.getString("Address");
                            String place = jobjt.getString("Place");
                            String post = jobjt.getString("Post");
                            String district = jobjt.getString("District")+"\nPhone : "+
                                    jobjt.getString("LandPhoneNumber")+", "+jobjt.getString("BranchMobileNumber");
                            String incharge = jobjt.getString("InchargeContactPerson");
                            String phone = jobjt.getString("ContactPersonMobile");
                            // Toast.makeText(getApplicationContext(),branch+"\n"+bankname+"\n"+
                            // address+"\n"+phone,
                            //  Toast.LENGTH_LONG).show();
                            String wrkinghrs="Working Hours : "+jobjt.getString("OpeningTime")+" - "+jobjt.getString("ClosingTime");
                            showCustomDialog(branch,bankname,address,place,post,district,incharge,phone, wrkinghrs);
                        }
                        else {
                            try{
                                JSONObject jobj = jsonObj.getJSONObject("BankBranchDetailsListInfo");
                                String ResponseMessage = jobj.getString("ResponseMessage");
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }
                            catch (JSONException e){
                                String EXMessage = jsonObj.getString("EXMessage");
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception e)
        {

        }
        } else {
            DialogUtil.showAlert(getActivity(),
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private void showCustomDialog(String branch,String bankname,String address,String place,String post,String district,String incharge,String phone, String Wrkghrs) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertDialogView = inflater.inflate(R.layout.layout_branchdetail, null);




      //  ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
       // View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_branchdetail, viewGroup, false);

        TextView txtv_bankname;
        TextView txtv_bankaddr;
        TextView txtv_bankhrs;
        TextView txtv_bankmob;

        txtv_bankname = (TextView)alertDialogView.findViewById(R.id.txtv_bankname);
        txtv_bankaddr = (TextView)alertDialogView.findViewById(R.id.txtv_bankaddr);
        txtv_bankmob = (TextView)alertDialogView.findViewById(R.id.txtv_bankmob);
        txtv_bankhrs = (TextView)alertDialogView.findViewById(R.id.txtv_bankhrs);
        Log.i("branch",bankname+address+phone+place+post+district+incharge);

        txtv_bankname.setText(bankname);
        String addr = branch+","+" "+address+","+" "+place+","+" "+post+","+district;
        if(addr!=null) {
            txtv_bankaddr.setText(addr);
        }
        else
        {
            //txtv_bankaddr.setText("No value");
        }
        txtv_bankmob.setText("Contact Person: \n"+incharge+"\nPhone no: "+phone);
        //txtv_bankmob.setText(phone+" "+"("+incharge+")");
        txtv_bankhrs.setText(Wrkghrs);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting the view of the builder to our custom view that we already inflated
      //  builder.setView(dialogView);
        alertDialog.setView(alertDialogView);
        //finally creating the alert dialog and displaying it
      //  AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
