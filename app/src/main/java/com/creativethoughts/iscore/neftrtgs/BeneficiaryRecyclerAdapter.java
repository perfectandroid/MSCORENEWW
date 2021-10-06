package com.creativethoughts.iscore.neftrtgs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import java.util.ArrayList;

/**
 * Created by vishnu on 12/8/2017 - 11:49 AM - 3:41 PM - 3:41 PM.
 */

public class BeneficiaryRecyclerAdapter  extends RecyclerView.Adapter<BeneficiaryRecyclerAdapter.MyBeneficiaryViewHolder>{
    private ArrayList<BeneficiaryDetailsModel> mBeneficiaryDeatilsList;
    private final BeneRecyclerClicker mBeneRecyclerClicker;
    public BeneficiaryRecyclerAdapter(Context context, ArrayList<BeneficiaryDetailsModel> beneficiaryDetailsList, BeneRecyclerClicker beneRecyclerClicker){
        this.mBeneficiaryDeatilsList = beneficiaryDetailsList;
        this.mBeneRecyclerClicker = beneRecyclerClicker;
    }

    @Override
    public MyBeneficiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from( parent.getContext()).
                inflate( R.layout.custom_list_beneficiary_details, parent, false);
        return new MyBeneficiaryViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(MyBeneficiaryViewHolder holder, int position ) {
        final int pos = holder.getAdapterPosition();
        final BeneficiaryDetailsModel beneficiaryDetailsModel = mBeneficiaryDeatilsList.get( pos );
        holder.mTxtName.setText( beneficiaryDetailsModel.getBeneficiaryName() );
        holder.mTxtAccNo.setText( beneficiaryDetailsModel.getBeneficiaryAccNo() );
        holder.mTxtIfsc.setText( beneficiaryDetailsModel.getBeneficiaryIfsc() );
        holder.mParentLinearLayout.setOnClickListener(view -> mBeneRecyclerClicker.onItemSelect( beneficiaryDetailsModel ));
        holder.mImgDelete.setOnClickListener(view -> mBeneRecyclerClicker.onItemDelete( beneficiaryDetailsModel, pos ));
    }

    @Override
    public int getItemCount(){
        return mBeneficiaryDeatilsList.size();
    }

    interface BeneRecyclerClicker{
        void onItemSelect(BeneficiaryDetailsModel beneficiaryDetailsModel);
        void onItemDelete(BeneficiaryDetailsModel beneficiaryDetailsModel, int index);
    }

    public class MyBeneficiaryViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTxtName;
        private final TextView mTxtAccNo;
        private final TextView mTxtIfsc ;
        private final ImageView mImgDelete;
        private final CardView mParentLinearLayout;
        public MyBeneficiaryViewHolder(View view){
            super(view);
            mTxtName = view.findViewById( R.id.txt_view_beneficiary_name );
            mTxtAccNo = view.findViewById( R.id.txt_view_beneficiary_acc_no );
            mTxtIfsc = view.findViewById( R.id.txt_view_beneficiary_ifsc );
            mParentLinearLayout = view.findViewById(R.id.linear_beneficiary_parent);
            mImgDelete = view.findViewById( R.id.img_delete );
        }
    }
    public void refreshView( ArrayList<BeneficiaryDetailsModel> beneficiaryDetailsList ){
        mBeneficiaryDeatilsList = beneficiaryDetailsList;
        notifyDataSetChanged();
    }
}
