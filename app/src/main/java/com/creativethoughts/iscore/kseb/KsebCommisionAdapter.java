package com.creativethoughts.iscore.kseb;

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

public class KsebCommisionAdapter extends RecyclerView.Adapter<KsebCommisionAdapter.MyViewHolder>{

    String TAG = "KsebCommisionAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;

    public KsebCommisionAdapter(Context context, JSONArray arrayHeader) {
        this.context=context;
        this.jsonArray=arrayHeader;
    }

    @Override
    public KsebCommisionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_ksebcommision, parent, false);

        return new KsebCommisionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KsebCommisionAdapter.MyViewHolder holder, int position) {

        try {
            jsonObject = jsonArray.getJSONObject(position);
            holder.tva_range.setText(""+jsonObject.getString("AmountFrom")+" - "+jsonObject.getString("AmountTo"));
            holder.tva_commision.setText(""+jsonObject.getString("CommissionAmount"));

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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tva_range,tva_commision;

        //  ImageView img_operator;

        public MyViewHolder(View v) {
            super(v);
            tva_commision=v.findViewById(R.id.tva_commision);
            tva_range=v.findViewById(R.id.tva_range);

        }
    }

}
