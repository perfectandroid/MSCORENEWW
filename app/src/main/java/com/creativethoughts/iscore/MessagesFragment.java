package com.creativethoughts.iscore;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.adapters.MessageAdapter;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.Message;
import com.creativethoughts.iscore.db.dao.model.UserDetails;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {

    private ListView mListView;
    private MessageAdapter mMessageAdapter;


    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        // get the listview
        mListView = (ListView) rootView.findViewById(R.id.listViewMessages);
        mMessageAdapter = new MessageAdapter(getActivity());
        // preparing list data


        mListView.setAdapter(mMessageAdapter);

        prepareMessagesData();

        return rootView;
    }


    private void prepareMessagesData() {

      //  UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
        String customerId = customerIdSP.getString("customerId","");


      //  ArrayList<Message> messages = PBMessagesDAO.getInstance().getAllMessages(userDetails.customerId);
        ArrayList<Message> messages = PBMessagesDAO.getInstance().getAllMessages(customerId);

        if (messages != null && messages.size() > 0) {
            mMessageAdapter.setMessages(messages);

            //            PBMessagesDAO.getInstance().markMessageAsRead(userDetails.customerId);
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
