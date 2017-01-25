package com.mail.sendsafe.common;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

	private SharedPreferences pref = null;

	static AppPreferences appPref = null;

	private Context mContext = null;

	public static AppPreferences getInstance(Context context) {

		if (appPref == null)
			appPref = new AppPreferences(context);

		return appPref;
	}

	public AppPreferences(Context context) {
		this.mContext = context;

		if (pref == null)
			pref = mContext.getSharedPreferences("bsecure", 0);

	}

	public void addToStore(String key, String value) {

		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public String getFromStore(String key) {

		String res = pref.getString(key, "");
		return res;
	}

	public int getFromStoreInteger(String key) {

		int res = pref.getInt(key, 0);
		return res;
	}

	public void addToStoreInteger(String key, int value) {

		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();

	}

	public void clearSharedPreferences() {

		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();

	}

	public void clearSharedPreferences(String key) {

		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		editor.commit();

	}

}
