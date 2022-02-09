package com.creativethoughts.iscore;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.utility.ConnectionUtil;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KsebBillStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KsebBillStatusFragment extends Fragment implements View.OnClickListener{

    private EditText editTransactionId;
    private Button btnCheckStatus;
    private TextView textViewTransactionIdErrorMsg;
    public KsebBillStatusFragment() {
        // Required empty public constructor
    }

    public static KsebBillStatusFragment newInstance() {

        return new KsebBillStatusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_kseb_bill_status, container, false);
        addView(rootView);
        return rootView;
    }
    private void addView(View rootView){
        editTransactionId = rootView.findViewById(R.id.edit_transaction_id);
        textViewTransactionIdErrorMsg = rootView.findViewById(R.id.transaction_id_error_message);
        btnCheckStatus = rootView.findViewById(R.id.check_status);
        btnCheckStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        if ( id == R.id.check_status )
            checkTransactionId();
    }
    private void checkTransactionId(){
        String transactionId = editTransactionId.getText().toString();
        //noinspection ObjectEqualsNull
        if(transactionId.equals("") ){
            setErrorHint(editTransactionId);
            return;
        }

        new CheckStatusAsync(transactionId).execute();
    }

    private void setErrorHint(EditText editText){
        if ( getContext() == null )
            return;
        editText.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(),
                R.color.error), PorterDuff.Mode.SRC_ATOP);
        String text = "Please enter valid transaction id";
        textViewTransactionIdErrorMsg.setText( text );
    }



    private class CheckStatusAsync extends AsyncTask<String, android.R.integer, String>{
        final String id;
        ProgressDialog progressDialog;
        private CheckStatusAsync(String transId){
            id = transId;
        }
        private String downloadStatus(String id){
            String url;
            String response;
            String pin;
            SharedPreferences pinIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF36, 0);
            pin = pinIdSP.getString("pinlog","");

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            url = BASE_URL+"/KSEBTransactionResponse?TransactioID="+id+
                    "&Pin="+pin;
            response = ConnectionUtil.getResponse(url);
            return response;

        }
        @Override
        public void onPreExecute(){
            btnCheckStatus.setEnabled(false);
            progressDialog = ProgressDialog.show(
                    getContext(), "",""
            );
        }
        @Override
        public String doInBackground(String... params){
            String result;
            result = downloadStatus(id);
            return result;
        }
        @Override
        public void onPostExecute(String result){
            btnCheckStatus.setEnabled(true);
            progressDialog.dismiss();
            processResponse(result);

        }
        private void processResponse(String response){
            try{
                int tempResponse = Integer.parseInt(response.trim());
                switch (tempResponse){
                    case 1:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment was successfull",1);
                        break;
                    case  2:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment was failed",2);
                        break;
                    case 3:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment is on pending",2);
                        break;
                    case 4:
                        showAlertDialogue(getString(R.string.app_name), "Wrong transaction Id", 2);
                        break;
                    case 5:
                        showAlertDialogue(getString(R.string.app_name), "Due to some technical issues, your transaction was reversed.\nThe amount will" +
                                " be reversed with in few hours",2);
                        break;
                    default:
                        break;
                }
            }catch (Exception e){
                if(IScoreApplication.DEBUG) Log.e("Response parse error", e.toString());
            }
        }
        private void showAlertDialogue(String title, String message, final int status){ // status for decide whether going to home fragment or not
            if ( getActivity() == null )
                return;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message).setTitle(title)
                    .setIcon(R.drawable.aappicon)
                    .setPositiveButton("Ok", (dialog, idTemp) -> {
                        if(status == 1){
                            Fragment fragment =   new KsebBillStatusFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            assert fragmentManager != null;
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, fragment);
                            fragmentTransaction.addToBackStack("Kseb");
                            fragmentTransaction.commit();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
