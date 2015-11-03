package com.sunilsahoo.bhagavadgita.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBOperation;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.view.QuoteDetailFragment;
import com.sunilsahoo.bhagavadgita.view.QuotesListFragment;

public class Utility {
    public static void launchQuoteDetailFragment(Bundle bundle, String name,
            FragmentActivity activity) {
        Fragment quoteDetail = new QuoteDetailFragment();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.FRAG_TYPE, name);
        quoteDetail.setArguments(bundle);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .add(R.id.frame_container, quoteDetail, name);
        transaction.addToBackStack(name);
        transaction.commit();
    }
    
    public static void launchQuotesListFragment(Bundle bundle, String name,
            FragmentActivity activity) {
        Fragment quoteDetail = new QuotesListFragment();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.FRAG_TYPE, name);
        quoteDetail.setArguments(bundle);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .add(R.id.frame_container, quoteDetail, name);
        transaction.addToBackStack(name);
        transaction.commit();
    }
    
    public static void closeCurrentFragment(FragmentActivity activity){
        try{
        activity.getSupportFragmentManager().popBackStack();
        }catch(Exception ex){            
        }
    }
    
    public static boolean isChapterFragment(String fragmentType){
        return (fragmentType != null)
                && (fragmentType.equals(Constants.FRAG_CHAPTERS_LIST));
    }
    
    public static boolean updateChapterId(Context context, int chapterId){
        if(chapterId != PreferenceUtils.getSelectedChapter(context)){
        PreferenceUtils.setSelectedChapter(context, chapterId);
        PreferenceUtils.resetSelectedQuoteText(context);
        return true;
        }
        return false;
    }
    
    public static void updateBackgroundImage(ViewGroup group, Context context){
        if(group == null){
            return;
        }
        int bgResId = PreferenceUtils.getDayMode(context) ? R.drawable.background : R.drawable.background_black;
        group.setBackgroundResource(bgResId);
    }
    
    public static void setTextColor(TextView textView, Context context){
        if(textView == null){
            return;
        }
        int colorId = PreferenceUtils.getDayMode(context) ? R.color.text_black : R.color.text_white; 
        textView.setTextColor(context.getResources().getColor(colorId));
    }
    
    public static int getQODID(Context context){
        long time = PreferenceUtils.getQODTime(context);
        long currentTime = System.currentTimeMillis();
        boolean timeElapsed = Math.abs(currentTime-time) > 24*60*60*1000;
        if(timeElapsed){
            int total = PreferenceUtils.getTotalNoOfQuotes(context);
            if (total == 0) {
                total = GitaDBOperation.getTotalQuotesNoFilter(context);
                PreferenceUtils.setTotalNoOfQuotes(context, total);
            }
            Quote quote = GitaDBOperation.getQuoteRandom(total, context);
            PreferenceUtils.setQODID(context, quote.getId(), currentTime);
        }
        return PreferenceUtils.getQODID(context);
        
            
    }
}
