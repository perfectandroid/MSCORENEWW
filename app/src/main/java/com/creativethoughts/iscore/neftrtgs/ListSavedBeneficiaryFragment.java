package com.creativethoughts.iscore.neftrtgs;


import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.OwnAccountFundTransferActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListSavedBeneficiaryFragment extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerBeneficiary;
    private BeneficiaryRecyclerAdapter mBeneficiaryRecyclerAdapter;
    private ProgressBar mProgressBarLoading;
    private LinearLayout mLnrAnimationContainer;
    private LinearLayout mLnrOops;
    private TextView mTxtOops;
    private static final String MODE = "MODE";
    private String mMode;
    public ListSavedBeneficiaryFragment() {
        // Required empty public constructor
    }

    public static ListSavedBeneficiaryFragment newInstance( String mode ){
        ListSavedBeneficiaryFragment listSavedBeneficiaryFragment = new ListSavedBeneficiaryFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MODE, mode);
        listSavedBeneficiaryFragment.setArguments( bundle );
        return listSavedBeneficiaryFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_saved_beneficiary, container, false);
        mRecyclerBeneficiary = view.findViewById( R.id.recycler_view_beneficiary );
        view.findViewById( R.id.img_refresh ).setOnClickListener( this );
        view.findViewById( R.id.lnr_add_new_beneficiary).setOnClickListener( this );
        mProgressBarLoading = view.findViewById( R.id.progressBar );
        mLnrAnimationContainer = view.findViewById( R.id.lnr_anim_container );
        mLnrOops    = view.findViewById( R.id.lnr_oops );
        mTxtOops    = view.findViewById( R.id.txt_oops_message );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
        mRecyclerBeneficiary.setLayoutManager( layoutManager );
        try{
            Bundle bundle = getArguments();
            assert bundle != null;
            mMode = bundle.getString( MODE );
        }catch ( Exception e ){
            //Do nothing
        }
        fetchBeneficiary();

        return view;
    }
    private void hideAnim( boolean hide ){
        if ( Build.VERSION.SDK_INT > 18 ){
            TransitionManager.beginDelayedTransition( mLnrAnimationContainer );
        }
        if ( hide )
            mProgressBarLoading.setVisibility( View.GONE );
        else mProgressBarLoading.setVisibility( View.VISIBLE );
    }
    private void fetchBeneficiary(){
        try{
            UserDetails user = UserDetailsDAO.getInstance( ).getUserDetail( );
            String url = CommonUtilities.getUrl( );
            url += "/NEFTRTGSGetReceiver?ID_Customer="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart( user.customerId  ) );
            hideAnim( false );
            NetworkManager.getInstance().connector(url, new ResponseManager() {
                @Override
                public void onSuccess(String result) {
                    hideAnim( true );
                    showBeneficiaryList( result );
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext( ), "Something went wrong", Toast.LENGTH_LONG ).show( );
                    hideAnim( true );
                }
            }, null, "Talking to server....");
        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG )
                Log.e("Exce", e.toString() );
        }
    }
    private void showBeneficiaryList(String result ){
        String noBen = "No beneficiary found in your account";
        try{
            Gson gson = new Gson();
            BeneficiaryDetailsResponseModel beneficiaryDetailsResponseModel = gson.fromJson( result, BeneficiaryDetailsResponseModel.class );
            ArrayList< BeneficiaryDetailsModel > beneficiaryDetailsModelList = beneficiaryDetailsResponseModel.getBeneficiaryDetailsModelList();
            if ( beneficiaryDetailsModelList.isEmpty() ){
                mRecyclerBeneficiary.setVisibility( View.GONE );
                mLnrOops.setVisibility( View.VISIBLE );
                mTxtOops.setText( noBen );
                return;

            }
            mRecyclerBeneficiary.setVisibility( View.VISIBLE );
            mLnrOops.setVisibility( View.GONE );
             mBeneficiaryRecyclerAdapter = new BeneficiaryRecyclerAdapter(getContext(), beneficiaryDetailsModelList, new BeneficiaryRecyclerAdapter.BeneRecyclerClicker() {
                @Override
                public void onItemSelect(BeneficiaryDetailsModel beneficiaryDetailsModel) {
                    changeFragment( beneficiaryDetailsModel);
                }

                @Override
                public void onItemDelete(BeneficiaryDetailsModel beneficiaryDetailsModel, int index) {

                    beneficiaryDeleteAlert("Alert","Do you want to delete the beneficiary details?",beneficiaryDetailsModel, index ,beneficiaryDetailsModelList );
//                    new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE )
//                            .setTitleText("Alert" )
//                            .setContentText("Do you want to delete the beneficiary details?" )
//                            .setCancelText("No" )
//                            .setConfirmText("Yes" )
//                            .showCancelButton(true )
//                            .setCustomImage( R.mipmap.save )
//                            .setCancelClickListener(SweetAlertDialog::cancel )
//                            .setConfirmClickListener(sDialog -> {
//                                sDialog.dismissWithAnimation( );
//                                deleteBeneficiary(  beneficiaryDetailsModel, index ,beneficiaryDetailsModelList );
//                            } )
//                            .show( );
                }
            });
            mRecyclerBeneficiary.setAdapter(mBeneficiaryRecyclerAdapter);
        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG )
                Log.e("exc", e.toString() );
        }
    }

    private void beneficiaryDeleteAlert(String msg1, String msg2, BeneficiaryDetailsModel beneficiaryDetailsModel, int index,
                               ArrayList< BeneficiaryDetailsModel > beneficiaryDetailsModelList) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_delet_layout, null);
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

                deleteBeneficiary(  beneficiaryDetailsModel, index ,beneficiaryDetailsModelList );
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeFragment( BeneficiaryDetailsModel beneficiaryDetailsModel ){
        NeftRtgsFragment neftRtgsFragment;
        if ( beneficiaryDetailsModel != null ){
            neftRtgsFragment = NeftRtgsFragment.newInstance( beneficiaryDetailsModel, mMode );
        }else {
            neftRtgsFragment = NeftRtgsFragment.newInstance( mMode );
        }
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add( R.id.container, neftRtgsFragment );
        fragmentTransaction.addToBackStack("sample");
        fragmentTransaction.commit();
    }
    private void deleteBeneficiary( BeneficiaryDetailsModel beneficiaryDetailsModel, int index,
                                    ArrayList< BeneficiaryDetailsModel > beneficiaryDetailsModelList){
        String baseUrl = CommonUtilities.getUrl();
        try{
            baseUrl += "/NEFTRTGSDeleteReceiver?BeneName="+IScoreApplication.encodedUrl( beneficiaryDetailsModel.getBeneficiaryName( )  )+
                    "&BeneIFSC="+IScoreApplication.encodedUrl( beneficiaryDetailsModel.getBeneficiaryIfsc( )  )+
                    "&BeneAccNo="+ IScoreApplication.encodedUrl( beneficiaryDetailsModel.getBeneficiaryAccNo( )  );
            NetworkManager.getInstance().connector(baseUrl, new ResponseManager() {
                @Override
                public void onSuccess(String response) {
                    try {
                        response = response.trim();
                        int resultInt = Integer.parseInt(response);
                        if (resultInt > 0) {
                            beneficiaryDetailsModelList.remove( index );
                            mBeneficiaryRecyclerAdapter.refreshView( beneficiaryDetailsModelList );
                            if ( beneficiaryDetailsModelList.isEmpty() ){
                                mLnrOops.setVisibility( View.VISIBLE );
                                mRecyclerBeneficiary.setVisibility( View.GONE );
                            }
                            Toast.makeText(getContext(), "Beneficiary deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), " Can't delete the beneficiary ", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        if (IScoreApplication.DEBUG)
                            Log.d("Number exception", e.toString());
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Oops. Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }, getActivity(), "Please wait...");
        }catch ( Exception e ){
            //Do nothing
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if ( id == R.id.img_refresh ){
            fetchBeneficiary();
        }else if ( id == R.id.lnr_add_new_beneficiary ){
            changeFragment( null );
        }
    }
}
