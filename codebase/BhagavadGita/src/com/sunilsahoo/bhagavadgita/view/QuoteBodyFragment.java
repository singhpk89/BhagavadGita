package com.sunilsahoo.bhagavadgita.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import com.sunilsahoo.bhagavadgita.OnSettingsChangeListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Constants.SettingsItem;
import com.sunilsahoo.bhagavadgita.utils.Log;
import com.sunilsahoo.bhagavadgita.utils.Utility;

/**
 * @author sunilsahoo
 * 
 */
@SuppressLint("NewApi")
public class QuoteBodyFragment extends Fragment implements OnClickListener, OnLongClickListener{

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
    private View rootView;
    private String lastquoteOfChapter = "";
    private OnSettingsChangeListener settingsChangeListener = null;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            settingsChangeListener = (OnSettingsChangeListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

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
        settingsChangeListener.onSettingsChanged(SettingsItem.PAUSE);
        unregisterReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        settingsChangeListener.onSettingsChanged(SettingsItem.PAUSE);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
        if(PreferenceUtils.enableTalk(getActivity())){
        settingsChangeListener.onSettingsChanged(SettingsItem.PLAY);
        }
//        processAutoTalk(false);
//        settingsChangeListener.onSettingsChanged(SettingsItem.PLAY);
    }



    @Override
    public void onClick(View v) {
        Log.d(TAG, "click :"+playPause+" "+playPause.isSelected());
        if(v == playPause){
            if(playPause.isSelected()){
                settingsChangeListener.onSettingsChanged(SettingsItem.PAUSE);
            }else{
                settingsChangeListener.onSettingsChanged(SettingsItem.PLAY);
//                startTalk(true);
            }
        }
        
    }
    
    /*public void updatePlayPauseView(int state){
        Log.d(TAG, state+" inside updatePlayPauseView : "+(state == SettingsItem.PLAY)+" playPause :"+playPause);
        if(playPause != null)
            playPause.setImageResource(R.drawable.ic_launcher);
    }*/
    
    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SHOW_PAUSE);
        filter.addAction(Constants.ACTION_SHOW_PLAY);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, filter);
    }
    
    private void unregisterReceiver(){
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mMessageReceiver);
    }
    
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getAction();
//            Log.d(TAG, message.equals(Constants.ACTION_SHOW_PAUSE)+" Got message: " + message);
            playPause.setSelected(message.equals(Constants.ACTION_SHOW_PAUSE));
        }
    };
    

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
