package com.creativethoughts.iscore.Recharge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder>{

    String TAG = "CircleAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    OnItemClickListener onItemClickListener;

    public AccountAdapter(Context context, JSONArray arrayHeader) {
        this.context=context;
        this.jsonArray=arrayHeader;
    }

    @Override
    public AccountAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circle_list, parent, false);

        return new AccountAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            jsonObject = jsonArray.getJSONObject(position);
            holder.tva_circle.setText(""+jsonObject.getString("AccountNumber"));
//            SharedPreferences pref =context.getSharedPreferences(Config.SHARED_PREF7, 0);
//            String BASE_URL=pref.getString("baseurl", null);
//            String ProvidersImagePath = BASE_URL+ jsonObject.getString("ProvidersImagePath");
//            Log.e(TAG,"ProvidersImagePath   96      "+ProvidersImagePath)  ;
//            PicassoTrustAll.getInstance(context).load(ProvidersImagePath).error(R.drawable.no_image).into(holder.img_operator);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tva_circle;
        LinearLayout ll_circle;
        //  ImageView img_operator;

        public MyViewHolder(View v) {
            super(v);
            tva_circle=v.findViewById(R.id.tva_circle);
            ll_circle=v.findViewById(R.id.ll_circle);
            //  img_operator=v.findViewById(R.id.img_operator);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            try {
                if (onItemClickListener != null) onItemClickListener.onItemClick(view,getAdapterPosition(),"account","0");

            }catch (Exception e){
                Log.e(TAG,"Exception   96      "+e.toString())  ;
            }
        }
    }

}
