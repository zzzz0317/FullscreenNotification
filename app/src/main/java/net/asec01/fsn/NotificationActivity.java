package net.asec01.fsn;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    String AppName = "";
    PendingIntent pendingIntent;
    String packageName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        try{
            loadContent();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadContent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        AppNotification appNotification = ((AppNotification) getApplicationContext());
        Notification notification = appNotification.sbn.getNotification();
        Bundle extras = appNotification.sbn.getNotification().extras;

        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_msg = findViewById(R.id.tv_msg);
        TextView tv_appname = findViewById(R.id.tv_appname);
        Button btn_openmsg = findViewById(R.id.btn_openmsg);
        ImageView img_head = findViewById(R.id.img_head);

//        // 获取接收消息APP的包名
//        String notificationPkg = appNotification.sbn.getPackageName();
//        // 获取接收消息的抬头
//        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
//        // 获取接收消息的内容
//        String notificationText = extras.getString(Notification.EXTRA_TEXT);
//        AppName = getAppName(notificationPkg);
//
//        tv_title.setText(notificationTitle);
//        tv_msg.setText(notificationText);

//        Bitmap bmp
//        if (extras.containsKey(Notification.EXTRA_PICTURE)) {
//            // this bitmap contain the picture attachment
//            Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
//        }
        try {
            Drawable drawable = getPackageManager().getApplicationIcon(bundle.getString("packageName"));
            img_head.setImageDrawable(drawable);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
        if (bmp != null){
            img_head.setImageBitmap(bmp);
        }
        bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
        if (bmp != null){
            img_head.setImageBitmap(bmp);
        }
//        if (extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
//             this bitmap contain the picture attachment
//            Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
//            if (bmp != null){
//                img_head.setImageBitmap(bmp);
//            }
//        } else {
//            img_head.setImageBitmap(getAppIcon(bundle.getString("packageName")));
//        }
        packageName = bundle.getString("packageName");
        Log.i("NotificationActivity", "bundle pkg name: " + packageName);
        AppName = getAppName(packageName);

        tv_title.setText(bundle.getString("title").replace(SPUtil.getString(this, "titlekeyword"), ""));
        tv_msg.setText(bundle.getString("msg"));
//        tv_msg.setText(bundle.getString("msg").replace(SPUtil.getString(this, "msgkeyword"), ""));
        tv_appname.setText("来自 \"" + AppName + "\"");
        btn_openmsg.setText("打开 \"" + AppName + "\" 处理消息");

        pendingIntent = notification.contentIntent;
    }

    public void onOpenClick(View v) {
        AppNotification appNotification = ((AppNotification) getApplicationContext());
        if (appNotification.sbn == null) {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            Log.i("NotificationActivity", "open pkg name: " + packageName);
            if(intent==null){
                System.out.println("APP not found!");
            }
            startActivity(intent);
        } else {
            try {
                pendingIntent.send();
                appNotification.sbn = null;
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAppName(String packageName) {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null)
                return packageInfo.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "未知应用";
    }
}
