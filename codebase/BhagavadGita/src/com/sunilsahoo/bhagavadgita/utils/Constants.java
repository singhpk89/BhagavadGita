/**
 * 
 */
package com.sunilsahoo.bhagavadgita.utils;

/**
 * 
 * 
 * @author sunilsahoo
 *
 */
public class Constants {
	public static final String SORTAZ = "ASC";
	public static final String SORTZA = "DESC";
	public static final String Bundle_quote = "Quote";
	public static final String QUOTE_LIST = "QuoteList";
	public static final String SELECTED_QUOTE = "SelectedQuote";
	public static final String SELECTED_CHAPTER = "SelectedChapter";
	public static final String SELECTED_QUOTE_TEXT = "SelectedQuoteText";
	public static final String FAG_CLOSED_LISTENER_INSTANCE = "FragCloseListenerInstance";
	public static final String Bundle_category = "Category";
	public static final String Bundle_pos = "pos";
    public static final String KEY_SELECTED_CHAPTER = "SelectedChapter";
    public static final int EOF = -1;
    public static final String FRAG_TYPE = "fragType";
    public static final String FRAG_QUOTE_DETAIL = "fragQuoteDetail";
    public static final String FRAG_SETTINGS = "fragSettings";
    public static final String FRAG_DETAIL_HOME = "fragHome";
    public static final String FRAG_QUOTES_LIST = "fragQuoteList";
    public static final String FRAG_SEARCH_QUOTES_LIST = "fragSearchQuoteList";
    public static final String FRAG_SEARCH = "fragSearch";
    public static final String FRAG_QOD = "fragQOD";
    public static final String FRAG_CHAPTERS_LIST = "fragChapterList";
    public static final String FRAG_CHAPTER_QUOTE_LIST = "fragChapterQuoteList";
    public static final String FRAG_FAVOURITE = "fragFavourite";    
    public static final String FRAG_FAV_QUOTE_DETAIL = "fragFavQuoteDetail";
    public static final String FRAG_CHAPTER_QUOTE_DETAIL = "fragChapterQuoteDetail";
    public static final String FRAG_SEARCH_QUOTE_DETAIL = "fragSearchQuoteDetail";
    
    public static final String SPINNER_CHAPTER_PREFIX = "Chapter ";
    public static final String SPINNER_CHAPTER_INTRODUCTION = "Introduction";
    public static final int ANIM_REPEAT_COUNT = 5;
    //delay of 4 sec
    public static final int ANIM_START_AT_DELAY = 3*1000;
    public static final int MAX_FONT_SIZE = 30;
    public static final int MIN_FONT_SIZE = 10;
    //splash screen fo 1 sec
    public static final long SPLASH_SCREEN_DURATION = 1*1000;
    
    
    public interface SlidingMenuItems{
        int CONTINUE_READING = 0;
        int CHAPTER = 1;
        int FAVOURITE = 2;
        int SEARCH = 3;
        int SETTINGS = 4;
        int QOD = 5;
        int INFO = 6;
        
    }
    
    public interface SettingsItem{
        int FONT_SIZE = 0;
        int READ_MODE = 1;
        int SHOW_SLOKA = 2;
        int READ_SLOKA = 3;
        int ENABLE_SPEAK = 4;
    }
    
    public interface FragmentCallbackAction{
        int UPDATE_UI = 0;
        int UPDATE_DATA = 1;
        int UPDATE_ALL = 2;
    }

}
