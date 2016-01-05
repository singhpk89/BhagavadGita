/**
 * 
 */
package com.sunilsahoo.bhagavadgita.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.OnFragmentResult;
import com.sunilsahoo.bhagavadgita.OnItemSelectionListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.activity.BhagavadGitaMainActivity;
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
public class QuotesListFragment extends GitaFragment implements
        OnItemSelectionListener, OnFragmentResult {

    private static final long serialVersionUID = -7557604799767782321L;
    protected static final String TAG = "QuotesFragment";
    private ListView lv;
    private TextView tv_empty;
    private QuoteAdapter adapter;
    private ArrayList<Item> listData;
    private static ProgressDialog dialogLoading;
    private Context mContext;
    private View rootView = null;
    private String fragmentType = Constants.FRAG_QUOTES_LIST;
    private SwipeRefreshLayout pullToRefresh = null;
//    private View mChapterQuoteHeader = null;
//    private LinearLayout mainContainerLL = null;
    private OnFragmentResult onFragmentResult = null;
    
    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");
        onFragmentResult = this;
        rootView = inflater.inflate(R.layout.full_quotes_index, container,
                false);
        initView(rootView);
        return rootView;
    }

    void initView(View rootView) {
        Log.d(TAG, "inside initView");
        mContext = getActivity();
        fragmentType = getArguments().getString(Constants.FRAG_TYPE);
        lv = (ListView) rootView.findViewById(R.id.fqi_ListView);
        lv.setFastScrollEnabled(true);
//        mChapterQuoteHeader = rootView.findViewById(R.id.chapter_quote_header);
        if (Constants.FRAG_FAVOURITE.equals(fragmentType)) {
            lv.setDividerHeight(0);
//            mChapterQuoteHeader.setVisibility(View.GONE);
            //TODO
        } else {
//            chapterQuoteSpinnerView.initialize(getActivity(), rootView, this);
        }
        ((BhagavadGitaMainActivity)mContext).setVisibilityOfMenuOptions(View.INVISIBLE);



        tv_empty = (TextView) rootView.findViewById(R.id.empty);

        listData = new ArrayList<Item>();
        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int pos,
                    long id) {
                if (Utility.isChapterFragment(fragmentType)) {
                    int selectedChapterId = ((Chapter) listData.get(pos))
                            .getId();
                    Utility.updateChapterId(mContext, selectedChapterId);
                    Utility.launchQuotesListFragment(null,
                            Constants.FRAG_CHAPTER_QUOTE_LIST, getActivity());
                } else {
                    if (listData.get(pos) instanceof Chapter) {
                        return;
                    }

                    Quote selectedQuote = (Quote) listData.get(pos);
                    if (!Constants.FRAG_FAVOURITE.equals(fragmentType)) {
//                        chapterQuoteSpinnerView.setQuoteSelection(selectedQuote
//                                .getTextId());
                        //TODO
                    }
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(Constants.QUOTE_LIST, new ArrayList<Item>(listData));
                    bundle.putInt(Constants.SELECTED_QUOTE,
                            selectedQuote.getId());
                    if (Constants.FRAG_CHAPTER_QUOTE_LIST.equals(fragmentType) || Constants.FRAG_FAVOURITE.equals(fragmentType)) {
                        bundle.putSerializable(
                                Constants.FAG_CLOSED_LISTENER_INSTANCE,
                                onFragmentResult);
                    }

                    String fragType = Constants.FRAG_QUOTE_DETAIL;
                    if (Constants.FRAG_FAVOURITE.equals(fragmentType)) {
                        fragType = Constants.FRAG_FAV_QUOTE_DETAIL;
                    }
                    if (Constants.FRAG_CHAPTER_QUOTE_LIST.equals(fragmentType)) {
                        fragType = Constants.FRAG_CHAPTER_QUOTE_DETAIL;
                    }
                    Utility.launchQuoteDetailFragment(bundle, fragType,
                            getActivity());

                }
            }
        });

        new GetQuoteData().execute();

    }

    private void refreshContent(){ 

        new Handler().postDelayed(new Runnable() {
               @Override public void run() {
                   pullToRefresh.setRefreshing(false);
               }
           }, 0);

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
            if(!isAdded()){
                return;
            }
            int dialogVal = Utility.isChapterFragment(fragmentType) ? R.string.loading_chapters
                    : R.string.loading_quotes;

            if (null == dialogLoading) {
                dialogLoading = DialogLoading.Loading(mContext,
                        getResources().getString(dialogVal));
                dialogLoading.show();
            } else if (!dialogLoading.isShowing()) {
                dialogLoading = DialogLoading.Loading(mContext,
                        getResources().getString(dialogVal));
                dialogLoading.show();
            }

        }

        @Override
        protected Object doInBackground(Object... params) {
            if(!isAdded()){
                return null;
            }
            if (Constants.FRAG_CHAPTERS_LIST.equals(fragmentType)) {
                listData = GitaDBOperation.getChapters(mContext);
            } else if (Constants.FRAG_CHAPTER_QUOTE_LIST.equals(fragmentType)) {
                int chapterNo = GitaDBOperation.getQuoteById(PreferenceUtils.getSelectedQuoteId(mContext), mContext).getChapterNo();
                listData = GitaDBOperation.getItemsOf(chapterNo, mContext);
            } else if (Constants.FRAG_FAVOURITE.equals(fragmentType)) {
                listData = GitaDBOperation.getQuoteByFav(mContext);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
           Log.d(TAG, "list data count :"+listData.size());
           if(!isAdded()){
               return;
           }
            if (adapter == null) {
                // if(tv_empty)
                if ((listData == null) || listData.isEmpty()) {
                    if (Constants.FRAG_FAVOURITE.equals(fragmentType)) {
//                        if (mChapterQuoteHeader != null) {
//                            mChapterQuoteHeader.setVisibility(View.GONE);
//                        }
                        //TODO
                        updateEmptyTextView();
                    }
                }
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
                                if (Constants.FRAG_FAVOURITE
                                        .equals(fragmentType)) {
                                    removeFromList(pos);
                                } else {
                                    ((Quote) listData.get(pos)).setFavourite(0);
                                }
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
            updateEmptyTextView();
            lv.setAdapter(adapter);
            // lv.setSelection(currentPostion);
            Log.d(TAG, "fragType :" + fragmentType + " isChapterFragment :"
                    + Utility.isChapterFragment(fragmentType) + " from pref :"
                    + PreferenceUtils.getSelectedQuoteId(mContext));
            if (!Utility.isChapterFragment(fragmentType)
                    && !Constants.FRAG_FAVOURITE.equals(fragmentType)) {
//                int selectedPosition = getPosition(chapterQuoteSpinnerView
//                        .getQuoteSpinner().getSelectedItem().toString());
//                lv.setSelection(selectedPosition);
                //TODO

            }
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
                dialogLoading = null;
            }

            super.onPostExecute(result);
        }
    }

    /*private int getPosition(String quoteText) {
        int selectedChapter = (GitaDBOperation.getQuoteById(PreferenceUtils.getSelectedQuoteId(getActivity()), getActivity()).getChapterNo());
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i) instanceof Quote) {
                if (matchQuote(quoteText, selectedChapter,
                        (Quote) listData.get(i))) {
                    return i;
                }
            }
        }
        return 0;
    }*/

    /*private boolean matchQuote(String quoteText, int chapterId, Quote quote) {
        if (chapterId == Constants.EOF) {
            return quoteText.equals(String.valueOf(quote.getId()));
        } else if (quote.getChapterNo() == chapterId) {
            return quoteText.equals(quote.getTextId());
        }
        return false;
    }*/

    @Override
    public void onItemSelected() {
        new GetQuoteData().execute();
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

        default:
            Log.d(TAG, "item Type unknown");
            break;
        }
    }

    private void removeFromList(int pos) {
        try {
            int chapterNo = ((Quote) listData.get(pos)).getChapterNo();
            listData.remove(pos);
            boolean isQuoteExists = false;
            for (Item item : listData) {
                if (item instanceof Quote) {
                    if (chapterNo == ((Quote) item).getChapterNo()) {
                        isQuoteExists = true;
                        break;
                    }
                }
            }

            if (!isQuoteExists) {
                for (Item item : listData) {
                    if (item instanceof Chapter) {
                        if (chapterNo == ((Chapter) item).getId()) {
                            listData.remove(item);
                            break;
                        }
                    }
                }
            }
            if (listData.isEmpty()) {
//                mChapterQuoteHeader.setVisibility(View.GONE);
                //TODO
                updateEmptyTextView();
            }
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {

        }
    }

    private void updateEmptyTextView() {
        if (listData.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentCallback(int action, Bundle bundle) {
        /*if (action == Constants.FragmentCallbackAction.UPDATE_UI) {
            if (bundle == null) {
                return;
            }
            String selectedQuote = bundle
                    .getString(Constants.SELECTED_QUOTE_TEXT);
            if ((selectedQuote != null) && (chapterQuoteSpinnerView != null)) {
                chapterQuoteSpinnerView.setQuoteSelection(selectedQuote);
            }
        } else if (action == Constants.FragmentCallbackAction.UPDATE_DATA) {
            new GetQuoteData().execute();
        }else if(action == Constants.FragmentCallbackAction.UPDATE_ALL){
            String selectedQuote = bundle
                    .getString(Constants.SELECTED_QUOTE_TEXT);
            if ((selectedQuote != null) && (chapterQuoteSpinnerView != null)) {
                chapterQuoteSpinnerView.setQuoteSelection(selectedQuote);
            }
            new GetQuoteData().execute();
        }*/
        //TODO
    }
    
    @Override
    public void onDestroyView() {
        if(dialogLoading != null){
            dialogLoading.dismiss();
            dialogLoading = null;
        }
        super.onDestroyView();
    }
}
