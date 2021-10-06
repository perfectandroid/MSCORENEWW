package com.creativethoughts.iscore.custom_alert_dialogs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.List;

public class SuccessAdapter extends RecyclerView.Adapter<SuccessAdapter.SuccessViewHolder>{
    private List<KeyValuePair> mKeyValuePairs;
    public SuccessAdapter(List<KeyValuePair> keyValuePairs){

       mKeyValuePairs = keyValuePairs;

    }
    @NonNull
    @Override
    public SuccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from( parent.getContext()).
                inflate( R.layout.recycle_success_alert, parent, false);
        return new SuccessViewHolder( itemView );

    }

    @Override
    public void onBindViewHolder(@NonNull SuccessViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();


            KeyValuePair keyValuePair = mKeyValuePairs.get(pos);
            holder.txtValue.setText(keyValuePair.getValue());
            holder.txtKey.setText(keyValuePair.getKey());


    }

    @Override
    public int getItemCount() {
        return mKeyValuePairs.size();
    }

    public class SuccessViewHolder extends RecyclerView.ViewHolder{
        private TextView txtKey;
        private TextView txtValue;
        public SuccessViewHolder(View view){
            super(view);
            txtKey = view.findViewById( R.id.txt_key );
            txtValue = view.findViewById( R.id.txt_value );
        }
    }
}
