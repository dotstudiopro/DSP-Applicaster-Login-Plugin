package com.dotstudioz;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mohsin on 03-10-2016.
 */
public class SharedPreferencesUtil {
    private static SharedPreferencesUtil ourInstance = new SharedPreferencesUtil();
    private static Context mContext;

    public static SharedPreferencesUtil getInstance(Context context) {
        mContext = context;
        return ourInstance;
    }

    private SharedPreferencesUtil() {
    }

    public void addToSharedPreference(String sharedPreferenceName, String sharedPreferenceValue, String sharedReferenceKey) {
        SharedPreferences sp = mContext.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.putString(sharedReferenceKey, sharedPreferenceValue);
        Ed.commit();
    }

    public String getSharedPreference(String sharedPreferenceName, String sharedPreferenceKey) {
        SharedPreferences spRead = mContext.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        String stringToReturn = "";
        if (spRead != null)
            stringToReturn = spRead.getString(sharedPreferenceKey, null);

        return stringToReturn;
    }

    public void removeFromSharedPreference(String sharedPreferenceName, String sharedPreferenceKey) {
        SharedPreferences sp = mContext.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.remove(sharedPreferenceKey);
        Ed.clear();
        Ed.commit();
    }

    public boolean containsKeyFromSharedPreference(String sharedPreferenceName, String sharedPreferenceKey) {
        SharedPreferences sp = mContext.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return sp.contains(sharedPreferenceKey);
    }
}
