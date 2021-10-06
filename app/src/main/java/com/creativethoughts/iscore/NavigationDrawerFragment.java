package com.creativethoughts.iscore;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.adapters.NewMenuAdapter;
import com.creativethoughts.iscore.db.dao.DynamicMenuDao;
import com.creativethoughts.iscore.db.dao.model.DynamicMenuDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private NewMenuAdapter adapter;
    private DynamicMenuDetails dynamicMenuDetails;


    public NavigationDrawerFragment() {
        //Do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerListView = (ExpandableListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        List<com.creativethoughts.iscore.MenuItem> menuItems = new ArrayList<>();


        com.creativethoughts.iscore.MenuItem menuItem0 = new com.creativethoughts.iscore.MenuItem();
        menuItem0.setMenuIcon(R.drawable.ic_home);
        menuItem0.id = 0  ;
        menuItem0.setMenuLabel(getString(R.string.title_section9));

        com.creativethoughts.iscore.MenuItem menuItem1 = new com.creativethoughts.iscore.MenuItem();
        menuItem1.setMenuIcon(R.drawable.ic_profile);
        menuItem1.id = 1;
        menuItem1.setMenuLabel("Profile");

        com.creativethoughts.iscore.MenuItem menuItem2 = new com.creativethoughts.iscore.MenuItem();
        menuItem2.setMenuIcon(R.drawable.ic_nav_mail);
        menuItem2.id = 2;
        menuItem2.setMenuLabel(getString(R.string.title_section3));

        com.creativethoughts.iscore.MenuItem menuItem3 = new com.creativethoughts.iscore.MenuItem();
        menuItem3.setMenuIcon(R.drawable.ic_nav_offer);
        menuItem3.id = 3;
        menuItem3.setMenuLabel(getString(R.string.title_section4));

        com.creativethoughts.iscore.MenuItem menuItem4 = new com.creativethoughts.iscore.MenuItem();
        menuItem4.setMenuIcon(R.drawable.appbillstatus);
        menuItem4.id = 4;
        menuItem4.setMenuLabel(getString(R.string.kseb_sub_name_bill_status_on_drawer));

     /*   com.creativethoughts.iscore.MenuItem menuItem5 = new com.creativethoughts.iscore.MenuItem();
        menuItem5.id = 5;
        menuItem5.setMenuIcon(R.drawable.ic_about_us);
        menuItem5.setMenuLabel("More");*/

        com.creativethoughts.iscore.MenuItem menuItem6 = new com.creativethoughts.iscore.MenuItem();
        menuItem6.id = 6;
        menuItem6.setMenuIcon(R.drawable.ic_date);
        menuItem6.setMenuLabel("Due Date Reminder");

        com.creativethoughts.iscore.MenuItem menuItem7 = new com.creativethoughts.iscore.MenuItem();
        menuItem7.id = 7;
        menuItem7.setMenuIcon(R.drawable.ic_nav_more);
        menuItem7.setMenuLabel("More");

        com.creativethoughts.iscore.MenuItem menuItem8 = new com.creativethoughts.iscore.MenuItem();
        menuItem8.id = 8;
        menuItem8.setMenuIcon(R.drawable.ic_nav_pin);
        menuItem8.setMenuLabel(getString(R.string.title_section5));

        com.creativethoughts.iscore.MenuItem menuItem9 = new com.creativethoughts.iscore.MenuItem();
        menuItem9.id = 9;
        menuItem9.setMenuIcon(R.drawable.ic_nav_settings);
        menuItem9.setMenuLabel(getString(R.string.title_section6));

     /*   com.creativethoughts.iscore.MenuItem menuItem10 = new com.creativethoughts.iscore.MenuItem();
        menuItem10.id = 10;
        menuItem10.setMenuIcon(R.drawable.ic_nav_close);
        menuItem10.setMenuLabel(getString(R.string.title_section8));*/

        com.creativethoughts.iscore.MenuItem menuItem11 = new com.creativethoughts.iscore.MenuItem();
        menuItem11.id = 11;
        menuItem11.setMenuIcon(R.drawable.ic_nav_close);
        menuItem11.setMenuLabel(getString(R.string.title_section8));

        dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();


        menuItems.add(menuItem0);
        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        menuItems.add(menuItem3);


        try {
            if ( IScoreApplication.decryptStart( dynamicMenuDetails.getKseb()).equals("0") ){
            }else {
                menuItems.add(menuItem4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       // menuItems.add(menuItem5);
        menuItems.add(menuItem6);
        menuItems.add(menuItem7);
        menuItems.add(menuItem8);
        menuItems.add(menuItem9);
        //menuItems.add(menuItem10);
        menuItems.add(menuItem11);

        adapter = new NewMenuAdapter(getActivity(), this::selectItem);
        adapter.setData((ArrayList<com.creativethoughts.iscore.MenuItem>) menuItems);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return mDrawerListView;
    }

    public void refreshMenu() {
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow_rtgs that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle =
                new ActionBarDrawerToggle(getActivity(),                    /* host Activity */
                        mDrawerLayout,            /* nav drawer image to replace 'Up' caret */
                        R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                        R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        if (!isAdded()) {
                            return;
                        }

                        getActivity()
                                .supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        if (!isAdded()) {
                            return;
                        }

                        if (!mUserLearnedDrawer) {
                            // The user manually opened the drawer; store this flag to prevent auto-showing
                            // the navigation drawer automatically in the future.
                            mUserLearnedDrawer = true;
                            SharedPreferences sp =
                                    PreferenceManager.getDefaultSharedPreferences(getActivity());
                            sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                        }

                        getActivity()
                                .supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                    }
                };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(() -> mDrawerToggle.syncState());

        //noinspection deprecation
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    private ActionBar getActionBar() {
        assert ((AppCompatActivity) getActivity()) != null;
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
