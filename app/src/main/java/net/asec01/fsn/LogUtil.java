package net.asec01.fsn;

import android.util.Log;

public class LogUtil {
    static Boolean DEBUG = false;

    public static void i(String tag, String msg){
        if (DEBUG)
            Log.i(tag,msg);
    }
    public static void w(String tag, String msg){
        if (DEBUG)
            Log.w(tag,msg);
    }
}
