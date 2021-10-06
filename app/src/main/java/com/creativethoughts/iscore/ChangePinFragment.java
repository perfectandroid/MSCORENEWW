package com.creativethoughts.iscore;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;

import com.creativethoughts.iscore.utility.ProgressBarUtil;

import static com.creativethoughts.iscore.IScoreApplication.FLAG_NETWORK_EXCEPTION;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePinFragment extends Fragment implements View.OnClickListener {

    private EditText edtOldPin;
    private EditText edtConfrmPin;
    private EditText edtNewPin;
    private Button btnSubmit;


    public ChangePinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePinFragment.
     */
    public static ChangePinFragment newInstance() {
        ChangePinFragment fragment = new ChangePinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_change_pin, container, false);

        edtConfrmPin = rootView.findViewById(R.id.edtCnfrmPin);
        edtOldPin = rootView.findViewById(R.id.edtOldPin);

        edtNewPin = rootView.findViewById(R.id.edtNewPin);

        btnSubmit = rootView.findViewById(R.id.cmdSubmit);

        edtOldPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (s.length() >= 6) {
                        UserCredential userCredential =
                                UserCredentialDAO.getInstance().getLoginCredential();

                        if (!s.toString().trim()
                                .equalsIgnoreCase(userCredential.pin)) {
                            edtOldPin.setError("Please enter your valid Pin No");
                        } else {
                            edtOldPin.setError(null);
                        }
                    } else {
                        edtOldPin.setError(null);
                    }
                } else {
                    edtOldPin.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtNewPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    String oldPin = edtOldPin.getText().toString();

                    if (oldPin.length() == 0) {
                        edtOldPin.setError("Please enter your Pin No.");
                    } else if (oldPin.length() < 6) {
                        edtOldPin.setError("Please enter your valid Pin No");
                    } else {

                        if (oldPin.length() >= 6) {
                            UserCredential userCredential =
                                    UserCredentialDAO.getInstance().getLoginCredential();

                            if (!oldPin.trim()
                                    .equalsIgnoreCase(userCredential.pin)) {
                                edtOldPin.setError("Please enter your valid Pin No");
                            } else {
                                edtOldPin.setError(null);
                            }
                        } else {
                            edtOldPin.setError(null);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtConfrmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    String newPin = edtNewPin.getText().toString();

                    if (newPin.length() == 0) {
                        edtNewPin.setError("Please enter new Pin No.");
                    } else if (newPin.length() < 6) {
                        edtNewPin.setError("Please enter a valid new Pin No.");
                    } else {
                        edtNewPin.setError(null);
                    }

                    if (s.length() == newPin.length()) {

                        if (!s.toString().trim().equalsIgnoreCase(newPin)) {
                            edtConfrmPin
                                    .setError("New Pin No and Confirm Pin No does not match.");
                        } else {
                            edtConfrmPin.setError(null);
                        }
                    } else {
                        edtConfrmPin.setError(null);
                    }
                } else {
                    edtConfrmPin.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSubmit.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {


        if (v == btnSubmit) {

            String strOldPin, strConfrmPin, strNewPin;

            strOldPin = edtOldPin.getText().toString();
            strConfrmPin = edtConfrmPin.getText().toString();
            strNewPin = edtNewPin.getText().toString();

            UserCredential userCredential = UserCredentialDAO.getInstance().getLoginCredential();


            if (strOldPin.length() == 0) {
                edtOldPin.setError("Please enter your Pin No.");

                return;
            } else if ((!strOldPin.trim()
                    .equalsIgnoreCase(userCredential.pin)) || (strOldPin.length() < 4)) {
                edtOldPin.setError("Please enter your valid Pin No");

                return;
            } else if (strNewPin.length() == 0) {
                edtNewPin.setError("Please enter new Pin No.");

                return;
            } else if (strNewPin.length() < 6) {
                edtNewPin.setError("Please enter a valid new Pin No.");

                return;
            } else if (strConfrmPin.length() == 0) {
                edtConfrmPin.setError("Please enter confirm Pin No.");

                return;
            } else if (!strNewPin.equalsIgnoreCase(strConfrmPin)) {
                edtConfrmPin.setError("New Pin No and Confirm Pin No does not match.");

                return;
            }

            ChangePinAsyncTask changePinAsyncTask = new ChangePinAsyncTask(strOldPin, strNewPin);

            changePinAsyncTask.execute();
        }
    }

    private class ChangePinAsyncTask extends AsyncTask<String, android.R.integer, Integer> {

        private final String mOldPin;
        private final String mNewPin;

        private ChangePinAsyncTask(String oldPin, String newPin) {
            mOldPin = oldPin;
            mNewPin = newPin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBarUtil.showProgressDialog(getActivity());
        }

        @Override
        protected Integer doInBackground(String... params) {
            UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

            String custId = user.customerId;

            String url;
            try {
                url = CommonUtilities.getUrl() + "/ChangeMpin?IDCustomer=" +
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                "&oldPin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mOldPin)) +
                "&newPin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mNewPin));
            } catch (Exception e) {
//                e.printStackTrace();
                url=   CommonUtilities.getUrl() + "/ChangeMpin";
            }

            try {
                String text1 = ConnectionUtil.getResponse(url);

                if(IScoreApplication.containAnyKnownException(text1)){
                    return  IScoreApplication.getFlagException(text1);
                }
              else   if (!TextUtils.isEmpty(text1)) {
                    if (text1.trim().equalsIgnoreCase("true")) {
                        UserCredentialDAO.getInstance().updateNewPin(mNewPin);

                        return 1;
                    } else if (text1.trim().equalsIgnoreCase("false")) {
                        return 0;
                    } else {
                        return 0;
                    }
                }else{
                    return IScoreApplication.FLAG_NETWORK_EXCEPTION;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            ProgressBarUtil.dismissProgressDialog();

            if (IScoreApplication.checkPermissionIemi(result,getActivity())) {
                if (result==FLAG_NETWORK_EXCEPTION){
                    Toast.makeText(getActivity(), IScoreApplication.MSG_EXCEPTION_NETWORK,
                            Toast.LENGTH_SHORT).show();
                }
                else if (result == 1) {
                    Toast.makeText(getActivity(), "Pin No changed successfully, Please login again",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), PinLoginActivity.class);

                    getActivity().startActivity(intent);

                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Not able to change Pin No, Please try later",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
