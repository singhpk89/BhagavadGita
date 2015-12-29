package com.sunilsahoo.bhagavadgita.view;

import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Log;
import com.sunilsahoo.bhagavadgita.utils.Utility;

/**
 * @author sunilsahoo
 * 
 */
public class QuoteBodyFragment extends GitaFragment implements OnClickListener, OnLongClickListener {

    protected static final String TAG = "QuoteBodyFragment";
    private TextView tv_body;
    private TextView slokaTV;
    private TextView slokaTitleTV;
    private TextView translationTitleTV;
    private TextView slokaIndex;
    private Quote quote;
    private ScrollView mScrollView;
    private RelativeLayout parentView;
    private ImageView playPause;
    private boolean mIsManual = false;
    private static final int STOP_TALK = 2;
    private static final int START_TALK = 1;
    private TextToSpeech ttobj = null;

    private View rootView;
    private String lastquoteOfChapter = "";

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

        lastquoteOfChapter = GitaDBOperation.getLastQuoteTextChapter(
                getActivity(), quote.getChapterNo());
        parentView = (RelativeLayout) rootView
                .findViewById(R.id.qp_main_parent);
        tv_body = (TextView) rootView.findViewById(R.id.qp_body);
        tv_body.setOnLongClickListener(this);
        slokaTV = (TextView) rootView.findViewById(R.id.sloka);
        slokaTV.setOnLongClickListener(this);
        playPause = (ImageView) rootView.findViewById(R.id.playPause);
        playPause.setOnClickListener(this);
        slokaTV.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "NotoSansHindi-Regular.ttf"));
        translationTitleTV = (TextView) rootView
                .findViewById(R.id.translationTitle);
        slokaTitleTV = (TextView) rootView.findViewById(R.id.slokaTitle);
        slokaIndex = (TextView) rootView.findViewById(R.id.slokaIndex);
        slokaIndex.setText(quote.getTextId() + "/" + lastquoteOfChapter);

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
        switch (itemType) {
        case Constants.SettingsItem.ENABLE_SPEAK:
            processAutoTalk(false);
            break;
        }
    }

    private void updateSlokaVisibility() {
        int visibility = PreferenceUtils.getShowSloka(getActivity())
                && (quote.getChapterNo() > 0) ? View.VISIBLE : View.GONE;
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

    @Override
    public void onPause() {
        super.onPause();
        cancelTalk();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelTalk();
    }

    @Override
    public void onResume() {
        super.onResume();
        processAutoTalk(false);
    }

    private void stopTalkResource(boolean setStatus) {
        try {
            if (ttobj != null) {
                Log.d(TAG, "stop talk");
                ttobj.stop();
                ttobj.shutdown();
                ttobj = null;
            }
        } catch (Exception ex) {

        }
        playPause.setSelected(!setStatus);
    }

    private void initializeTalkResource(boolean manual) {
        try {
            // Log.d(TAG, "initialize talk");
            boolean startSpeak = manual
                    || PreferenceUtils.enableTalk(getActivity());
            stopTalkResource(false);
            if (!startSpeak) {
                return;
            }
            ttobj = new TextToSpeech(getActivity(),
                    new TextToSpeech.OnInitListener() {

                        @Override
                        public void onInit(int status) {
                            // Log.d(TAG, "start talk");
                            String speakingText = getSpeakingText();
                            if ((status != TextToSpeech.ERROR && !ttobj
                                    .isSpeaking()) && (speakingText != null)) {
                                Log.d(TAG, "talking");
                                ttobj.setSpeechRate(0.85f);
                                ttobj.setLanguage(Locale.UK);
                                ttobj.speak(speakingText,
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Exception :" + ex);
        }
    }

    private String getSpeakingText() {
        if (quote == null) {
            return null;
        }
        return PreferenceUtils.getReadSloka(getActivity()) ? getResources()
                .getString(R.string.sloka)
                + quote.getSlokaEnglish()
                + getResources().getString(R.string.translation)
                + quote.getBody() : quote.getBody();
    }

    private final Handler timeoutHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case STOP_TALK:
                stopTalkResource(true);
                break;

            case START_TALK:
                initializeTalkResource(mIsManual);
                break;
            }
        }
    };

    Runnable swipeImageAnimRunnable = new Runnable() {
        public void run() {
            timeoutHandler.sendEmptyMessage(START_TALK);
        }
    };

    private void processAutoTalk(boolean startNow){
        boolean isAutoTalkOn = PreferenceUtils.enableTalk(getActivity());
        Log.d(TAG, "isAutoTalkOn :"+isAutoTalkOn);
        if (isAutoTalkOn) {
            startTalk(startNow);
        } else {
            cancelTalk();
        }
    }
    private void startTalk(boolean startNow) {
        try {
            Log.d(TAG, "inside startTalk : " + mIsManual);            
            mIsManual = startNow;
            cancelTalk();
            playPause.setSelected(true);
            if (startNow) {
                timeoutHandler.post(swipeImageAnimRunnable);
            } else {
                timeoutHandler.postDelayed(swipeImageAnimRunnable,
                        Constants.ANIM_START_AT_DELAY);
            }

        } catch (Exception ex) {
            Log.w(TAG, "Problem in scheduling talk :" + ex);
            playPause.setSelected(false);
        }
    }

    private void cancelTalk() {
        try {
            timeoutHandler.sendEmptyMessage(STOP_TALK);
            timeoutHandler.removeCallbacks(swipeImageAnimRunnable);
        } catch (Exception ex) {
            Log.w(TAG, "Problem in canceling talk :" + ex);
        }
    }

    /*private String getCopyText() {
        if (quote == null) {
            return null;
        }
        String chapterNoText = getString(R.string.app_name)
                + " - "
                + ((quote.getChapterNo() > 0) ? "Chapter "
                        + quote.getChapterNo() + "(Verse " + quote.getTextId()
                        + ")" : Constants.SPINNER_CHAPTER_INTRODUCTION);
        return (PreferenceUtils.getShowSloka(getActivity())
                && (quote.getChapterNo() > 0) ? chapterNoText + " : "
                + quote.getSlokaSanskrit() + "\n" + quote.getBody()
                : chapterNoText + " : " + quote.getBody())
                + "\n" + getResources().getString(R.string.share_bottom);
    }*/

    @Override
    public void onClick(View v) {
        if(v == playPause){
            if(playPause.isSelected()){
                cancelTalk();
            }else{
                startTalk(true);
            }
        }
        
    }

    @Override
    public boolean onLongClick(View v) {
        String chapterNoText = getString(R.string.app_name)
                + " - "
                + ((quote.getChapterNo() > 0) ? "Chapter "
                        + quote.getChapterNo() + "(Verse " + quote.getTextId()
                        + ")" : Constants.SPINNER_CHAPTER_INTRODUCTION);
        if ((v == tv_body) || (v == slokaTV)){
            String msgheader ="";
            if(v == tv_body){
                msgheader = getResources().getString(R.string.translation);
            }else{
                msgheader = getResources().getString(R.string.sloka);
            }
            setClipboard(getActivity(), chapterNoText + " : "+((TextView)v).getText(), msgheader);
        }
        
        return false;
    }
    
    
    private void setClipboard(Context context, String text, String msgHeader) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(
                getResources().getString(R.string.copy_title), text);
        clipboard.setPrimaryClip(clip);
        String msg = "";
        try{
            msg = String.format(getResources().getString(R.string.msg_copy), msgHeader);
        }catch(Exception ex){
            msg = getResources().getString(R.string.msg_copy);
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                .show();
    }
}
