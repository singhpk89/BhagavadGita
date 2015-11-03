package com.sunilsahoo.bhagavadgita.utils;



public class Log {
	public static final int NO_DEBUG_LOGS = -1;
	public static final String TAG = Log.class.getName();

	public static boolean mIsDebugEnabled = false;
	public static int v(String tag, String msg) {
		return mIsDebugEnabled ? android.util.Log.v(tag, msg) : NO_DEBUG_LOGS;
	}

	public static int d(String tag, String msg) {
		return mIsDebugEnabled ? android.util.Log.d(tag, msg) : NO_DEBUG_LOGS;
	}

	public static int i(String tag, String msg) {
		return mIsDebugEnabled ? android.util.Log.i(tag, msg) : NO_DEBUG_LOGS;
	}

	public static int w(String tag, String msg) {
		return mIsDebugEnabled ? android.util.Log.w(tag, msg) : NO_DEBUG_LOGS;
	}

	public static int e(String tag, String msg) {
		return mIsDebugEnabled ? android.util.Log.e(tag, msg) : NO_DEBUG_LOGS;
	}

}
