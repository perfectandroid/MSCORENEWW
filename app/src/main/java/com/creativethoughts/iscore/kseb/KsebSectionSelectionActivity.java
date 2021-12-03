package com.creativethoughts.iscore.kseb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtilitySectionList;
import com.google.gson.Gson;


import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class KsebSectionSelectionActivity extends Activity {
    private RecyclerSectionListAdapter mRecyclerSectionListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kseb_section_selection);

        RecyclerView recyclerSectionList = findViewById(R.id.recycler_select_kseb_section);
        recyclerSectionList.setHasFixedSize( true );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerSectionList.setLayoutManager( layoutManager );

        mRecyclerSectionListAdapter = new RecyclerSectionListAdapter(new ArrayList< >(), sectionDetails -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable( getString(R.string.kseb_section_list  ), sectionDetails );
            intent.putExtras( bundle );
            setResult( RESULT_OK, intent );
            finish();
        });
        recyclerSectionList.setAdapter(mRecyclerSectionListAdapter);

        EditText edtTxtSectionName = findViewById( R.id.edt_txt_section_name );
        edtTxtSectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence keyWord, int i, int i1, int i2) {
                 getObservable( keyWord.toString() )
                         .subscribeOn( Schedulers.io() )
                         .observeOn( AndroidSchedulers.mainThread() )
                         .subscribe(  getObserver() );
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing
            }
        });
    }
    private Observable< String > getObservable( String keyword ){
        return Observable.fromCallable( ()-> listenText( keyword ));
    }
    private Observer< String > getObserver(  ){
        return  new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Do nothing
            }

            @Override
            public void onNext(String result ) {
                processResult( result );
            }

            @Override
            public void onError(Throwable e) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                //Do nothing
            }
        };
    }
    private String listenText( String keyWord ){
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
        String BASE_URL=pref.getString("oldbaseurl", null);
        String url = BASE_URL+"/KSEBSectionList?Sectionname="+ keyWord;
        return ConnectionUtilitySectionList.getResponse( url );
    }
    private void processResult( String result ){
        Gson gson = new Gson();
        try{
            SectionSearchResult sectionSearchResult  = gson.fromJson( result, SectionSearchResult.class );
            if ( mRecyclerSectionListAdapter != null ){
                mRecyclerSectionListAdapter.addSections( sectionSearchResult.getSectionDetailsList() );
                mRecyclerSectionListAdapter.notifyDataSetChanged();
            }

        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG ){
                Log.e("ksebsectionexc", e.toString() );
            }
        }
    }
}
