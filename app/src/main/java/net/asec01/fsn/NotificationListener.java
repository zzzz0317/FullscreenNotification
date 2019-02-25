package net.asec01.fsn;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String titlekeyword = SPUtil.getString(this, "titlekeyword");
        String msgkeyword = SPUtil.getString(this, "msgkeyword");
        Notification notification = sbn.getNotification();
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
//        Log.i("NotificationListener", "Notification posted " + notificationTitle + " & " + notificationText);
//        Log.i("NotificationListener", "titlekeyword " + titlekeyword + " & " + notificationTitle);
//        Log.i("NotificationListener", "msgkeyword " + msgkeyword + " & " + notificationText);
        Boolean sw_title = SPUtil.getBoolean(this,"sw_title");
        Boolean sw_msg = SPUtil.getBoolean(this,"sw_msg");
        Boolean cont0,cont1,cont2,cont3,cont3_0,cont3_1,cont3_2,cont;
        cont = false;
        cont1 = !TextUtils.isEmpty(notificationTitle) && !TextUtils.isEmpty(notificationText);
        if (cont1) {
            cont0 = notificationPkg.equals(getPackageName());
            cont2 = (notificationTitle.contains(titlekeyword) && sw_title) || (notificationText.contains(msgkeyword) && sw_msg);
            cont3_1 = notificationText.contains("语音通话中");
            cont3_2 = notificationText.contains("视频通话中");
            cont3_0 = notificationPkg.equals("com.tencent.mm");
            cont3 = !(cont3_0 && (cont3_1 || cont3_2));
            cont = (cont2 && cont3) || cont0;
        }
        if (cont) {
            if (SPUtil.getBoolean(this,"sw_vib")){
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(SPUtil.getInt(this, "vib_time"));
            }
            Log.i("NotificationListener", "Notification posted " + notificationTitle + " & " + notificationText);
            if (SPUtil.getBoolean(this, "sw_fullscreen")) {
                Log.i("NotificationListener", "全屏消息");
                AppNotification appNotification = ((AppNotification) getApplicationContext());
                appNotification.sbn = sbn;
                newNotification(notificationTitle, notificationText, notificationPkg);
            } else {
                Log.i("NotificationListener", "直接打开消息");
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                    Toast.makeText(this, "收到含关键词的消息，已为您自动打开", Toast.LENGTH_SHORT).show();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void newNotification(String title, String msg, String packageName) {
        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putString("packageName", packageName);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
