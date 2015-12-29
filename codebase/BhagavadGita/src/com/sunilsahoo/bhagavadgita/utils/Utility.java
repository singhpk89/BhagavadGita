package com.sunilsahoo.bhagavadgita.utils;

import java.util.Calendar;

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
                .replace(R.id.frame_container, quoteDetail, name);
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
                .replace(R.id.frame_container, quoteDetail, name);
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
        int previousSelectedChapter = GitaDBOperation.getQuoteById(PreferenceUtils.getSelectedQuoteId(context), context).getChapterNo();
        if(chapterId != previousSelectedChapter){
        PreferenceUtils.setSelectedQuoteId(context, GitaDBOperation.getFirstQuoteOfChapter(context, chapterId).getId());
        return true;
        }
        return false;
    }
    
    public static void updateBackgroundImage(ViewGroup group, Context context){
        /*if(group == null){
            return;
        }
        int bgResId = PreferenceUtils.getDayMode(context) ? R.drawable.background : R.drawable.background_black;
        group.setBackgroundResource(bgResId);*/
        //TODO
    }
    
    public static void setTextColor(TextView textView, Context context){
        /*if(textView == null){
            return;
        }
        int colorId = PreferenceUtils.getDayMode(context) ? R.color.text_black : R.color.text_white; 
        textView.setTextColor(context.getResources().getColor(colorId));*/
    }
    
    public static int getQODID(Context context){
        long time = PreferenceUtils.getQODTime(context);
        long currentTime = System.currentTimeMillis();
        boolean isSameDay = isSameDay(time, currentTime);
        if(!isSameDay){
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
    
    private static boolean isSameDay(long time1, long time2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
