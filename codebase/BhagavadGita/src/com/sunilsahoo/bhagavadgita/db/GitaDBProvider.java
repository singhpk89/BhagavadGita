package com.sunilsahoo.bhagavadgita.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sunilsahoo.bhagavadgita.db.GitaDBProviderMetaData.ChaptersTable;
import com.sunilsahoo.bhagavadgita.db.GitaDBProviderMetaData.QuotesColumns;
import com.sunilsahoo.bhagavadgita.db.GitaDBProviderMetaData.QuotesTable;
import com.sunilsahoo.bhagavadgita.utils.Constants;

public class GitaDBProvider extends ContentProvider {
    private static final String TAG = GitaDBProvider.class.getName();

    private GitaDBHelper mDbHelper;
    private static final UriMatcher sUriMatcher;
    private static final int QUOTES_TYPE_LIST = 1;
    private static final int QUOTES_TYPE_ONE = 2;
    private static final int CHAPTERS_TYPE_LIST = 3;
    private static final int CHAPTERS_TYPE_ONE = 4;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(GitaDBProviderMetaData.AUTHORITY,
                QuotesTable.PATH, QUOTES_TYPE_LIST);
        sUriMatcher.addURI(GitaDBProviderMetaData.AUTHORITY,
                QuotesTable.PATH + "/#", QUOTES_TYPE_ONE);

