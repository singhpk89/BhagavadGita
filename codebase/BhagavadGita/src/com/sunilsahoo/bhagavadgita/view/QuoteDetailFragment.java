package com.sunilsahoo.bhagavadgita.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.OnFragmentResult;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.activity.BhagavadGitaMainActivity;
import com.sunilsahoo.bhagavadgita.adapter.QuotePageStateAdapter;
import com.sunilsahoo.bhagavadgita.beans.Chapter;
import com.sunilsahoo.bhagavadgita.beans.Item;
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
public class QuoteDetailFragment extends GitaFragment implements OnClickListener,
        OnPageChangeListener, OnInitListener {

    protected static final String TAG = "QuoteView";

    private RelativeLayout parentView;
    private ViewPager mPager;

    private View rootView;
    // private ChapterQuoteSpinnerView chapterQuoteSpinnerView = new
    // ChapterQuoteSpinnerView();

    private String fragmentType = null;
    private QuotePageStateAdapter mPageAdapter = null;
    private ArrayList<Item> quoteList = null;
    private int selectedQuoteId = 0;
    private int selectedPosition = 0;

    private ImageView prevChapter = null;
    private ImageView prevQuote = null;
    private ImageView nextQuote = null;
    private ImageView nextChapter = null;
    private int previousState = ViewPager.SCROLL_STATE_IDLE;
    private boolean userScrollChange = false;
    private OnFragmentResult onFragmentClosedListener = null;
    private boolean isFavChanged = false;
    private String lastquoteOfChapter = "";
    private boolean mIsManual = false;
    private static final int STOP_TALK = 2;
    private static final int START_TALK = 1;
    private static TextToSpeech ttobj = null;
    private HashMap<String, String> params = new HashMap<String, String>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.quote_preview, container, false);
        initView(rootView);
        return rootView;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(ttobj == null){
        ttobj = new TextToSpeech(getActivity(), this);
        }
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    void initView(View rootView) {
        Log.d(TAG, "on initView");
        // chapterQuoteSpinnerView.initialize(getActivity(), rootView, this);

        prevChapter = (ImageView) rootView.findViewById(R.id.chapterPrev);
        prevChapter.setOnClickListener(this);
        prevQuote = (ImageView) rootView.findViewById(R.id.quotePrev);
        prevQuote.setOnClickListener(this);
        nextChapter = (ImageView) rootView.findViewById(R.id.chapterNext);
        nextChapter.setOnClickListener(this);
        nextQuote = (ImageView) rootView.findViewById(R.id.quoteNext);
        nextQuote.setOnClickListener(this);

        Bundle b = getArguments();
        try {
            onFragmentClosedListener = (OnFragmentResult) b
                    .getSerializable(Constants.FAG_CLOSED_LISTENER_INSTANCE);
        } catch (Exception ex) {
        }
        fragmentType = b.getString(Constants.FRAG_TYPE);
        quoteList = (ArrayList<Item>) b.getSerializable(Constants.QUOTE_LIST);
        reArrangeItems();

        selectedQuoteId = b.getInt(Constants.SELECTED_QUOTE);
        selectedPosition = selectedPosition(quoteList, selectedQuoteId);

        if (quoteList == null) {
            Utility.closeCurrentFragment(getActivity());
        }

        parentView = (RelativeLayout) rootView
                .findViewById(R.id.qp_main_parent);
        ((BhagavadGitaMainActivity) getActivity()).getFavouriteBtn()
                .setOnClickListener(this);
        ((BhagavadGitaMainActivity) getActivity()).getShareBtn()
        .setOnClickListener(this);
        

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        perfrormPageSelectedtask(selectedPosition, false);
        mPager.setOnPageChangeListener(this);
        initializePager();

        Utility.updateBackgroundImage(parentView, getActivity());

        ((BhagavadGitaMainActivity) getActivity())
                .setVisibilityOfMenuOptions(View.VISIBLE);

    }

    private void setfavorite() {
        if (((BhagavadGitaMainActivity) getActivity()).getFavouriteBtn() != null) {
            ((BhagavadGitaMainActivity) getActivity()).getFavouriteBtn()
                    .setSelected(getSelectedQuote().isFavourite() == 1);
        }
    }

    private void initializePager() {
        mPageAdapter = new QuotePageStateAdapter(getActivity()
                .getSupportFragmentManager(), fragmentType, quoteList);
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(selectedPosition);
    }

    private void updateState() {
        prevQuote.setEnabled(false);
        nextQuote.setEnabled(false);
        prevChapter.setEnabled(false);
        nextChapter.setEnabled(false);

        if ((Constants.FRAG_QUOTE_DETAIL.equals(fragmentType))) {
            prevQuote.setEnabled(true);
            nextQuote.setEnabled(true);
            prevChapter.setEnabled(true);
            nextChapter.setEnabled(true);
            if (getSelectedQuote().getId() == quoteList.size()) {
                nextChapter.setEnabled(false);
                nextQuote.setEnabled(false);
            }
            if ((getSelectedQuote().getChapterNo()) == 0) {
                prevChapter.setEnabled(false);
                prevQuote.setEnabled(false);
            }
            
            if ((getSelectedQuote().getChapterNo()) == ((Quote) quoteList.get(quoteList.size()-1)).getChapterNo()) {
                nextChapter.setEnabled(false);
            }
        } else if ((Constants.FRAG_CHAPTER_QUOTE_DETAIL.equals(fragmentType))) {
            lastquoteOfChapter = GitaDBOperation.getLastQuoteTextChapter(
                    getActivity(), getSelectedQuote().getChapterNo());
            prevQuote.setEnabled(true);
            nextQuote.setEnabled(true);
            if (getSelectedQuote().getTextId().equals("1")
                    || getSelectedQuote().getTextId().contains("1-")) {
                prevQuote.setEnabled(false);
            }

            if (getSelectedQuote().getTextId().equals(lastquoteOfChapter)) {
                nextQuote.setEnabled(false);
            }
        } else if(Constants.FRAG_QOD.equals(fragmentType)){
            prevQuote.setEnabled(false);
            nextQuote.setEnabled(false);
            prevChapter.setEnabled(false);
            nextChapter.setEnabled(false);
        } else {
            prevQuote.setEnabled(true);
            nextQuote.setEnabled(true);
            if (mPager.getCurrentItem() == 0) {
                prevQuote.setEnabled(false);
            } else if (mPager.getCurrentItem() >= (quoteList.size() - 1)) {
                nextQuote.setEnabled(false);
            }
        }
    }

    @Override
    public void onSettingsChanged(int itemType) {
        Log.d(TAG, "onSettingsChanged :" + itemType);
        switch (itemType) {
        case Constants.SettingsItem.FONT_SIZE:
            mPageAdapter.notifyDataSetChanged();
            break;
        case Constants.SettingsItem.READ_MODE:
            Utility.updateBackgroundImage(parentView, getActivity());
            mPageAdapter.notifyDataSetChanged();
            break;
        case Constants.SettingsItem.SHOW_SLOKA:
            mPageAdapter.notifyDataSetChanged();
        case Constants.SettingsItem.ENABLE_SPEAK:
            processAutoTalk(false);
            break;
        case Constants.SettingsItem.PLAY:
            startTalk(true);
            break;
        case Constants.SettingsItem.PAUSE:
            cancelTalk();
            break;
        default:
            Log.d(TAG, "item Type unknown");
            break;
        }
    }

    private void updateFavourite(Quote quote) {
        if (quote.isFavourite() == 1) {
            quote.setFavourite(0);
            GitaDBOperation.deleteFavourites(quote.getId(), getActivity());
        } else {
            quote.setFavourite(1);
            GitaDBOperation.addFavourites(quote.getId(), getActivity());
        }
    }

    

    @Override
    public void onClick(View view) {
        if ((view == nextQuote) || (view == nextChapter) || (view == prevQuote)
                || (view == prevChapter)) {
            moveToPage(view);
        } else if (view == ((BhagavadGitaMainActivity) getActivity())
                .getFavouriteBtn()) {
            isFavChanged = true;
            updateFavourite(getSelectedQuote());
            setfavorite();
        } else if(view == ((BhagavadGitaMainActivity) getActivity())
                .getShareBtn()){
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getCopyText());
            Intent intent = Intent.createChooser(share, getActivity()
                    .getResources().getString(R.string.share_title));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
    }

    private void moveToPage(View view) {
        Log.d(TAG, "before moving position :" + selectedPosition);
        if (view == nextChapter) {
            for (int i = selectedPosition; i < quoteList.size() - 1; i++) {
                if (((Quote) quoteList.get(i)).getChapterNo() == getSelectedQuote()
                        .getChapterNo() + 1) {
                    selectedPosition = i;
                    break;
                }
            }
        }

        if (view == prevChapter) {
            int prevChapterNo = getSelectedQuote()
                    .getChapterNo() - 1;
            for (int i = selectedPosition; i >= 0; i--) {
                if (((Quote) quoteList.get(i)).getChapterNo() == prevChapterNo) {
                    selectedPosition = i;
                }
            }
        }
        Log.d(TAG, "selected position :"+selectedPosition);
        if ((view == nextQuote)) {
            selectedPosition = selectedPosition < quoteList.size() - 1 ? ++selectedPosition
                    : selectedPosition;
        } else if ((view == prevQuote)) {
            selectedPosition = selectedPosition > 0 ? --selectedPosition
                    : selectedPosition;
        }
        Log.d(TAG, "move to position :" + selectedPosition);
        mPager.setCurrentItem(selectedPosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Log.d(TAG, "onPageScrollStateChanged "+state);
        if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                && state == ViewPager.SCROLL_STATE_SETTLING)
            userScrollChange = true;

        else if (previousState == ViewPager.SCROLL_STATE_SETTLING
                && state == ViewPager.SCROLL_STATE_IDLE)
            userScrollChange = false;

        previousState = state;

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        Log.d(TAG, "onPageSelected " + arg0);
        perfrormPageSelectedtask(arg0, userScrollChange);        

    }

    private void perfrormPageSelectedtask(int arg0, boolean fromScroll) {
        Log.d(TAG, "perfrormPageSelectedtask :" + fromScroll
                + " selectedPosition : " + arg0 + " fragmentType :"
                + fragmentType + " size :" + quoteList.size());
        selectedPosition = arg0;
        updateTitle();
        updateState();
        if (Constants.FRAG_QUOTE_DETAIL.equals(fragmentType)) {
            PreferenceUtils.setLastReadQuoteID(getActivity(),
                    getSelectedQuote().getId());
        }
        setfavorite();
    }

    private String getCopyText() {
        if (getSelectedQuote() == null) {
            return null;
        }
        String chapterNoText = getString(R.string.app_name)
                + " - "
                + ((getSelectedQuote().getChapterNo() > 0) ? "Chapter "
                        + getSelectedQuote().getChapterNo() + "(Verse "
                        + getSelectedQuote().getTextId() + ")"
                        : Constants.SPINNER_CHAPTER_INTRODUCTION);
        return (PreferenceUtils.getShowSloka(getActivity())
                && (getSelectedQuote().getChapterNo() > 0) ? chapterNoText
                + " : " + getSelectedQuote().getSlokaSanskrit() + "\n"
                + getSelectedQuote().getBody() : chapterNoText + " : "
                + getSelectedQuote().getBody())
                + "\n" + getResources().getString(R.string.share_bottom);
    }

    private ArrayList<Item> reArrangeItems() {
        if (Constants.FRAG_CHAPTERS_LIST.equals(fragmentType)
                || Constants.FRAG_SEARCH_QUOTE_DETAIL.equals(fragmentType)
                || Constants.FRAG_FAV_QUOTE_DETAIL.equals(fragmentType)) {
            for (int i = quoteList.size() - 1; i >= 0; i--) {
                if (quoteList.get(i) instanceof Chapter) {
                    quoteList.remove(i);
                }
            }
        }
        return quoteList;
    }

    private int selectedPosition(ArrayList<Item> quoteList, int selectedQuoteId) {
        int index = 0;
        try {
            for (int i = 0; i < quoteList.size(); i++) {
                if (((Quote) quoteList.get(i)).getId() == selectedQuoteId) {
                    index = i;
                    break;
                }
            }
        } catch (Exception ex) {

        }
        return index;
    }

    private Quote getSelectedQuote() {
        try {
            return (Quote) quoteList.get(selectedPosition);
        } catch (Exception ex) {
            return null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ttobj != null){
            ttobj.shutdown();
            ttobj = null;
        }
        if (onFragmentClosedListener != null) {
            Bundle b = new Bundle();
            if (Constants.FRAG_CHAPTER_QUOTE_DETAIL.equals(fragmentType)) {
                if (getSelectedQuote() != null) {
                    b.putInt(Constants.SELECTED_CHAPTER, getSelectedQuote()
                            .getChapterNo());
                    if (getSelectedQuote().getTextId() != null) {
                        b.putString(Constants.SELECTED_QUOTE_TEXT,
                                getSelectedQuote().getTextId());
                    }

                }
                onFragmentClosedListener
                        .onFragmentCallback(
                                isFavChanged ? Constants.FragmentCallbackAction.UPDATE_ALL
                                        : Constants.FragmentCallbackAction.UPDATE_UI,
                                b);
            } else if (Constants.FRAG_FAV_QUOTE_DETAIL.equals(fragmentType)) {
                onFragmentClosedListener.onFragmentCallback(
                        Constants.FragmentCallbackAction.UPDATE_DATA, null);
            }

        }
    }

    private void updateTitle() {
        String title = "";
        if (Constants.FRAG_CHAPTER_QUOTE_DETAIL.equals(fragmentType)) {
            title = getSelectedQuote().getChapterNo() == 0 ? Constants.SPINNER_CHAPTER_INTRODUCTION
                    : String.format(
                            getResources().getString(
                                    R.string.particular_chapter),
                            getSelectedQuote().getChapterNo());
        } else if (Constants.FRAG_FAV_QUOTE_DETAIL.equals(fragmentType)) {
            title = String.format(
                    getResources().getString(R.string.favourites_title), selectedPosition+1, quoteList.size());
        } else if (Constants.FRAG_SEARCH_QUOTE_DETAIL.equals(fragmentType)) {
            title = String.format(
                    getResources().getString(R.string.search_title), selectedPosition+1, quoteList.size());
        } else if(Constants.FRAG_QOD.equals(fragmentType)){
            String[] navMenuTitles = getResources().getStringArray(
                    R.array.menu_drawer_options);
            title = navMenuTitles[Constants.SlidingMenuItems.QOD];
        }else {

            title = getSelectedQuote().getChapterNo() == 0 ? Constants.SPINNER_CHAPTER_INTRODUCTION
                    : String.format(
                            getResources().getString(R.string.chapter_title),
                            getSelectedQuote().getChapterNo(),
                            ((Quote) quoteList.get(quoteList.size() - 1))
                                    .getChapterNo());
        }
        ((BhagavadGitaMainActivity) getActivity()).setActionBarTitle(title);
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
            }
        } catch (Exception ex) {

        }
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
            speak(getSpeakingText());
        } catch (Exception ex) {
            Log.e(TAG, "Exception :" + ex);
        }
    }
    
    
    protected void speak(String speakingText) {
        if(params != null){
            params.clear();
        }
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"test");
//        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, speakingText);
        ttobj.speak(speakingText,TextToSpeech.QUEUE_FLUSH,params);
    }
    
    @SuppressLint("NewApi")
    public void setTtsCompletionListener(TextToSpeech tts)
    {
        final QuoteDetailFragment callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId)
                {
                    callWithResult.onDone(utteranceId);
                }

                @Override
                public void onError(String utteranceId)
                {
                    callWithResult.onError(utteranceId);
                }

                @Override
                public void onStart(String utteranceId)
                {
                    callWithResult.onStart(utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
        else
        {
            int listenerResult = tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener()
            {
                @Override
                public void onUtteranceCompleted(String utteranceId)
                {
                    callWithResult.onDone(utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }

    protected void onStart(String utteranceId) {
        Log.d(TAG, "onStart()");
        
    }

    protected void onError(String utteranceId) {
        Log.d(TAG, "onError()");
        cancelTalk();
        
    }

    protected void onDone(String utteranceId) {
        Log.d(TAG, "onDone()");
        cancelTalk();
    }

    private String getSpeakingText() {
        if (getSelectedQuote() == null) {
            return null;
        }
        return PreferenceUtils.getReadSloka(getActivity()) ? getResources()
                .getString(R.string.sloka)
                + getSelectedQuote().getSlokaEnglish()
                + getResources().getString(R.string.translation)
                + getSelectedQuote().getBody() : getSelectedQuote().getBody();
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
            cancelTalk(false);
            updatePlayPauseView(Constants.ACTION_SHOW_PAUSE);
//            if(settingsChangeListener != null)
//                settingsChangeListener.onSettingsChanged(SettingsItem.SHOW_PLAY);
            if (startNow) {
                timeoutHandler.post(swipeImageAnimRunnable);
            } else {
                timeoutHandler.postDelayed(swipeImageAnimRunnable,
                        Constants.ANIM_START_AT_DELAY);
            }

        } catch (Exception ex) {
            Log.w(TAG, "Problem in scheduling talk :" + ex);
//            playPause.setSelected(false);
            updatePlayPauseView(Constants.ACTION_SHOW_PLAY);
//            if(settingsChangeListener != null)
//                settingsChangeListener.onSettingsChanged(SettingsItem.SHOW_PAUSE);
        }
    }
    
    private void cancelTalk() {
        cancelTalk(true);
    }

    private void cancelTalk(boolean updateIcon) {
        try {
            timeoutHandler.sendEmptyMessage(STOP_TALK);
            timeoutHandler.removeCallbacks(swipeImageAnimRunnable);
            if(updateIcon){
            updatePlayPauseView(Constants.ACTION_SHOW_PLAY);
            }
        } catch (Exception ex) {
            Log.w(TAG, "Problem in canceling talk :" + ex);
        }
    }



    @Override
    public void onInit(int status) {
        setTtsCompletionListener(ttobj);
        if ((status != TextToSpeech.ERROR && !ttobj
                .isSpeaking())) {
            Log.d(TAG, "talking");
            ttobj.setSpeechRate(0.75f);
            ttobj.setLanguage(Locale.UK);
        }
    }
    
    
    
    public void updatePlayPauseView(String action){
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast( new Intent(action));
        /*QuoteBodyFragment fragment = findFragmentByPosition(position);
        if(fragment != null){
            fragment.updatePlayPauseView(state);
        }*/
        
    }
    
}
