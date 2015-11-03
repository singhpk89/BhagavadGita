package com.sunilsahoo.bhagavadgita.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.sunilsahoo.bhagavadgita.OnItemSelectionListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Utility;

public class ChapterQuoteSpinnerView {
    protected static final String TAG = "ChapterQuoteSpinnerView";
    private Spinner mChapterSpinner = null;
    private Spinner mQuoteSpinner = null;
    private ArrayAdapter<String> mChapterAdapter = null;
    private ArrayAdapter<String> mQuoteAdapter = null;
    private List<String> chapterSpinnerArr;
    private List<String> quoteSpinnerArr;
    private Context mContext = null;
    private OnItemSelectionListener mItemSelectionListener = null;
    private ImageButton moreIB = null;

    public void initialize(Context context, View rootView,
            OnItemSelectionListener itemSelectionListener) {
        mContext = context;
        mItemSelectionListener = itemSelectionListener;
        mChapterSpinner = (Spinner) rootView.findViewById(R.id.chapter_spinner);
        mQuoteSpinner = (Spinner) rootView.findViewById(R.id.quote_spinner);
        moreIB = (ImageButton) rootView.findViewById(R.id.actionbar_moreBtn);
        chapterSpinnerArr = getChapterArr();
        mChapterAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, chapterSpinnerArr);
        mChapterAdapter.setDropDownViewResource(R.layout.simple_list_item_1);
        mChapterSpinner.setAdapter(mChapterAdapter);
        mChapterSpinner.setOnItemSelectedListener(onItemSelectedListener);
        setChapterSelection(PreferenceUtils.getSelectedChapter(context));
        updateQuoteSpinner();
    }

    public void updateQuoteSpinner() {
        updateQuotesArr();
        if (mQuoteAdapter == null) {
            mQuoteAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, quoteSpinnerArr);
            mQuoteAdapter.setDropDownViewResource(R.layout.simple_list_item_1);
            mQuoteSpinner.setAdapter(mQuoteAdapter);
            mQuoteSpinner.setOnItemSelectedListener(onItemSelectedListener);
        } else {
            mQuoteAdapter.notifyDataSetChanged();
        }

        setQuoteSelection(PreferenceUtils.getSelectedQuoteText(mContext));
    }

    private void updateQuotesArr() {
        int selectedChapterNo = PreferenceUtils.getSelectedChapter(mContext);
        ArrayList<Quote> itemList = GitaDBOperation.getQuotesOf(
                selectedChapterNo, mContext);
        if (quoteSpinnerArr == null) {
            quoteSpinnerArr = new ArrayList<String>();
        } else {
            quoteSpinnerArr.clear();
        }
        for (int i = 0; i < itemList.size(); i++) {
            quoteSpinnerArr.add(itemList.get(i).getTextId());
        }
    }

    private List<String> getChapterArr() {
        int chapterCount = GitaDBOperation.getChapterCount(mContext);
        List<String> chapterArr = new ArrayList<String>();
        chapterArr.add(Constants.SPINNER_CHAPTER_INTRODUCTION);
        for (int chapter = 1; chapter < chapterCount; chapter++) {
            chapterArr.add(Constants.SPINNER_CHAPTER_PREFIX + chapter);
        }
        return chapterArr;
    }

    OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                long id) {
            if (!arg0.isEnabled()) {
                return;
            }
            if (arg0 == mChapterSpinner) {
                Utility.updateChapterId(mContext, (pos));
                updateQuoteSpinner();

            } else if (arg0 == mQuoteSpinner) {
                PreferenceUtils.setSelectedQuoteText(mContext, mQuoteSpinner
                        .getSelectedItem().toString());
            }
            mItemSelectionListener.onItemSelected();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    public Spinner getQuoteSpinner() {
        return mQuoteSpinner;
    }

    public Spinner getChapterSpinner() {
        return mChapterSpinner;
    }

    public void setVisibilityOfMenuOptions(int visibility) {
        if (moreIB != null) {
            moreIB.setVisibility(visibility);
        }
    }

    public void setQuoteSelection(String quote) {
        if (quote != null) {
            PreferenceUtils.setSelectedQuoteText(mContext, quote);
        }
        if ((mQuoteSpinner != null) && (mQuoteAdapter != null)) {
            int quotePosition = mQuoteAdapter.getPosition(quote);
            if((quotePosition == Constants.EOF) && !mQuoteSpinner.isEnabled()){
                quoteSpinnerArr = new ArrayList<String>();
                quoteSpinnerArr.add(quote);
                mQuoteAdapter.notifyDataSetChanged();
                quotePosition = 0;
            }
            mQuoteSpinner.setSelection(quotePosition);
        }
    }

    public void setChapterSelection(int chapterId) {
        if (chapterId < 1) { // For All and Introduction
            mChapterSpinner.setSelection(chapterId);
        } else {
            int chapterIdNew = mChapterAdapter
                    .getPosition(Constants.SPINNER_CHAPTER_PREFIX + chapterId);
            mChapterSpinner.setSelection(chapterIdNew);
        }
    }

    public void enableChapterSpinner(boolean enable) {
        if (mChapterSpinner != null)
            mChapterSpinner.setEnabled(enable);
    }

    public void enableQuoteSpinner(boolean enable) {
        if (mQuoteSpinner != null)
            mQuoteSpinner.setEnabled(enable);
    }
}
