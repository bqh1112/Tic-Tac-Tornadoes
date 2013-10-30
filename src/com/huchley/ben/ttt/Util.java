package com.huchley.ben.ttt;

import android.util.Log;

public class Util {
	public static final boolean DEBUG = false;
	public static void debug(String tag, String message)
	{
		if (DEBUG)
		{
			Log.i(tag, message);
		}
	}
	public static void warn(String tag, String message)
	{
		if (DEBUG)
		{
			Log.w(tag, message);
		}
	}
}
