package cn.porkchop.mobilesafe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {
	public static void putBoolean(Context context, String fileName, String key,
			boolean value) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public static boolean getBoolean(Context context, String fileName,
			String key, boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static void putString(Context context, String fileName, String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static String getString(Context context, String fileName,
			String key, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}
	public static void putInt(Context context, String fileName, String key,
			int value) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.apply();
	}
	
	public static int getInt(Context context, String fileName,
			String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}
}
