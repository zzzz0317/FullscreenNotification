package net.asec01.fsn;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    SharedPreferences.Editor editor;
    private static String PREF_NAME = "settings";
    private static String DEFAULT_STRING = "[fsn]";

    public SPUtil() {
        // Blank
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getString(Context context,String key) {
        return getPrefs(context).getString(key, DEFAULT_STRING);
    }

    public static void setString(Context context,String key, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, input);
        editor.commit();
    }
    public static Boolean getBoolean(Context context,String key) {
        return getPrefs(context).getBoolean(key, false);
    }

    public static void setBoolean(Context context,String key, Boolean input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(key, input);
        editor.commit();
    }

}