        sUriMatcher.addURI(GitaDBProviderMetaData.AUTHORITY,
                ChaptersTable.PATH, CHAPTERS_TYPE_LIST);
        sUriMatcher.addURI(GitaDBProviderMetaData.AUTHORITY,
                ChaptersTable.PATH + "/#", CHAPTERS_TYPE_ONE);
    }

    private static final HashMap<String, String> sQuotesProjectionMap;
    private static final HashMap<String, String> sChaptersProjectionMap;

    static {
        sQuotesProjectionMap = new HashMap<String, String>();
        sQuotesProjectionMap.put(QuotesTable.TABLE_NAME+"."+QuotesTable.KEY_ROWID, QuotesTable.TABLE_NAME+"."+QuotesTable.KEY_ROWID);
//        sQuotesProjectionMap.put(QuotesColumns.KEY_ROWID, QuotesColumns.KEY_ROWID);
        sQuotesProjectionMap.put(QuotesColumns.CHAPTER_NO, QuotesColumns.CHAPTER_NO);
        sQuotesProjectionMap.put(QuotesColumns.TEXT_NO, QuotesColumns.TEXT_NO);
        sQuotesProjectionMap.put(QuotesColumns.BODY, QuotesColumns.BODY);
        sQuotesProjectionMap.put(QuotesColumns.IS_FAVORITE,
                QuotesColumns.IS_FAVORITE);
        sQuotesProjectionMap.put(QuotesColumns.SLOKA_SANSKRIT,
                QuotesColumns.SLOKA_SANSKRIT);
        sQuotesProjectionMap.put(QuotesColumns.SLOKA_ENGLISH,
                QuotesColumns.SLOKA_ENGLISH);
        sQuotesProjectionMap.put(ChaptersTable.TABLE_NAME+"."+ChaptersTable.KEY_ROWID, ChaptersTable.TABLE_NAME+"."+ChaptersTable.KEY_ROWID);
        sQuotesProjectionMap.put(ChaptersTable.TABLE_NAME+"."+ChaptersTable.TITLE, ChaptersTable.TABLE_NAME+"."+ChaptersTable.TITLE);
    }

    static {
        sChaptersProjectionMap = new HashMap<String, String>();
//        sChaptersProjectionMap.put(ChaptersTable.TABLE_NAME+"."+ChaptersTable.KEY_ROWID, ChaptersTable.TABLE_NAME+"."+ChaptersTable.KEY_ROWID);
        sChaptersProjectionMap.put(ChaptersTable.KEY_ROWID, ChaptersTable.KEY_ROWID);
        sChaptersProjectionMap.put(ChaptersTable.TITLE, ChaptersTable.TITLE);
    }

    @Override
    public boolean onCreate() {
        try {
            mDbHelper = new GitaDBHelper(getContext());
        } catch (Exception e) {
            Log.e(TAG, "Exception to create database : "+e);
        }
        return false;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
        case QUOTES_TYPE_LIST:
            count = db.delete(QuotesTable.TABLE_NAME, where, whereArgs);
            break;

        case QUOTES_TYPE_ONE:
            String rowId = uri.getPathSegments().get(1);
            count = db.delete(
                    QuotesTable.TABLE_NAME,
                    QuotesColumns.KEY_ROWID
                            + " = "
                            + rowId
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ")" : ""), whereArgs);
            break;
        case CHAPTERS_TYPE_LIST:
            count = db.delete(ChaptersTable.TABLE_NAME, where, whereArgs);
            break;
        case CHAPTERS_TYPE_ONE:
            rowId = uri.getPathSegments().get(1);
            count = db.delete(
                    ChaptersTable.TABLE_NAME,
                    ChaptersTable.KEY_ROWID
                            + " = "
                            + rowId
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ")" : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
        case QUOTES_TYPE_LIST:
            return QuotesTable.CONTENT_TYPE;
        case QUOTES_TYPE_ONE:
            return QuotesTable.CONTENT_ITEM_TYPE;
        case CHAPTERS_TYPE_LIST:
            return ChaptersTable.CONTENT_TYPE;
        case CHAPTERS_TYPE_ONE:
            return ChaptersTable.CONTENT_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
        case QUOTES_TYPE_ONE:
        case QUOTES_TYPE_LIST:
            long rowId = db.insert(QuotesTable.TABLE_NAME, null, values);
            if (rowId != Constants.EOF) {

                Uri articleUri = ContentUris.withAppendedId(
                        QuotesTable.CONTENT_URI, rowId);
                Log.i(TAG, "inserted successfully row id :" + rowId + " Uri :"
                        + articleUri);
                getContext().getContentResolver()
                        .notifyChange(articleUri, null);
                return articleUri;
            }
            break;

        case CHAPTERS_TYPE_ONE:
        case CHAPTERS_TYPE_LIST:
            rowId = db.insert(ChaptersTable.TABLE_NAME, null, values);
            if (rowId != Constants.EOF) {

                Uri articleUri = ContentUris.withAppendedId(
                        ChaptersTable.CONTENT_URI, rowId);
                Log.i(TAG, "inserted successfully row id :" + rowId + " Uri :"
                        + articleUri);
                getContext().getContentResolver()
                        .notifyChange(articleUri, null);
                return articleUri;
            }
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI : " + uri);

        }
        // close(db);
        throw new IllegalArgumentException("Exception while inserting : " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {

        case QUOTES_TYPE_LIST:
            StringBuilder itemJoin = new StringBuilder();
            itemJoin.append(QuotesTable.TABLE_NAME).append(" LEFT OUTER JOIN ").append(ChaptersTable.TABLE_NAME).append(" ON ")
            .append("(").append(QuotesTable.TABLE_NAME).append(".").append(QuotesTable.CHAPTER_NO)
            .append("=").append(ChaptersTable.TABLE_NAME).append(".").append(ChaptersTable._ID).append(")");
//          Log.i(TAG, "Table name :"+itemJoin.toString());
            builder.setProjectionMap(sQuotesProjectionMap);
            builder.setTables(itemJoin.toString());

            break;

        case QUOTES_TYPE_ONE:
            itemJoin = new StringBuilder();
            itemJoin.append(QuotesTable.TABLE_NAME).append(" LEFT OUTER JOIN ").append(ChaptersTable.TABLE_NAME).append(" ON ")
            .append("(").append(QuotesTable.TABLE_NAME).append(".").append(QuotesTable.CHAPTER_NO)
            .append("=").append(ChaptersTable.TABLE_NAME).append(".").append(ChaptersTable._ID).append(")");
//          Log.i(TAG, "Table name :"+itemJoin.toString());
            builder.setProjectionMap(sQuotesProjectionMap);
            builder.setTables(itemJoin.toString());

            break;

        case CHAPTERS_TYPE_LIST:
            builder.setProjectionMap(sChaptersProjectionMap);
            builder.setTables(ChaptersTable.TABLE_NAME);

            break;

        case CHAPTERS_TYPE_ONE:
            builder.setProjectionMap(sChaptersProjectionMap);
            builder.setTables(ChaptersTable.TABLE_NAME);

            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;

        switch (sUriMatcher.match(uri)) {

        case QUOTES_TYPE_LIST:
            count = db.update(QuotesTable.TABLE_NAME, values, where,
                    whereArgs);
            break;

        case QUOTES_TYPE_ONE:
            String rowIdtask = uri.getPathSegments().get(1);
            count = db.update(QuotesTable.TABLE_NAME, values, "_id" + " = "
                    + rowIdtask
                    + (!TextUtils.isEmpty(where) ? " AND (" + ")" : ""),
                    whereArgs);
            break;

        case CHAPTERS_TYPE_LIST:
            count = db.update(ChaptersTable.TABLE_NAME, values, where,
                    whereArgs);
            break;

        case CHAPTERS_TYPE_ONE:
            rowIdtask = uri.getPathSegments().get(1);
            count = db.update(ChaptersTable.TABLE_NAME, values, "_id" + " = "
                    + rowIdtask
                    + (!TextUtils.isEmpty(where) ? " AND (" + ")" : ""),
                    whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private SQLiteDatabase getWritableDatabase() {
        File sqliteDBFile = null;
        SQLiteDatabase sqliteDB = null;
        try {
            sqliteDB = mDbHelper.getWritableDatabase();
            sqliteDBFile = new File(sqliteDB.getPath());
            if (!sqliteDBFile.exists()) {
                onCreate();
                sqliteDB = mDbHelper.getWritableDatabase();
            }
        } finally {
            sqliteDBFile = null;
        }
        return sqliteDB;
    }
    
    private SQLiteDatabase getReadableDatabase() {
        File sqliteDBFile = null;
        SQLiteDatabase sqliteDB = null;
        try {
            sqliteDB = mDbHelper.getReadableDatabase();
            sqliteDBFile = new File(sqliteDB.getPath());
            if (!sqliteDBFile.exists()) {
                onCreate();
                sqliteDB = mDbHelper.getWritableDatabase();
            }
        } finally {
            sqliteDBFile = null;
        }
        return sqliteDB;
    }

    private static class GitaDBHelper extends SQLiteOpenHelper {

        private final Context mContext;

        public GitaDBHelper(Context context) {
            super(context, GitaDBProviderMetaData.DATABASE_NAME, null, GitaDBProviderMetaData.DATABASE_VERSION);
            mContext = context;
            initialize();
        }

        /**
         * Initializes database. Creates database if doesn't exist.
         */
        private void initialize() {
            if (databaseExists()) {
                int dbVersion = PreferenceUtils.getDBVersion(mContext);
                if (GitaDBProviderMetaData.DATABASE_VERSION != dbVersion) {
                    File dbFile = mContext.getDatabasePath(GitaDBProviderMetaData.DATABASE_NAME);
                    if (!dbFile.delete()) {
                        Log.w(TAG, "Unable to update database");
                    }
                }
            }
            if (!databaseExists()) {
                createDatabase();
            }
        }

        /**
         * Returns true if database file exists, false otherwise.
         */
        private boolean databaseExists() {
            File dbFile = mContext.getDatabasePath(GitaDBProviderMetaData.DATABASE_NAME);
            return dbFile.exists();
        }

        /**
         * Creates database by copying it from assets directory.
         */
        private void createDatabase() {
            String parentPath = mContext.getDatabasePath(GitaDBProviderMetaData.DATABASE_NAME).getParent();
            String path = mContext.getDatabasePath(GitaDBProviderMetaData.DATABASE_NAME).getPath();

            File file = new File(parentPath);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    Log.w(TAG, "Unable to create database directory");
                    return;
                }
            }

            InputStream is = null;
            OutputStream os = null;
            try {
                is = mContext.getAssets().open(GitaDBProviderMetaData.DATABASE_NAME);
                os = new FileOutputStream(path);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                PreferenceUtils.setDBVersion(mContext, GitaDBProviderMetaData.DATABASE_VERSION);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                int newVersion) {
        }
    }
        
    }

