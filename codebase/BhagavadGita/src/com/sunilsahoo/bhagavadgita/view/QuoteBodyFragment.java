package com.sunilsahoo.bhagavadgita.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Utility;

/**
 * @author sunilsahoo
 * 
 */
public class QuoteBodyFragment extends GitaFragment{

    protected static final String TAG = "QuoteBodyFragment";
    private TextView tv_body;
    private TextView slokaTV;
    private TextView slokaTitleTV;
    private TextView translationTitleTV;
    private Quote quote;
    private ScrollView mScrollView;
    private RelativeLayout parentView;

    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.quote_body, container, false);
        initView(rootView);
        return rootView;
    }

    void initView(View rootView) {
        // chapterQuoteSpinnerView.initialize(getActivity(), rootView, this);
        Bundle b = getArguments();
        if (b.containsKey(Constants.Bundle_quote)) {
            quote = (Quote) b.get(Constants.Bundle_quote);
        } else {
            Utility.closeCurrentFragment(getActivity());
        }

        parentView = (RelativeLayout) rootView
                .findViewById(R.id.qp_main_parent);
        tv_body = (TextView) rootView.findViewById(R.id.qp_body);
        slokaTV = (TextView) rootView.findViewById(R.id.sloka);
        slokaTV.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "NotoSansHindi-Regular.ttf"));
        translationTitleTV = (TextView) rootView
                .findViewById(R.id.translationTitle);
        slokaTitleTV = (TextView) rootView.findViewById(R.id.slokaTitle);

        mScrollView = (ScrollView) rootView.findViewById(R.id.qp_body_wrapper);
        mScrollView.setHorizontalFadingEdgeEnabled(false);
        mScrollView.setVerticalFadingEdgeEnabled(false);
        setQuoteContent();
        updateSlokaVisibility();
        // chapterQuoteSpinnerView.setQuoteSelection(quote.getTextId());

        Utility.updateBackgroundImage(parentView, getActivity());
        updateFontColor();
        updateFontSize();
    }


    @Override
    public void onSettingsChanged(int itemType) {
    }

    


    private void updateSlokaVisibility() {
        int visibility = PreferenceUtils.getShowSloka(getActivity()) && (quote.getChapterNo() >0) ? View.VISIBLE
                : View.GONE;
        slokaTitleTV.setVisibility(visibility);
        translationTitleTV.setVisibility(visibility);
        slokaTV.setVisibility(visibility);
    }

    private void setQuoteContent() {
        tv_body.setText(quote.getBody());
        slokaTV.setText(quote.getSlokaSanskrit());
    }

    private void updateFontColor() {
        Utility.setTextColor(tv_body, getActivity());
        Utility.setTextColor(slokaTV, getActivity());
    }

    private void updateFontSize() {
        int fontSize = PreferenceUtils.getFontSize(getActivity());
        if (tv_body != null) {
            tv_body.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }

        if (slokaTV != null) {
            slokaTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }

        if (slokaTitleTV != null) {
            slokaTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }

        if (translationTitleTV != null) {
            translationTitleTV
                    .setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }
    
}
