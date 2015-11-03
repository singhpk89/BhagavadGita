package com.sunilsahoo.bhagavadgita.view;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunilsahoo.bhagavadgita.GitaFragment;
import com.sunilsahoo.bhagavadgita.OnFragmentResult;
import com.sunilsahoo.bhagavadgita.OnItemSelectionListener;
import com.sunilsahoo.bhagavadgita.OnMenuDrawerItemSelectionListener;
import com.sunilsahoo.bhagavadgita.R;
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
public class QuoteDetailFragment extends GitaFragment implements
        OnItemSelectionListener, OnMenuDrawerItemSelectionListener,
        OnClickListener, OnPageChangeListener{

    protected static final String TAG = "QuoteView";

    private RelativeLayout parentView;
    private ViewPager mPager;

    private View rootView;
    private ImageButton moreIB = null;
    private ChapterQuoteSpinnerView chapterQuoteSpinnerView = new ChapterQuoteSpinnerView();

    private String fragmentType = null;
    private QuotePageStateAdapter mPageAdapter = null;
    private ArrayList<Item> quoteList = null;
    private int selectedQuoteId = 0;
    private int selectedPosition = 0;

    private TextView pageTV1 = null;
    private TextView pageTV2 = null;
    private TextView pageTV3 = null;
    private boolean mIsManual = false;
    private static final int HIDE_SWIPE_VIEW = 2;
    private static final int SHOW_SWIPE_VIEW = 1;
    private TextToSpeech ttobj = null;
    private int previousState = ViewPager.SCROLL_STATE_IDLE;
    private boolean userScrollChange = false;
    private OnFragmentResult onFragmentClosedListener = null;
    private boolean isFavChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.quote_preview, container, false);
        initView(rootView);
        return rootView;
    }

    void initView(View rootView) {
        Log.d(TAG, "on initView");
        chapterQuoteSpinnerView.initialize(getActivity(), rootView, this);

        pageTV1 = (TextView) rootView.findViewById(R.id.pageTV1);
        pageTV2 = (TextView) rootView.findViewById(R.id.pageTV2);
        pageTV3 = (TextView) rootView.findViewById(R.id.pageTV3);
        
        Bundle b = getArguments();
        try{
        onFragmentClosedListener = (OnFragmentResult) b.getSerializable(Constants.FAG_CLOSED_LISTENER_INSTANCE);
        }catch(Exception ex){}
        fragmentType = b.getString(Constants.FRAG_TYPE);
        quoteList = (ArrayList<Item>) b.getSerializable(Constants.QUOTE_LIST);
        reArrangeItems();

        selectedQuoteId = b.getInt(Constants.SELECTED_QUOTE);
        selectedPosition = selectedPosition(quoteList, selectedQuoteId);
        chapterQuoteSpinnerView.enableQuoteSpinner(false);
        chapterQuoteSpinnerView.enableChapterSpinner(false);
        if ((Constants.FRAG_CHAPTER_QUOTE_DETAIL.equals(fragmentType))) {
            chapterQuoteSpinnerView.enableQuoteSpinner(true);
        }
        if ((Constants.FRAG_QUOTE_DETAIL.equals(fragmentType))) {
            chapterQuoteSpinnerView.enableQuoteSpinner(true);
            chapterQuoteSpinnerView.enableChapterSpinner(true);
        }

        if (quoteList == null) {
            Utility.closeCurrentFragment(getActivity());
        }

        parentView = (RelativeLayout) rootView
                .findViewById(R.id.qp_main_parent);
        moreIB = (ImageButton) rootView.findViewById(R.id.actionbar_moreBtn);
        moreIB.setOnClickListener(this);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        perfrormPageSelectedtask(selectedPosition, false);
        mPager.setOnPageChangeListener(this);
        intializeFooter();
        initializePager();

        chapterQuoteSpinnerView.setQuoteSelection(((Quote) quoteList
                .get(selectedPosition)).getTextId());
        Utility.updateBackgroundImage(parentView, getActivity());
    }

    private void initializePager() {
        mPageAdapter = new QuotePageStateAdapter(getActivity()
                .getSupportFragmentManager(), fragmentType, quoteList);
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(selectedPosition);

    }

    @Override
    public void onItemSelected() {
        Log.d(TAG, "inside onItemSelected");
        int prevSelectedChapter = getSelectedQuote().getChapterNo();
        int chapterNo = PreferenceUtils.getSelectedChapter(getActivity());

        if ((prevSelectedChapter != chapterNo)
                && !Constants.FRAG_QUOTE_DETAIL.equals(fragmentType)) {
            quoteList = GitaDBOperation.getItemsOf(chapterNo, getActivity());
            initializePager();
        }
        String quoteText = "1";
        try{
            quoteText = chapterQuoteSpinnerView.getQuoteSpinner()
                .getSelectedItem().toString();
        }catch(Exception ex){
            Log.w(TAG, "no selected quote");
        }
        try {
            selectedPosition = getSelectedQuoteIndex(quoteList, chapterNo,
                    quoteText);
            mPager.setCurrentItem(selectedPosition);
        } catch (Exception ex) {
        }

    }

    @Override
    public void onMenuDrawerItemSelected(int position) {
        if (getSelectedQuote() == null) {
            Log.w(TAG, "Quote is null");
            return;
        }
        switch (position) {
        case 0:
            // Share
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getCopyText());
            Intent intent = Intent.createChooser(share, getActivity()
                    .getResources().getString(R.string.share_title));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
            break;
        case 1:
            // Copy
            setClipboard(getActivity(), getCopyText());
            break;
        case 2:
            // add to favorite
            isFavChanged = true;
            updateFavourite(getSelectedQuote());
            break;
        case 3:
            // qod
            setQOD();
            break;
        case 4:
            // read
            startTalk(true);
            break;

        default:
            break;
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
            boolean isAutoTalkOn = PreferenceUtils.enableTalk(getActivity());
            if (isAutoTalkOn) {
                startTalk(true);
            } else {
                cancelTalk();
            }
            break;
        default:
            Log.d(TAG, "item Type unknown");
            break;
        }
    }

    private void setQOD() {
        PreferenceUtils.setQODID(getActivity(), getSelectedQuote().getId(),
                System.currentTimeMillis());
        Toast.makeText(getActivity(),
                getResources().getString(R.string.update_qod),
                Toast.LENGTH_SHORT).show();
    }

    private void updateFavourite(Quote quote) {
        if (quote.isFavourite() == 1) {
            quote.setFavourite(0);
            GitaDBOperation.deleteFavourites(quote.getId(), getActivity());
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.remove_fav),
                    Toast.LENGTH_SHORT).show();
        } else {
            quote.setFavourite(1);
            GitaDBOperation.addFavourites(quote.getId(), getActivity());
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.add_fav),
                    Toast.LENGTH_SHORT).show();
        }
        // update menu settings
        SettingsMenuDrawer.updatemenuDrawer(2,
                getResources().getStringArray(getMenuDrawerId())[2]);
    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(
                getResources().getString(R.string.copy_title), text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(),
                getResources().getString(R.string.msg_copy), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClick(View view) {
        int topMargin = ((((View) view.getParent()).getTop() + ((View) view
                .getParent()).getHeight()))
                - (view.getTop() + view.getHeight());
        SettingsMenuDrawer.showMenuDrawer(getActivity(), this, view, 0,
                topMargin, getMenuDrawerId());
    }

    private int getMenuDrawerId() {
        return ((getSelectedQuote() != null) && (getSelectedQuote()
                .isFavourite() == 1)) ? R.array.item_options2
                : R.array.item_options1;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.d(TAG, "onPageScrollStateChanged "+state);
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
//        Log.d(TAG, "onPageSelected "+arg0);
        perfrormPageSelectedtask(arg0, userScrollChange);
        
    }

    private void perfrormPageSelectedtask(int arg0, boolean fromScroll) {
        Log.d(TAG, "perfrormPageSelectedtask :"+fromScroll+" selectedPosition : "+arg0);
        selectedPosition = arg0;
        selectpage(selectedPosition);
        startTalk(false);

        if (Constants.FRAG_QUOTE_DETAIL.equals(fragmentType)
                || Constants.FRAG_CHAPTER_QUOTE_DETAIL.equals(fragmentType)) {
            int prevSeleChapter = PreferenceUtils
                    .getSelectedChapter(getActivity());
//            if (fromScroll || (prevSeleChapter != Constants.EOF)) {
                PreferenceUtils.setSelectedChapter(getActivity(),
                        getSelectedQuote().getChapterNo());
//            }
            chapterQuoteSpinnerView.setChapterSelection(PreferenceUtils
                    .getSelectedChapter(getActivity()));
            if (prevSeleChapter != getSelectedQuote().getChapterNo()) {
                chapterQuoteSpinnerView.updateQuoteSpinner();
            }
            chapterQuoteSpinnerView.setQuoteSelection(getSelectedQuote()
                    .getTextId());
        } else {
            chapterQuoteSpinnerView.setChapterSelection(getSelectedQuote()
                    .getChapterNo());
            chapterQuoteSpinnerView.setQuoteSelection(getSelectedQuote()
                    .getTextId());
        }
        if (Constants.FRAG_QUOTE_DETAIL.equals(fragmentType)) {
            PreferenceUtils.setLastReadQuoteID(getActivity(),
                    getSelectedQuote().getId());
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
        startTalk(false);
    }

    private void stopTalkResource() {
        try {
            if (ttobj != null) {
                Log.d(TAG, "stop talk");
                ttobj.stop();
                ttobj.shutdown();
                ttobj = null;
            }
        } catch (Exception ex) {

        }
    }

    private void initializeTalkResource(boolean manual) {
        try {
//            Log.d(TAG, "initialize talk");
            boolean startSpeak = manual
                    || PreferenceUtils.enableTalk(getActivity());
            stopTalkResource();
            if (!startSpeak) {
                return;
            }
            ttobj = new TextToSpeech(getActivity(),
                    new TextToSpeech.OnInitListener() {

                        @Override
                        public void onInit(int status) {
//                            Log.d(TAG, "start talk");
                            String speakingText = getSpeakingText();
                            if ((status != TextToSpeech.ERROR && !ttobj
                                    .isSpeaking()) && (speakingText != null)) {
                                Log.d(TAG, "talking");
                                ttobj.setSpeechRate(0.85f);
                                ttobj.setLanguage(Locale.US);
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
            case HIDE_SWIPE_VIEW:
                stopTalkResource();
                break;

            case SHOW_SWIPE_VIEW:
                initializeTalkResource(mIsManual);
                break;
            }
        }
    };

    Runnable swipeImageAnimRunnable = new Runnable() {
        public void run() {
            timeoutHandler.sendEmptyMessage(SHOW_SWIPE_VIEW);
        }
    };

    private void startTalk(boolean startNow) {
        try {
            Log.d(TAG, "inside startTalk : "+mIsManual);
            mIsManual = startNow;
            cancelTalk();
            if (startNow) {
                timeoutHandler.post(swipeImageAnimRunnable);
            } else {
                timeoutHandler.postDelayed(swipeImageAnimRunnable,
                        Constants.ANIM_START_AT_DELAY);
            }

        } catch (Exception ex) {
            Log.w(TAG, "Problem in scheduling talk :" + ex);
        }
    }

    private void cancelTalk() {
        try {
            timeoutHandler.sendEmptyMessage(HIDE_SWIPE_VIEW);
            timeoutHandler.removeCallbacks(swipeImageAnimRunnable);
        } catch (Exception ex) {
            Log.w(TAG, "Problem in canceling talk :" + ex);
        }
    }

    private String getCopyText() {
        if (getSelectedQuote() == null) {
            return null;
        }
        String chapterNoText = getString(R.string.app_name)+" - "+((getSelectedQuote().getChapterNo() > 0) ? "Chapter "+ getSelectedQuote().getChapterNo() + "(Verse "
                + getSelectedQuote().getTextId()
                + ")": Constants.SPINNER_CHAPTER_INTRODUCTION);
        return (PreferenceUtils.getShowSloka(getActivity())
                && (getSelectedQuote().getChapterNo() > 0) ? chapterNoText
                + " : "
                + getSelectedQuote().getSlokaSanskrit()
                + "\n"
                + getSelectedQuote().getBody() : chapterNoText
                + " : " + getSelectedQuote().getBody())
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

    private int getSelectedQuoteIndex(ArrayList<Item> quoteList, int chapterId,
            String quoteTxt) {
        int index = Constants.EOF;

        for (int i = 0; i < quoteList.size(); i++) {
            if ((((Quote) quoteList.get(i)).getChapterNo() == chapterId)
                    && ((Quote) quoteList.get(i)).getTextId().equals(quoteTxt)) {
                index = i;
                break;
            }
        }
        if(index == Constants.EOF){
            for (int i = 0; i < quoteList.size(); i++) {
                if ((((Quote) quoteList.get(i)).getChapterNo() == chapterId)) {
                    index = i;
                    break;
                }
            }
        }
        if(index == Constants.EOF){
            index = 0;
        }
        Log.d(TAG, "selected postion :"+index+" chapterId :"+chapterId+" quote text :"+quoteTxt);
        return index;
    }

    private void intializeFooter() {
        int count = quoteList.size();
        if ((quoteList != null) && !quoteList.isEmpty()) {
            if (count == 1) {
                pageTV2.setVisibility(View.GONE);
                pageTV3.setVisibility(View.GONE);
            } else if (count == 2) {
                pageTV3.setVisibility(View.GONE);
            }
        } else {
            pageTV1.setVisibility(View.GONE);
            pageTV2.setVisibility(View.GONE);
            pageTV3.setVisibility(View.GONE);
        }
        selectpage(selectedPosition);
    }

    private void selectpage(int position) {
        try {
            int count = quoteList.size();
            pageTV1.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.page_dot, 0, 0);
            pageTV2.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.page_dot, 0, 0);
            pageTV3.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.page_dot, 0, 0);
            pageTV1.setTextColor(getResources().getColor(R.color.grey));
            pageTV2.setTextColor(getResources().getColor(R.color.grey));
            pageTV3.setTextColor(getResources().getColor(R.color.grey));
            if (position == 0) {
                pageTV1.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.page_shown, 0, 0);
                pageTV1.setTextColor(getResources().getColor(
                        R.color.darkestgray));
                pageTV1.setText("" + (position + 1));
                pageTV2.setText("" + (position + 2));
                pageTV3.setText("" + (position + 3));
            } else if (position == count - 1) {
                if (count >= 3) {
                    pageTV3.setCompoundDrawablesWithIntrinsicBounds(0,
                            R.drawable.page_shown, 0, 0);
                    pageTV3.setTextColor(getResources().getColor(
                            R.color.darkestgray));
                    pageTV1.setText("" + (position - 1));
                    pageTV2.setText("" + position);
                    pageTV3.setText("" + (position + 1));
                } else if (count == 2) {
                    pageTV2.setCompoundDrawablesWithIntrinsicBounds(0,
                            R.drawable.page_shown, 0, 0);
                    pageTV2.setTextColor(getResources().getColor(
                            R.color.darkestgray));
                    pageTV1.setText("" + position);
                    pageTV2.setText("" + (position + 1));
                }
            } else {
                pageTV2.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.page_shown, 0, 0);
                pageTV2.setTextColor(getResources().getColor(
                        R.color.darkestgray));
                pageTV1.setText("" + position);
                pageTV2.setText("" + (position + 1));
                pageTV3.setText("" + (position + 2));
            }
        } catch (Exception ex) {
            Log.w(TAG, "exception ex :" + ex);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
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
                onFragmentClosedListener.onFragmentCallback(isFavChanged? Constants.FragmentCallbackAction.UPDATE_ALL :
                        Constants.FragmentCallbackAction.UPDATE_UI, b);
            } else if (Constants.FRAG_FAV_QUOTE_DETAIL.equals(fragmentType)) {
                onFragmentClosedListener.onFragmentCallback(
                        Constants.FragmentCallbackAction.UPDATE_DATA, null);
            }

        }
    }

}
