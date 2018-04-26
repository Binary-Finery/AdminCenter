package com.spencerstudios.admincenter.Utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private static final String USER = "user";
    private static SharedPreferences prefs;

    public static String getUserPref(Context context, String KEY, String DEF) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY, DEF);
    }

    public static void setUserPref(Context context, String KEY, String content) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY, content).apply();
    }

    public static boolean getBooleanPrefs(Context context, String KEY, boolean DEF) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY, DEF);
    }
}
