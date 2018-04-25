package com.spencerstudios.admincenter.Utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private static SharedPreferences prefs;
    private static final String USER = "user";

    public static String getUserPref(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(USER, "");
    }

    public static void setUserPref(Context context, String user){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER, user).apply();
    }
}
