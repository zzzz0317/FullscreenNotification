package net.asec01.fsn;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;

import com.oasisfeng.condom.CondomContext;
import com.tencent.bugly.crashreport.CrashReport;

public class AppNotification extends Application {
    public StatusBarNotification sbn;

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashReport.initCrashReport(getApplicationContext(), "0bda0260e1", true);
        if (!SPUtil.getBoolean(this, "sw_bugly")) {
            initBugly();
        }

    }

    public void initBugly() {
//        CrashReport.initCrashReport(getApplicationContext(), "0bda0260e1", true);
        LogUtil.w("initBugly", "Bugly 已启用");
        CrashReport.initCrashReport(CondomContext.wrap(getApplicationContext(), "Condom-Bugly"), "0bda0260e1", false);
    }


}
