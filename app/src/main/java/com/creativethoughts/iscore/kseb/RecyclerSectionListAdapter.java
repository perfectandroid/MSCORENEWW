package com.creativethoughts.iscore.kseb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 1/20/2018 - 4:26 PM
 */

public class RecyclerSectionListAdapter extends RecyclerView.Adapter< RecyclerSectionListAdapter.SectionListViewHolder >{
    private final SectionSelect mSectionSelect;
    private List< SectionDetails > mSectionDetailsList;
    public RecyclerSectionListAdapter(List< SectionDetails > sectionDetails , SectionSelect sectionSelect ){
        mSectionDetailsList = sectionDetails;
        mSectionSelect = sectionSelect;
    }

    @Override
    public int getItemCount(){
        return mSectionDetailsList.size();
    }

    @Override
    public SectionListViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View itemView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.recycler_kseb_section, parent, false );
        return new SectionListViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder( SectionListViewHolder sectionListViewHolder, int position){
        SectionDetails sectionDetails = mSectionDetailsList.get( position );
        if ( sectionDetails != null ){
            sectionListViewHolder.txtSectionName.setText( sectionDetails.getSectionName() );
            sectionListViewHolder.txtSectionCode.setText( sectionDetails.getSectionCode() );
            sectionListViewHolder.relativeParentList.setOnClickListener(view -> mSectionSelect.onSelect( sectionDetails ));
        }
    }
    public void addSections( List< SectionDetails > sectionDetails ){
        mSectionDetailsList = sectionDetails;
        notifyDataSetChanged();
    }

    public interface SectionSelect{
        void onSelect( SectionDetails sectionDetails );
    }

    public class SectionListViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtSectionName;
        private final TextView txtSectionCode;
        private final RelativeLayout relativeParentList;
        public SectionListViewHolder( View view ){
            super( view );
            txtSectionName = view.findViewById( R.id.txt_section_name );
            txtSectionCode = view.findViewById( R.id.txt_section_code );
            relativeParentList = view.findViewById( R.id.linear_parent_kseb_list );
        }
    }
}
