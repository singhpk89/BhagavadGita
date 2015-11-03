package com.sunilsahoo.bhagavadgita.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class PreferenceUtils {
    private static final String FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH";
    private static final String TOTAL_QUOTES = "total_quote";
    private static final String IS_DAY_MODE = "is_day_mode";
    private static final String ENABLE_TALK_BYDEFAULT = "enable_talk_default";
    private static final String SHOW_SLOKA = "show_sloka";
    private static final String READ_SLOKA = "read_sloka";
    private static final String SHOW_NOTICE = "showNotice";
    private static final String KEY_QUOTE_FONT_COLOR = "quote_font_color";
    private static final String KEY_FONT_SIZE = "quote_font_size";
    private static final int KEY_FONT_SIZE_DEFAULT = 15;
    private static final String DB_VERSION = "db_version";
    private static final String SELECTED_CHAPTER = "sel_ch";
    private static final String SELECTED_QUOTE_TEXT = "sel_quote_text";
    private static final String SELECTED_QUOTE_TEXT_DEFAULT = "1";
    private static final String QOD = "qod";
    private static final String QOD_TIME = "qod_time";
    private static final String LAST_READ_QUOTE = "last_read_quote";
    
    public static boolean isFirstTimeLaunch(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(FIRST_TIME_LAUNCH, true);
    }

    public static void updateFirstTimeLaunch(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(FIRST_TIME_LAUNCH, false);
        prefEditor.commit();
    }
    
    public static String getSelectedQuoteText(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getString(SELECTED_QUOTE_TEXT, SELECTED_QUOTE_TEXT_DEFAULT);
    }

    public static void setSelectedQuoteText(Context context, String quoteText) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putString(SELECTED_QUOTE_TEXT, quoteText);
        prefEditor.commit();
    }
    
    public static void resetSelectedQuoteText(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putString(SELECTED_QUOTE_TEXT, SELECTED_QUOTE_TEXT_DEFAULT);
        prefEditor.commit();
    }
    
    
    public static int getSelectedChapter(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(SELECTED_CHAPTER, 0);
    }

    public static void setSelectedChapter(Context context, int chapter) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(SELECTED_CHAPTER, chapter);
        prefEditor.commit();
    }
    
    public static int getDBVersion(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(DB_VERSION, 1);
    }

    public static void setDBVersion(Context context, int version) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(DB_VERSION, version);
        prefEditor.commit();
    }
    
    public static boolean isShowNotice(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(SHOW_NOTICE, false);
    }

    public static void setShowNotice(Context context, boolean show) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(SHOW_NOTICE, show);
        prefEditor.commit();
    }
    
    public static int getTotalNoOfQuotes(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(TOTAL_QUOTES, 0);
    }

    public static void setTotalNoOfQuotes(Context context, int noOfQuotes) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(TOTAL_QUOTES, noOfQuotes);
        prefEditor.commit();
    }
    
    public static boolean enableTalk(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(ENABLE_TALK_BYDEFAULT, true);
    }

    public static void setEnableTalk(Context context, boolean enable) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(ENABLE_TALK_BYDEFAULT, enable);
        prefEditor.commit();
    }
    
    public static boolean getShowSloka(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(SHOW_SLOKA, true);
    }

    public static void setShowSloka(Context context, boolean show) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(SHOW_SLOKA, show);
        prefEditor.commit();
    }
    
    
    public static boolean getReadSloka(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(READ_SLOKA, false);
    }

    public static void setReadSloka(Context context, boolean show) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(READ_SLOKA, show);
        prefEditor.commit();
    }
    
    public static boolean getDayMode(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getBoolean(IS_DAY_MODE, true);
    }

    public static void setReadMode(Context context, boolean isDay) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putBoolean(IS_DAY_MODE, isDay);
        prefEditor.commit();
    }
    
    public static int getFontSize(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(KEY_FONT_SIZE, KEY_FONT_SIZE_DEFAULT);
    }

    public static void setFontSize(Context context, int size) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(KEY_FONT_SIZE, size);
        prefEditor.commit();
    }
    
    public static int getFontColor(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(KEY_QUOTE_FONT_COLOR, 0);
    }

    public static void setFontColor(Context context, int color) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(KEY_QUOTE_FONT_COLOR, color);
        prefEditor.commit();
    }
    
    public static int getQODID(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(QOD, 1);
    }
    
    public static long getQODTime(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getLong(QOD_TIME, 0);
    }
    

    public static void setQODID(Context context, int qodID, long time) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(QOD, qodID);
        prefEditor.putLong(QOD_TIME, time);
        prefEditor.commit();
    }

    public static int getLastReadQuoteID(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getInt(LAST_READ_QUOTE, 1);
    }

    public static void setLastReadQuoteID(Context context, int qodID) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor prefEditor = pref.edit();
        prefEditor.putInt(LAST_READ_QUOTE, qodID);
        prefEditor.commit();
    }
}
