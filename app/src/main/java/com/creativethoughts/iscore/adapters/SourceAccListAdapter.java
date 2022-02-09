package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.OtherAccountFundTransferActivity;
import com.creativethoughts.iscore.OwnAccountFundTransferActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SourceAccListAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    int typeAcc;

    public SourceAccListAdapter(Context context, JSONArray jsonArray,int typeAcc) {
        this.context=context;
        this.jsonArray=jsonArray;
        this.typeAcc=typeAcc;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_source_acc_list, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            ((MainViewHolder)holder).tv_acc_no.setText(jsonObject.getString("AccountNumber"));
            ((MainViewHolder)holder).tv_branch_name.setText(jsonObject.getString("BranchName"));

            double num = Double.parseDouble(jsonObject.getString("Balance"));
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");
            System.out.println(fmt.format(num));
            ((MainViewHolder)holder).tv_balance.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));

            ((MainViewHolder)holder).ll_accounts.setTag(position);
            ((MainViewHolder)holder).ll_accounts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        jsonObject = jsonArray.getJSONObject(position);
                        String AccountNumber = jsonObject.getString("AccountNumber");
                        String BranchName = jsonObject.getString("BranchName");
                        String Balance = jsonObject.getString("Balance");
                        String typeShort = jsonObject.getString("typeShort");
                        String FK_Account =jsonObject.getString("FK_Account");
                        String SubModule =jsonObject.getString("SubModule");
                        if (typeAcc == 0){
                            Intent i = new Intent(context, OwnAccountFundTransferActivity.class);
                            i.putExtra("AccountNumber", AccountNumber);
                            i.putExtra("BranchName", BranchName);
                            i.putExtra("Balance", Balance);
                            i.putExtra("typeShort", typeShort);
                            i.putExtra("SubModule", SubModule);
                            i.putExtra("FK_Account", FK_Account);
                            context.startActivity(i);
                        }
                        else {
                            Intent i = new Intent(context, OtherAccountFundTransferActivity.class);
                            i.putExtra("AccountNumber", AccountNumber);
                            i.putExtra("BranchName", BranchName);
                            i.putExtra("Balance", Balance);
                            i.putExtra("typeShort", typeShort);
                            i.putExtra("SubModule", SubModule);
                            i.putExtra("FK_Account", FK_Account);
                            context.startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView tv_acc_no,tv_balance,tv_branch_name;
        LinearLayout ll_accounts;
        public MainViewHolder(View v) {
            super(v);
            tv_balance=v.findViewById(R.id.tv_balance);
            tv_acc_no=v.findViewById(R.id.tv_acc_no);
            tv_branch_name=v.findViewById(R.id.tv_branch_name);
            ll_accounts=v.findViewById(R.id.ll_accounts);
           // imSeperator=v.findViewById(R.id.imSeperator);
        }
    }

}
