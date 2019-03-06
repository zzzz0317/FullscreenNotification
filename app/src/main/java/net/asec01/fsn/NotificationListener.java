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
        LogUtil.i("NotificationListener", "收到新通知");

        if (!SPUtil.getBoolean(this, "sw_main")) {
            LogUtil.i("NotificationListener", "由于主开关关闭，不检测");
            return;
        }
        Notification notification = sbn.getNotification();
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        LogUtil.i("NotificationListener", "主开关未关闭\n" +
                "应用包名: \n" + notificationPkg + "\n" +
                "消息标题: \n" + notificationTitle + "\n" +
                "消息文字: \n" + notificationText);
        if (TextUtils.isEmpty(notificationTitle) || TextUtils.isEmpty(notificationText)) {
            LogUtil.i("NotificationListener", "空标题或空消息");
            return;
        }
        if (notificationPkg.equals("com.tencent.mm") && (notificationText.contains("语音通话中") || notificationText.contains("视频通话中"))) {
            LogUtil.i("NotificationListener", "微信语音或视频");
            return;
        }

        Boolean sw_title = SPUtil.getBoolean(this, "sw_title");
        Boolean sw_msg = SPUtil.getBoolean(this, "sw_msg");
        Boolean sw_vib = SPUtil.getBoolean(this, "sw_vib");
        Boolean sw_vib_s = SPUtil.getBoolean(this, "sw_vib_s");

        String keyword_title = SPUtil.getString(this, "et_keyword_title");
        String keyword_msg = SPUtil.getString(this, "et_keyword_text");
        String keyword_vib = SPUtil.getString(this, "et_keyword_vib");

//        if ((notificationTitle.contains(keyword_vib) && sw_vib_s) || (notificationText.contains(keyword_vib) && sw_vib_s)) {
        if (notificationText.contains(keyword_vib) && sw_vib_s) {
            strongNotification(sbn, notificationTitle, notificationText, notificationPkg);
            LogUtil.i("NotificationListener", "增强提醒关键词匹配成功 - " + keyword_vib);
            return;
        } else if (notificationTitle.contains(keyword_title) && sw_title) {
            normalNotification(sbn, notification, notificationTitle, notificationText, notificationPkg, sw_vib);
            LogUtil.i("NotificationListener", "标题关键词匹配成功 - " + keyword_title);
            return;
        } else if (notificationText.contains(keyword_msg) && sw_msg) {
            normalNotification(sbn, notification, notificationTitle, notificationText, notificationPkg, sw_vib);
            LogUtil.i("NotificationListener", "消息关键词匹配成功 - " + keyword_msg);
            return;
        } else if (notificationPkg.equals(getPackageName())) {
            normalNotification(sbn, notification, notificationTitle, notificationText, notificationPkg, false);
//            strongNotification(sbn, notificationTitle, notificationText, notificationPkg);
            LogUtil.i("NotificationListener", "测试消息");
            return;
        }


        if (SPUtil.getBoolean(this, "sw_more")) {
            if (SPUtil.getBoolean(this, "sw_more_vib")) {
                for (String retval : SPUtil.getString(this, "et_more_vib").split(",")) {
                    if (notificationText.contains(retval)) {
//                    if (notificationTitle.contains(retval) || notificationText.contains(retval)) {
                        strongNotification(sbn, notificationTitle, notificationText, notificationPkg);
                        LogUtil.i("NotificationListener", "增强提醒关键词匹配成功(m) - " + retval);
                        return;
                    }
                }
            }
            if (SPUtil.getBoolean(this, "sw_more_title")) {
                for (String retval : SPUtil.getString(this, "et_more_title").split(",")) {
                    if (notificationTitle.contains(retval)) {
                        normalNotification(sbn, notification, notificationTitle, notificationText, notificationPkg, sw_vib);
                        LogUtil.i("NotificationListener", "标题关键词匹配成功(m) - " + retval);
                        return;
                    }
                }
            }
            if (SPUtil.getBoolean(this, "sw_more_text")) {
                for (String retval : SPUtil.getString(this, "et_more_text").split(",")) {
                    if (notificationText.contains(retval)) {
                        normalNotification(sbn, notification, notificationTitle, notificationText, notificationPkg, sw_vib);
                        LogUtil.i("NotificationListener", "消息关键词匹配成功(m) - " + retval);
                        return;
                    }
                }
            }
        }
    }

    public void strongNotification(StatusBarNotification sbn, String title, String msg, String packageName) {
        LogUtil.i("NotificationListener", "增强消息");
        AppNotification appNotification = ((AppNotification) getApplicationContext());
        appNotification.sbn = sbn;
        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putString("packageName", packageName);
        bundle.putBoolean("alarm", true);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void normalNotification(StatusBarNotification sbn, Notification notification,
                                   String notificationTitle, String notificationText, String notificationPkg, Boolean vib) {
        if (SPUtil.getBoolean(this, "sw_fullscreen")) {
            newFullscreenNotification(sbn, notificationTitle, notificationText, notificationPkg);
        } else {
            newDirectOpenNotification(notification, notificationTitle, notificationText, notificationPkg);
        }
        if (vib) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(SPUtil.getInt(this, "vib_time"));
        }
    }

    public void newDirectOpenNotification(Notification notification, String title, String msg, String packageName) {
        LogUtil.i("NotificationListener", "直接打开消息");
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
            Toast.makeText(this, "收到含关键词的消息，已为您自动打开", Toast.LENGTH_SHORT).show();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void newFullscreenNotification(StatusBarNotification sbn, String title, String msg, String packageName) {
        LogUtil.i("NotificationListener", "全屏消息");
        AppNotification appNotification = ((AppNotification) getApplicationContext());
        appNotification.sbn = sbn;
        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putString("packageName", packageName);
        bundle.putBoolean("alarm", false);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
