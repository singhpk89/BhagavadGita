package com.sunilsahoo.bhagavadgita.db;

import android.net.Uri;

public class GitaDBProviderMetaData {

	public static final String AUTHORITY = "com.sunilsahoo.bhagavadgita";
	public static final String DATABASE_NAME = "gita.sqlite";
	public static final int DATABASE_VERSION = 4;

	public static final class QuotesTable implements QuotesColumns {
		public static final String _ID = "_id";
		public static final String PATH = "quotes";
		public static final String TABLE_NAME = "quotes";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
				+ AUTHORITY + "." + PATH;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
				+ AUTHORITY + "." + PATH;
		public static final String DEFAULT_SORT_ORDER = QuotesColumns.TEXT_NO
				+ " ASC";
	}


	public interface QuotesColumns {
		public static final String KEY_ROWID = "_id";
		public static final String CHAPTER_NO = "ch_no";
		public static final String TEXT_NO = "txt_no";
		public static final String BODY = "meaning";
		public static final String SLOKA_SANSKRIT = "sloka_sanskrit";
		public static final String SLOKA_ENGLISH = "sloka_eng";
		public static final String IS_FAVORITE = "is_favourite";
	}
	
	
	
	public static final class ChaptersTable implements ChapterColumns {
		public static final String _ID = "_id";
		public static final String PATH = "chapters";
		public static final String TABLE_NAME = "chapters";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
				+ AUTHORITY + "." + PATH;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
				+ AUTHORITY + "." + PATH;
		public static final String DEFAULT_SORT_ORDER = ChaptersTable.KEY_ROWID
				+ " ASC";
	}


	public interface ChapterColumns {
		public static final String KEY_ROWID = "_id";
		public static final String TITLE = "title";
	}
	
}