package com.creativethoughts.iscore;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.adapters.MessageAdapter;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.Message;
import com.creativethoughts.iscore.db.dao.model.UserDetails;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {

    private MessageAdapter mMessageAdapter;


    public OffersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    public static OffersFragment newInstance() {
        OffersFragment fragment = new OffersFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        ListView listView = rootView.findViewById(R.id.listViewMessages);
        mMessageAdapter = new MessageAdapter(getActivity());

        listView.setAdapter(mMessageAdapter);

        prepareMessagesData();

        return rootView;
    }


    private void prepareMessagesData() {
        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();

        ArrayList<Message> messages =
                PBMessagesDAO.getInstance().getAllOffers(userDetails.customerId);

        if (messages != null && !messages.isEmpty() ) {
            mMessageAdapter.setMessages(messages);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setCancelable(true);
            //alertDialog.setTitle("No Access");
            alertDialog.setMessage("No data found!");
            alertDialog.setIcon(R.drawable.ic_warning);
            alertDialog.show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(),HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }
}