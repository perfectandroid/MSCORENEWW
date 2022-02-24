package com.creativethoughts.iscore;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView textViewVersionName = rootView.findViewById(R.id.textviewVersionName);
        TextView txt_app_name = rootView.findViewById(R.id.txt_app_name);
        String versionName = "V-"+ IScoreApplication.versionName;
        textViewVersionName.setText(versionName);

        SharedPreferences ResellerNameeSP = getActivity().getSharedPreferences(Config.SHARED_PREF2, 0);
        String aapName = ResellerNameeSP.getString("ResellerName","");
        txt_app_name.setText(aapName);

        return rootView;
    }


}
