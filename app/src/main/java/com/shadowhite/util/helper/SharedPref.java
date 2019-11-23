package com.shadowhite.util.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPref {
    private static SharedPreferences preferences;
    public static final String KEY_BOARD_HEIGHT = "keyboard_height";
    public static final String REMEMBER_ME_PREF = "secondary_pref";
    public static final String USER_ID="user_id";
    public static final String PASSWORD="password";
    public static final String IS_REMEMBERED="is_remembered";
    public static final String LAN="lan";
    private SharedPref() {
    }

    public static void init(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }
    public static void clear()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    public static boolean write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);

        return editor.commit();
    }

    public static boolean write(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);

        return editor.commit();
    }

    public static boolean write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(key, value);

        return editor.commit();
    }

    public static boolean write(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(key, value);

        return editor.commit();
    }
    public static boolean write(Context context, String key, String value) {
        SharedPreferences pref=context.getSharedPreferences(REMEMBER_ME_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    public static String read(Context context, String key) {
        SharedPreferences pref=context.getSharedPreferences(REMEMBER_ME_PREF, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }


    public static String read(String key) {
        return preferences.getString(key, "");
    }

    public static long readLong(String key) {
        return preferences.getLong(key, 0);
    }

    public static int readInt(String key) {
        return preferences.getInt(key, 0);
    }
    public static boolean readBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static boolean readBooleanDefaultTrue(String key){
        return preferences.getBoolean(key, true);
    }

    public static boolean contains(String key) {
        return preferences.contains(key);
    }

}