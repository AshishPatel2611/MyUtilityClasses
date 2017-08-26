package com.abc.mylibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Prefs {
	public static SharedPreferences sharedPreferences = null;

	public static void openPrefs(Context context) {

		sharedPreferences = context.getSharedPreferences(constants.PREF_FILE,
				Context.MODE_PRIVATE);
	}

	public static String getValue(Context context, String key,
			String defaultValue) {

		Prefs.openPrefs(context);

		String result = Prefs.sharedPreferences.getString(key, defaultValue);
		Prefs.sharedPreferences = null;
		return result;
	}
	
	
	

	public static void setValue(Context context, String key, String value) {

		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();

		preferenceEditor.putString(key, value);
		preferenceEditor.commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;
	}

	public static void setClear(Context context) {
		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();
		preferenceEditor.clear().commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;
	}

	public static void remove(Context context, String key) {
		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();
		preferenceEditor.remove(key);
		preferenceEditor.commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;

	}

}
