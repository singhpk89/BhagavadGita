package com.sunilsahoo.bhagavadgita.activity;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.OnSettingsChangeListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Item;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Utility;
import com.sunilsahoo.bhagavadgita.view.InfoFragment;
import com.sunilsahoo.bhagavadgita.view.NavDrawerItem;
import com.sunilsahoo.bhagavadgita.view.NavDrawerListAdapter;
import com.sunilsahoo.bhagavadgita.view.QuoteDetailFragment;
import com.sunilsahoo.bhagavadgita.view.QuotesListFragment;
import com.sunilsahoo.bhagavadgita.view.SearchViewFragment;
import com.sunilsahoo.bhagavadgita.view.SettingsFragment;

public class BhagavadGitaMainActivity extends FragmentActivity implements
        OnSettingsChangeListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(
                R.array.menu_drawer_options);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(
                R.array.menu_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
     // Continue Reading
        navDrawerItems
                .add(new NavDrawerItem(
                        navMenuTitles[Constants.SlidingMenuItems.CONTINUE_READING],
                        navMenuIcons.getResourceId(
                                Constants.SlidingMenuItems.CONTINUE_READING, -1)));
        // Chapters
        navDrawerItems
                .add(new NavDrawerItem(
                        navMenuTitles[Constants.SlidingMenuItems.CHAPTER],
                        navMenuIcons.getResourceId(
                                Constants.SlidingMenuItems.CHAPTER, -1)));
        // Favorite
        navDrawerItems.add(new NavDrawerItem(
                navMenuTitles[Constants.SlidingMenuItems.FAVOURITE],
                navMenuIcons.getResourceId(
                        Constants.SlidingMenuItems.FAVOURITE, -1)));
        // Search
        navDrawerItems.add(new NavDrawerItem(
                navMenuTitles[Constants.SlidingMenuItems.SEARCH], navMenuIcons
                        .getResourceId(Constants.SlidingMenuItems.SEARCH, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(
                navMenuTitles[Constants.SlidingMenuItems.SETTINGS],
                navMenuIcons.getResourceId(Constants.SlidingMenuItems.SETTINGS,
                        -1)));
        // QOD
        navDrawerItems.add(new NavDrawerItem(
                navMenuTitles[Constants.SlidingMenuItems.QOD], navMenuIcons
                        .getResourceId(Constants.SlidingMenuItems.QOD, -1)));
        // Info
        navDrawerItems.add(new NavDrawerItem(
                navMenuTitles[Constants.SlidingMenuItems.INFO], navMenuIcons
                        .getResourceId(Constants.SlidingMenuItems.INFO, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                                   // accessibility
                R.string.app_name // nav drawer close - description for
                                  // accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * 
     * @throws Exception
     * */
    public void displayView(int position) {
        Fragment fragment = null;
        String fragmentType = null;
        switch (position) {
        case Constants.SlidingMenuItems.CONTINUE_READING:
            // chapter
            fragment = new QuoteDetailFragment();
            Bundle bundle = new Bundle();
            fragmentType = Constants.FRAG_QUOTE_DETAIL;
//            Quote lastReadQuote = GitaDBOperation.getQuoteById(PreferenceUtils.getLastReadQuoteID(this), this);
            bundle.putSerializable(Constants.QUOTE_LIST, GitaDBOperation.getQuotesWOChapter(Constants.EOF, this));
            bundle.putInt(Constants.SELECTED_QUOTE, PreferenceUtils.getLastReadQuoteID(this));
            bundle.putString(Constants.FRAG_TYPE, fragmentType);
            fragment.setArguments(bundle);
            break;
        case Constants.SlidingMenuItems.CHAPTER:
            // chapter
            fragment = new QuotesListFragment();
            bundle = new Bundle();
            fragmentType = Constants.FRAG_CHAPTERS_LIST;
            bundle.putString(Constants.FRAG_TYPE, fragmentType);
            fragment.setArguments(bundle);
            break;
        /*
         * case Constants.SlidingMenuItems.QUOTES: //quotes fragment = new
         * QuotesListFragment(); break;
         */
        case Constants.SlidingMenuItems.FAVOURITE:
            // favorite
            fragment = new QuotesListFragment();
            fragmentType = Constants.FRAG_FAVOURITE;
            bundle = new Bundle();
            bundle.putString(Constants.FRAG_TYPE, fragmentType);
            fragment.setArguments(bundle);
            break;
        case Constants.SlidingMenuItems.QOD:
            // qod
            fragmentType = Constants.FRAG_QOD;
            int qodId = Utility.getQODID(this);
            ArrayList<Item> quoteList = new ArrayList<Item>();
            Quote qod = GitaDBOperation.getQuoteById(qodId, this);
            quoteList.add(qod);
            bundle = new Bundle();
            bundle.putSerializable(Constants.QUOTE_LIST, quoteList);
            bundle.putInt(Constants.SELECTED_QUOTE, qod.getId());
            bundle.putString(Constants.FRAG_TYPE, fragmentType);
            fragment = new QuoteDetailFragment();
            fragment.setArguments(bundle);
            break;

        case Constants.SlidingMenuItems.SETTINGS:
            // settings
            fragment = new SettingsFragment();
            break;
        case Constants.SlidingMenuItems.INFO:
            // info
            fragment = new InfoFragment();
            break;

        case Constants.SlidingMenuItems.SEARCH:
            // search
            fragmentType = Constants.FRAG_SEARCH;
            fragment = new SearchViewFragment();
            break;

        /*
         * case Constants.SlidingMenuItems.EXIT: //exit finish(); break;
         */

        default:
            Toast.makeText(this, "This Feature is Not Implemented Yet",
                    Toast.LENGTH_SHORT).show();
            break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            if (isDialogFragment(fragment)) {
                ((DialogFragment) fragment).show(fragmentManager,
                        Constants.FRAG_SETTINGS);
            } else {
                if (fragmentType != null) {
                    transaction.replace(R.id.frame_container, fragment,
                            fragmentType).commit();
                } else {
                    Log.e(TAG, "Invalid fragment type ");
                }
                setTitle(navMenuTitles[position]);
            }

            // update selected item and title, then close the drawer
            if (!isDialogFragment(fragment)) {
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSettingsChanged(int itemType) {
        Log.d(TAG, "onSettingsChanged :" + itemType);
        String fragArr[] = new String[] { Constants.FRAG_QUOTE_DETAIL,
                Constants.FRAG_DETAIL_HOME, Constants.FRAG_QUOTES_LIST,
                Constants.FRAG_SEARCH, Constants.FRAG_QOD,
                Constants.FRAG_CHAPTERS_LIST, Constants.FRAG_FAVOURITE, Constants.FRAG_FAV_QUOTE_DETAIL, Constants.FRAG_SEARCH_QUOTE_DETAIL, Constants.FRAG_CHAPTER_QUOTE_DETAIL, Constants.FRAG_CHAPTER_QUOTE_LIST };
        for (String tag : fragArr) {
            GitaFragment frag = (GitaFragment) getSupportFragmentManager()
                    .findFragmentByTag(tag);
            if (frag != null) {
                frag.onSettingsChanged(itemType);
            }
        }

    }

    private boolean isDialogFragment(Fragment frag) {
        return (frag != null) && (frag instanceof DialogFragment);
    }

}
