package net.asec01.fsn;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

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

        if (!TextUtils.isEmpty(notificationTitle) && !TextUtils.isEmpty(notificationText)) {
            if (notificationTitle.contains(titlekeyword) || notificationText.contains(msgkeyword)) {
                Log.i("NotificationListener", "Notification posted " + notificationTitle + " & " + notificationText);
                if (SPUtil.getBoolean(this, "fullscreen")) {
                    Log.i("NotificationListener", "全屏消息");
                    AppNotification appNotification = ((AppNotification) getApplicationContext());
                    appNotification.sbn = sbn;
                    newNotification(notificationTitle, notificationText, notificationPkg);
                } else {
                    Log.i("NotificationListener", "直接打开消息");
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
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
