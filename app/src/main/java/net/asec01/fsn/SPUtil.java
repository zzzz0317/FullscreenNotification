package net.asec01.fsn;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    SharedPreferences.Editor editor;
    private static String PREF_NAME = "settings";
    private static String DEFAULT_STRING = "[fsn]";
    private static Boolean DEFAULT_BOOLEAN = false;
    private static Integer DEFAULT_INTEGER = 0;

    public SPUtil() {
        // Blank
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getString(Context context,String key) {
        //Log.i("getString", key+ " , " +getPrefs(context).getString(key, DEFAULT_STRING));
        return getPrefs(context).getString(key, DEFAULT_STRING);
    }

    public static void setString(Context context,String key, String input) {
        //Log.i("setString", key+ " , " +getPrefs(context).getString(key, DEFAULT_STRING) + "->" + input);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, input);
        editor.commit();
    }
    public static Boolean getBoolean(Context context,String key) {
        //Log.i("getBoolean", key+ " , " +getPrefs(context).getBoolean(key, DEFAULT_BOOLEAN));
        return getPrefs(context).getBoolean(key, DEFAULT_BOOLEAN);
    }

    public static void setBoolean(Context context,String key, Boolean input) {
        //Log.i("setBoolean", key+ " , " +getPrefs(context).getBoolean(key, DEFAULT_BOOLEAN) + "->" + input);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(key, input);
        editor.commit();
    }
    public static Integer getInt(Context context,String key) {
        //Log.i("getInt", key+ " , " +getPrefs(context).getInt(key,DEFAULT_INTEGER));
        return getPrefs(context).getInt(key,DEFAULT_INTEGER);
    }

    public static void setInt(Context context, String key, Integer input) {
        //Log.i("setInt", key+ " , " +getPrefs(context).getInt(key,DEFAULT_INTEGER) + "->" + input);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(key, input);
        editor.commit();
    }

}

