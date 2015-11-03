package com.sunilsahoo.bhagavadgita.view;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.adapter.QuoteAdapter;
import com.sunilsahoo.bhagavadgita.adapter.QuoteAdapter.OnClickCheckBoxListener;
import com.sunilsahoo.bhagavadgita.beans.Chapter;
import com.sunilsahoo.bhagavadgita.beans.Item;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.dialog.DialogLoading;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Log;
import com.sunilsahoo.bhagavadgita.utils.Utility;

/**
 * @author sunilsahoo
 * 
 */
public class SearchViewFragment extends GitaFragment implements OnQueryTextListener{

    protected static final String TAG = "SearchViewFragment";
    protected static final int PERFORM_SEARCH = 0;
    protected static final int CANCEL_SEARCH = 1;
    private ListView lv;
    private TextView tv_empty;
    private QuoteAdapter adapter;
    private ArrayList<Item> listData;
    private ProgressDialog dialogLoading;
    private Context mContext;
    private View rootView = null;
    private String fragmentType = Constants.FRAG_QUOTES_LIST;
    private SearchView searchView = null;
    private static String mMatchText = null;
    private GetQuoteData mGetQuoteData = null;
    private LinearLayout mainContainerLL = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_quotes_index, container,
                false);
        initView(rootView);
        return rootView;
    }
    

    private void initView(View rootView) {
        mContext = getActivity();
        mMatchText = null;
        mainContainerLL = (LinearLayout) rootView
                .findViewById(R.id.backgroundLL);
        searchView = (SearchView) rootView.findViewById(R.id.searchViewFrag);
        searchView.setOnQueryTextListener(this);
        setSearchTextColor(getActivity());
        lv = (ListView) rootView.findViewById(R.id.fqi_ListView);

        tv_empty = (TextView) rootView.findViewById(R.id.empty);

        listData = new ArrayList<Item>();
        Utility.updateBackgroundImage(mainContainerLL, getActivity());

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int pos,
                    long id) {
                if (Utility.isChapterFragment(fragmentType)) {
                    int selectedChapterId = ((Chapter) listData.get(pos))
                            .getId();
                    Utility.updateChapterId(getActivity(), selectedChapterId);
                    Utility.launchQuotesListFragment(null,
                            Constants.FRAG_QUOTES_LIST, getActivity());
                } else {
                    if (listData.get(pos) instanceof Chapter) {
                        return;
                    }
                    
                    Quote selectedQuote = (Quote) listData.get(pos);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.QUOTE_LIST, new ArrayList<Item>(listData));
                    bundle.putInt(Constants.SELECTED_QUOTE, selectedQuote.getId());
                    Utility.launchQuoteDetailFragment(bundle,
                            Constants.FRAG_SEARCH_QUOTE_DETAIL, getActivity());
                }
            }
        });
        
        new GetQuoteData().execute();
        
    }

    /**
     * get data task
     * 
     * @author sunilsahoo
     * 
     */
    private class GetQuoteData extends AsyncTask<Object, Object, Object> {

        public GetQuoteData() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int dialogVal = Utility.isChapterFragment(fragmentType) ? R.string.loading_chapters
                    : R.string.loading_quotes;

            if (null == dialogLoading) {
                dialogLoading = DialogLoading.Loading(getActivity(),
                        getResources().getString(dialogVal));
                dialogLoading.show();
            } else if (!dialogLoading.isShowing()) {
                dialogLoading = DialogLoading.Loading(getActivity(),
                        getResources().getString(dialogVal));
                dialogLoading.show();
            }

        }

        @Override
        protected Object doInBackground(Object... params) {
            if (Utility.isChapterFragment(fragmentType)) {
                listData = GitaDBOperation.getChapters(mContext);
            } else {
                listData = GitaDBOperation.getItemsOf(mMatchText, mContext);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

            if (adapter == null) {
                adapter = new QuoteAdapter(mContext, fragmentType, listData);
                adapter.setOnClickCheckBoxListener(new OnClickCheckBoxListener() {
                    @Override
                    public void OnClick(View v, Item item, int pos) {
                        if (item instanceof Quote) {
                            Quote quote = (Quote) item;
                            if (quote.isFavourite() == 0) {
                                GitaDBOperation.addFavourites(quote.getId(),
                                        mContext);
                                ((Quote) listData.get(pos)).setFavourite(1);
                            } else {
                                GitaDBOperation.deleteFavourites(quote.getId(),
                                        mContext);
                                ((Quote) listData.get(pos)).setFavourite(0);
                            }
                        }
                        adapter.setData(listData);
                        adapter.notifyDataSetChanged();

                    }
                });
            } else {
                adapter.setData(listData);
                adapter.notifyDataSetChanged();
            }
            if (listData.size() == 0) {
                tv_empty.setVisibility(View.VISIBLE);
            } else {
                tv_empty.setVisibility(View.GONE);
            }
            lv.setAdapter(adapter);
            // lv.setSelection(currentPostion);
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            }

            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        cancelSearch();
        mMatchText = newText;
        scheduleSearch();
        return false;
    }
    
    
    
    
    private final Handler timeoutHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CANCEL_SEARCH:
                if(mGetQuoteData != null){
                mGetQuoteData.cancel(true);
                }
                break;

            case PERFORM_SEARCH:
                if(mGetQuoteData != null){
                    mGetQuoteData.cancel(true);
                }
                mGetQuoteData = new GetQuoteData();
                mGetQuoteData.execute();
                break;
            }
        }
    };
   

    Runnable swipeImageAnimRunnable = new Runnable() {
        public void run() {
            timeoutHandler.sendEmptyMessage(PERFORM_SEARCH);
        }
    };

    private void scheduleSearch() {
        timeoutHandler.postDelayed(swipeImageAnimRunnable,
                500);
    }

    private void cancelSearch() {
        timeoutHandler.sendEmptyMessage(CANCEL_SEARCH);
        timeoutHandler.removeCallbacks(swipeImageAnimRunnable);
    }

    @Override
    public void onSettingsChanged(int itemType) {
        Log.d(TAG, "onSettingsChanged :" + itemType);
        switch (itemType) {
        case Constants.SettingsItem.FONT_SIZE:
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            break;
        case Constants.SettingsItem.READ_MODE:
            Utility.updateBackgroundImage(mainContainerLL, getActivity());
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            setSearchTextColor(getActivity());
            break;
        default:
            Log.d(TAG, "item Type unknown");
            break;
        }
    }
    
    private void setSearchTextColor(Context context){
        int id =  searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);

        // Set search text color
        if(textView == null){
            return;
        }
        int colorId = PreferenceUtils.getDayMode(context) ? R.color.text_black : R.color.text_white;
        textView.setTextColor(context.getResources().getColor(colorId));

        // Set search hints color
        textView.setHintTextColor(context.getResources().getColor(PreferenceUtils.getDayMode(context) ? R.color.text_hint_black : R.color.text_hint_white));
    }
    public static String getMatchText(){
        return mMatchText;
    }

}
