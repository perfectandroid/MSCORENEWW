package com.creativethoughts.iscore.passbook;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.NewTransactionDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class PassBookFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private Spinner mSpinnerAccNo;
    private String mSelectedAccountNo;
    public PassBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pass_book, container, false);

        mExpandableListView = view.findViewById( R.id.exp_list );
        mSpinnerAccNo = view.findViewById( R.id.spinner_acc );

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
        if ( settingsModel != null ){
            CommonUtilities.setAccountNumber( settingsModel.customerId, mSpinnerAccNo, getActivity() );
        }
        mSpinnerAccNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition( i );
                mSelectedAccountNo = (String) item;
                mSelectedAccountNo = mSelectedAccountNo.replace(mSelectedAccountNo.substring(mSelectedAccountNo.indexOf(" (")+1, mSelectedAccountNo.indexOf(")")+1), "");
                mSelectedAccountNo = mSelectedAccountNo.replace(" ","");
                initExpandableView( mSelectedAccountNo );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });
        return view;
    }
    private void initExpandableView( String accNo ){
        List<Transaction> transactionList = NewTransactionDAO.getInstance().getTransactions( accNo );
        if ( !transactionList.isEmpty() ){
            SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
            final int days;
            if ( settingsModel == null || settingsModel.days <= 0 ){
                days = 0;
            }else
                days = settingsModel.days;
        }else {
            Toast.makeText( getContext(), "No Transactions found...", Toast.LENGTH_SHORT ).show();
        }
    }
}
