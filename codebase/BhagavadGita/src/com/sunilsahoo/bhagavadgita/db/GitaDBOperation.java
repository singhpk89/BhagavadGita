package com.sunilsahoo.bhagavadgita.db;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sunilsahoo.bhagavadgita.beans.Chapter;
import com.sunilsahoo.bhagavadgita.beans.Item;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.GitaDBProviderMetaData.ChaptersTable;
import com.sunilsahoo.bhagavadgita.db.GitaDBProviderMetaData.QuotesTable;
import com.sunilsahoo.bhagavadgita.utils.Constants;

public class GitaDBOperation {

    private static final String TAG = GitaDBOperation.class.getName();

    /**
     * @param QuoteID
     * @return
     */
    public static boolean addFavourites(long QuoteID, Context context) {
        try {
            ContentValues cv = new ContentValues();
            String selection = QuotesTable._ID + " = " + QuoteID;
            cv.put(QuotesTable.IS_FAVORITE, 1);
            context.getContentResolver().update(
                    GitaDBProviderMetaData.QuotesTable.CONTENT_URI, cv,
                    selection, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * @param QuoteID
     * @return
     */
    public static boolean deleteFavourites(long QuoteID, Context context) {
        try {
            ContentValues cv = new ContentValues();
            String selection = QuotesTable._ID + " = " + QuoteID;
            cv.put(QuotesTable.IS_FAVORITE, 0);
            context.getContentResolver().update(
                    GitaDBProviderMetaData.QuotesTable.CONTENT_URI, cv,
                    selection, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * @param QuoteID
     * @return
     */
    public static boolean deleteAllFavourites(Context context) {
        try {
            ContentValues cv = new ContentValues();
            String selection = QuotesTable.IS_FAVORITE + " = " + 1;
            cv.put(QuotesTable.IS_FAVORITE, 0);
            context.getContentResolver().update(
                    GitaDBProviderMetaData.QuotesTable.CONTENT_URI, cv,
                    selection, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * GET QUOTE COUNT
     * 
     * @return
     */
    public static int getTotalQuotesNoFilter(Context context) {

        Cursor cursor = null;
        int salesCount = 0;
        try {
            cursor = context.getContentResolver().query(
                    QuotesTable.CONTENT_URI,
                    new String[] { "count(*) AS count" }, null, null, null);
            cursor.moveToFirst();
            salesCount = cursor.getInt(0);
        } catch (Exception ex) {
            Log.e(TAG, "Error in getting sales count :" + ex.getMessage());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                }
            }
            cursor = null;
        }
        return salesCount;
    }

    public static int getChapterCount(Context context) {

        Cursor cursor = null;
        int salesCount = 0;
        try {
            cursor = context.getContentResolver().query(
                    ChaptersTable.CONTENT_URI,
                    new String[] { "count(*) AS count" }, null, null, null);
            cursor.moveToFirst();
            salesCount = cursor.getInt(0);
        } catch (Exception ex) {
            Log.e(TAG, "Error in getting sales count :" + ex.getMessage());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                }
            }
            cursor = null;
        }
        return salesCount;
    }

    public static int getQuotesCountOf(int chapter, Context context) {

        Cursor cursor = null;
        int salesCount = 0;
        try {
            String whereClause = null;
            if(chapter != Constants.EOF){
            whereClause = QuotesTable.CHAPTER_NO + " = " + chapter;
            }
            cursor = context.getContentResolver().query(
                    QuotesTable.CONTENT_URI,
                    new String[] { "count(*) AS count" }, whereClause, null,
                    null);
            cursor.moveToFirst();
            salesCount = cursor.getInt(0);
        } catch (Exception ex) {
            Log.e(TAG, "Error in getting sales count :" + ex.getMessage());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                }
            }
            cursor = null;
        }
        return salesCount;
    }

    /**
     * get Quote list by limit
     * 
     * @param start
     * @param limit
     * @param order
     * @return
     */
        
    public static ArrayList<Item> getItemsOf(int chapterNo, Context context) {
        ArrayList<Item> dataList = new ArrayList<Item>();
        String selection = null;
        String orderBy;
        if(chapterNo != Constants.EOF){
            selection = QuotesTable.CHAPTER_NO + " = " + chapterNo;
        }
        orderBy = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " ASC ";

        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, selection, null, orderBy);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }
        cursor.moveToFirst();
        do {
            dataList.add(retrieveQuote(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return dataList;
    }
    
    public static ArrayList<Item> getQuotesWOChapter(int chapterNo, Context context) {
        ArrayList<Item> dataList = new ArrayList<Item>();
        String selection = null;
        String orderBy;
        if(chapterNo != Constants.EOF){
            selection = QuotesTable.CHAPTER_NO + " = " + chapterNo;
        }
        orderBy = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " ASC ";

        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, selection, null, orderBy);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }
        cursor.moveToFirst();
        do {

            dataList.add( retrieveQuote(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return dataList;
    }
    
    public static ArrayList<Item> getItemsOf(String matchText, Context context) {
        ArrayList<Item> dataList = new ArrayList<Item>();
        int prevChapterId = Constants.EOF;
        String selection = null;
        if(matchText != null){
        selection = QuotesTable.BODY + " like  '%" + matchText + "%'"+" OR "+QuotesTable.SLOKA_SANSKRIT + " like  '%" + matchText + "%'";
        }
        String orderBy = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " ASC ";

        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, selection, null, orderBy);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }
        cursor.moveToFirst();
        do {

            Quote entity = retrieveQuote(cursor);
            if (prevChapterId != entity.getChapterNo()) {
                dataList.add(new Chapter(entity.getChapterNo(), entity
                        .getChapterTitle()));
                prevChapterId = entity.getChapterNo();
            }

            dataList.add(entity);
        } while (cursor.moveToNext());
        cursor.close();
        return dataList;
    }
    
    
    public static ArrayList<Quote> getQuotesOf(int chapterNo, Context context) {
        ArrayList<Quote> dataList = new ArrayList<Quote>();
        String selection = null;
        String orderBy;
        if(chapterNo != Constants.EOF){
            selection = QuotesTable.CHAPTER_NO + " = " + chapterNo;
        }
        orderBy = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " ASC ";

        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, selection, null, orderBy);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }
        cursor.moveToFirst();
        do {

            Quote entity = retrieveQuote(cursor);
            dataList.add(entity);
        } while (cursor.moveToNext());
        cursor.close();
        return dataList;
    }
    
    public static Quote getQuoteOf(Quote quote, Context context) {
        String whereClause;
        if(quote.getChapterNo() != Constants.EOF){
            whereClause = QuotesTable.CHAPTER_NO + " = " + quote.getChapterNo() +" AND "+QuotesTable.TEXT_NO+" = '"+quote.getTextId()+"'";
        }else{
            whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                    + " = " + quote.getId();
        }
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            quote = null;
        } else {
            cursor.moveToFirst();
            do {
                quote = retrieveQuote(cursor);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return quote;
    }

    public static ArrayList<Item> getChapters(Context context) {
        ArrayList<Item> chapterList = new ArrayList<Item>();

        Cursor cursor = context.getContentResolver().query(
                ChaptersTable.CONTENT_URI,
                new String[] { ChaptersTable._ID, ChaptersTable.TITLE }, null,
                null, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return chapterList;
        }
        cursor.moveToFirst();
        do {
            Chapter chapter = new Chapter(
                    Integer.parseInt(cursor.getString(0)), cursor.getString(1));
            chapterList.add(chapter);
        } while (cursor.moveToNext());
        cursor.close();
        return chapterList;
    }

    /**
     * get Quote list by fav
     * 
     * @param authorid
     * @return
     */
    public static ArrayList<Item> getQuoteByFav(Context context) {
        ArrayList<Item> dataList = new ArrayList<Item>();
        int prevChapterId = Constants.EOF;
        String whereClause = QuotesTable.IS_FAVORITE + " = 1";
        String sortOrder = QuotesTable.CHAPTER_NO + " ASC ";
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, sortOrder);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return dataList;
        }
        cursor.moveToFirst();
        do {
            Quote entity = retrieveQuote(cursor);
            if (prevChapterId != entity.getChapterNo()) {
                dataList.add(new Chapter(entity.getChapterNo(), entity
                        .getChapterTitle()));
                prevChapterId = entity.getChapterNo();
            }
            dataList.add(entity);
        } while (cursor.moveToNext());
        cursor.close();
        return dataList;
    }
    
    
    public static Quote getNextQuoteByFav(int quoteID, Context context) {
        Quote entity = null;
        
        String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                + " > " + quoteID+" AND "+QuotesTable.IS_FAVORITE + " = 1";
        String sortOrder = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " ASC LIMIT 1";
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, sortOrder);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        do {
            entity = retrieveQuote(cursor);
        } while (cursor.moveToNext());
        cursor.close();
        return entity;
    }
    
    public static Quote getPreviousQuoteByFav(int quoteID, Context context) {
        Quote entity = null;
        
        String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                + " < " + quoteID+" AND "+QuotesTable.IS_FAVORITE + " = 1";
        String sortOrder = QuotesTable.TABLE_NAME + "." + QuotesTable._ID + " DESC LIMIT 1";
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, sortOrder);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        do {
            entity = retrieveQuote(cursor);
        } while (cursor.moveToNext());
        cursor.close();
        return entity;
    }

        
    /**
     * get Joke random
     * 
     * @param total
     * @return
     */
    public static Quote getQuoteRandom(int total, Context context) {
        Quote entity = null;

        while (entity == null) {
            Random rand = new Random();
            int a = rand.nextInt(total + 1);

            String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                    + " = " + a;
            Cursor cursor = context.getContentResolver().query(
                    QuotesTable.CONTENT_URI, null, whereClause, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                cursor.close();
                entity = null;
            } else {
                cursor.moveToFirst();
                do {
                    entity = retrieveQuote(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        return entity;
    }

    /**
     * get Quote Next
     * 
     * @param quoteId
     * @return
     */
    public static Quote getNextQuote(int quoteId, Context context) {
        Quote entity = null;
        int nextQuoteId = quoteId + 1;
        String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                + " = " + nextQuoteId;
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            entity = null;
        } else {
            cursor.moveToFirst();
            do {
                entity = retrieveQuote(cursor);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return entity;
    }

    /**
     * get Quote pre
     * 
     * @param quoteId
     * @return
     */
    public static Quote getPreQuote(int quoteId, Context context) {
        Quote entity = null;
        int nextQuoteId = quoteId - 1;
        String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                + " = " + nextQuoteId;
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            entity = null;
        } else {
            cursor.moveToFirst();
            do {
                entity = retrieveQuote(cursor);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return entity;
    }

    /**
     * get Quote pre
     * 
     * @param quoteId
     * @return
     */
    public static Quote getQuoteById(int quoteId, Context context) {
        Quote entity = null;
        String whereClause = QuotesTable.TABLE_NAME + "." + QuotesTable._ID
                + " = " + quoteId;
        Cursor cursor = context.getContentResolver().query(
                QuotesTable.CONTENT_URI, null, whereClause, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            entity = null;
        } else {
            cursor.moveToFirst();
            do {
                entity = retrieveQuote(cursor);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return entity;
    }

    private static Quote retrieveQuote(Cursor cursor) {
        Quote entity = new Quote();
        entity.setChapterNo(cursor.getInt(cursor
                .getColumnIndexOrThrow(QuotesTable.CHAPTER_NO)));
        entity.setFavourite(cursor.getInt(cursor
                .getColumnIndexOrThrow(QuotesTable.IS_FAVORITE)));
        entity.setBody(cursor.getString(cursor
                .getColumnIndexOrThrow(QuotesTable.BODY)));
        entity.setTextId(cursor.getString(cursor
                .getColumnIndexOrThrow(QuotesTable.TEXT_NO)));
        
        entity.setSlokaEnglish(cursor.getString(cursor
                .getColumnIndexOrThrow(QuotesTable.SLOKA_ENGLISH)));
        entity.setSlokaSanskrit(cursor.getString(cursor
                .getColumnIndexOrThrow(QuotesTable.SLOKA_SANSKRIT)));
        
        entity.setId(cursor.getInt(cursor
                .getColumnIndexOrThrow(QuotesTable._ID)));
        entity.setChapterTitle(cursor.getString(cursor
                .getColumnIndexOrThrow(ChaptersTable.TITLE)));
        
        return entity;
    }

}
