package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.model.Share;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShareListInfoAdapter extends RecyclerView.Adapter<ShareListInfoAdapter.MyViewHolder> {

    private List<Share> mModelList;
    Context context;
    JSONArray Jarray;
    JSONObject jsonObject=null;


    public ShareListInfoAdapter(Context context, List<Share> modelList, JSONArray Jarray) {
        mModelList = modelList;
        context = context;
        this.Jarray = Jarray;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_share, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {
            jsonObject= Jarray.getJSONObject(position);
            if (jsonObject.getString("IsShareAc").equals("1")){
                final Share model = mModelList.get(position);
                holder.txtvacctno.setText("A/C NUMBER : "+model.getAccountNumber());
                holder.txtvaccno.setText("FUND TRANSFER A/C : "+model.getFundTransferAccount());
                holder.txtv_branchname.setText("BRANCH NAME : "+model.getBranch());
                holder.txtv_name.setText(model.getName());
                // holder.txtv_fund.setText(model.getFundTransferAccount());
                holder.txtv_ifsc.setText("IFSC CODE : "+model.getIFSCCode());
                //  holder.view.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
                holder.chkbx1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.setSelected(!model.isSelected());
//                holder.view.setBackgroundColor(model.isSelected() ? Color.LTGRAY : Color.WHITE);
                    }
                });
            }
            else{
                holder.cv_share.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtvaccno,txtv_name,txtv_branchname,txtv_fund,txtv_ifsc,txtvacctno;
        private CheckBox chkbx1;
        private LinearLayout l1;
        private CardView cv_share;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cv_share = itemView.findViewById(R.id.cv_share);
            txtvaccno = (TextView) itemView.findViewById(R.id.txtv_accno);
            txtv_name = (TextView) itemView.findViewById(R.id.txtv_name);
            txtvacctno = (TextView) itemView.findViewById(R.id.txtvacctno);
            txtv_branchname = (TextView) itemView.findViewById(R.id.txtv_branchname);
            // txtv_fund = (TextView) itemView.findViewById(R.id.txtv_fund);
            txtv_ifsc = (TextView) itemView.findViewById(R.id.txtv_ifsc);
            chkbx1 = (CheckBox) itemView.findViewById(R.id.chkbx);
            l1 = (LinearLayout) itemView.findViewById(R.id.l2);
        }
    }
}




//package com.creativethoughts.iscore.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.creativethoughts.iscore.R;
//import com.creativethoughts.iscore.db.dao.model.Share;
//import java.util.List;
//
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class ShareListInfoAdapter extends RecyclerView.Adapter<ShareListInfoAdapter.MyViewHolder> {
//
//    private List<Share> mModelList;
//    Context context;
//    JSONArray Jarray;
//    JSONObject jsonObject=null;
//
//
//    public ShareListInfoAdapter(Context context, List<Share> modelList, JSONArray Jarray) {
//        mModelList = modelList;
//        context = context;
//        this.Jarray = Jarray;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_share, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final MyViewHolder holder, int position) {
//        try {
//            jsonObject= Jarray.getJSONObject(position);
//            if (jsonObject.getString("IsShareAc").equals("1")){
//                final Share model = mModelList.get(position);
//                //holder.txtvaccno.setText(model.getAccountNumber());
//                holder.txtvaccno.setText("FUND TRANSFER A/C : "+model.getFundTransferAccount());
//                //holder.txtv_branchname.setText(model.getBranch());
//                holder.txtv_name.setText(model.getName());
//                // holder.txtv_fund.setText(model.getFundTransferAccount());
//                holder.txtv_ifsc.setText("IFSC CODE : "+model.getIFSCCode());
//                //  holder.view.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
//                holder.chkbx1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        model.setSelected(!model.isSelected());
////                holder.view.setBackgroundColor(model.isSelected() ? Color.LTGRAY : Color.WHITE);
//                    }
//                });
//            }
//            else{
//                holder.cv_share.setVisibility(View.GONE);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mModelList == null ? 0 : mModelList.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        private View view;
//        private TextView txtvaccno,txtv_name,txtv_branchname,txtv_fund,txtv_ifsc;
//        private CheckBox chkbx1;
//        private LinearLayout l1;
//        private CardView cv_share;
//
//        private MyViewHolder(View itemView) {
//            super(itemView);
//            view = itemView;
//            cv_share = itemView.findViewById(R.id.cv_share);
//            txtvaccno = (TextView) itemView.findViewById(R.id.txtv_accno);
//            txtv_name = (TextView) itemView.findViewById(R.id.txtv_name);
//            // txtv_branchname = (TextView) itemView.findViewById(R.id.txtv_branchname);
//            // txtv_fund = (TextView) itemView.findViewById(R.id.txtv_fund);
//            txtv_ifsc = (TextView) itemView.findViewById(R.id.txtv_ifsc);
//            chkbx1 = (CheckBox) itemView.findViewById(R.id.chkbx);
//            l1 = (LinearLayout) itemView.findViewById(R.id.l2);
//        }
//    }
//}
//
