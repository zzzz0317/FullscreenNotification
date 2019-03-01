package net.asec01.fsn;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Set;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class OldMainActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_main);
        loadVar();
        loadStats();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadVar();
        loadStats();
    }

    private void loadVar() {
        EditText et_titlekeyword = (EditText) findViewById(R.id.et_titlekeyword);
        EditText et_msgkeyword = (EditText) findViewById(R.id.et_msgkeyword);
        EditText et_vib = (EditText) findViewById(R.id.et_vib);
        Button btn_switch_title = (Button) findViewById(R.id.btn_switch_title);
        Button btn_switch_msg = (Button) findViewById(R.id.btn_switch_msg);
        Button btn_switch_vib = (Button) findViewById(R.id.btn_switch_vib);
        Button btn_switch_fs = (Button) findViewById(R.id.btn_switch_fs);
        et_titlekeyword.setText(SPUtil.getString(this, "titlekeyword"));
        et_msgkeyword.setText(SPUtil.getString(this, "msgkeyword"));
        et_vib.setText(SPUtil.getInt(this, "vib_time").toString());
        btn_switch_title.setText(SPUtil.getBoolean(this,"sw_title").toString());
        btn_switch_msg.setText(SPUtil.getBoolean(this,"sw_msg").toString());
        btn_switch_vib.setText(SPUtil.getBoolean(this,"sw_vib").toString());
        btn_switch_fs.setText(SPUtil.getBoolean(this,"sw_fullscreen").toString());
    }
    private void loadStats(){
        ImageView img_check_listener = (ImageView) findViewById(R.id.img_check_listener);
        ImageView img_check_bat = (ImageView) findViewById(R.id.img_check_bat);
        if (isNotificationListenerEnabled(this)) {
            img_check_listener.setImageDrawable(getDrawable(R.drawable.ok));
        }else{
            img_check_listener.setImageDrawable(getDrawable(R.drawable.error));
            Toast.makeText(this, "无读取通知权限\n请点击\"通知读取设置\"以打开服务", Toast.LENGTH_SHORT).show();
        }
        if (isIgnoringBatteryOptimizations()) {
            img_check_bat.setImageDrawable(getDrawable(R.drawable.ok));
        }else{
            img_check_bat.setImageDrawable(getDrawable(R.drawable.error));
        }
    }
    public void onSaveClick(View v) {
        EditText et_titlekeyword = (EditText) findViewById(R.id.et_titlekeyword);
        EditText et_msgkeyword = (EditText) findViewById(R.id.et_msgkeyword);
        EditText et_vib = (EditText) findViewById(R.id.et_vib);
        String titlekeyword = et_titlekeyword.getText().toString();
        String msgkeyword = et_msgkeyword.getText().toString();
        Integer vib_time = 200;
        try{
            vib_time = Integer.valueOf(et_vib.getText().toString());
        }catch (Exception e){
            Toast.makeText(this, "震动时间已重置", Toast.LENGTH_SHORT).show();
        }
        if (titlekeyword.equals("") || titlekeyword.equals("")) {
            Toast.makeText(this, "保存失败: 禁止空关键词", Toast.LENGTH_SHORT).show();
        }else{
            SPUtil.setString(this, "titlekeyword", titlekeyword);
            SPUtil.setString(this, "msgkeyword", msgkeyword);
            SPUtil.setInt(this, "vib_time", vib_time);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSwitchTitleClick(View v){
        Button btn_switch_title = (Button) findViewById(R.id.btn_switch_title);
        Boolean newValue = !SPUtil.getBoolean(this,"sw_title");
        btn_switch_title.setText(newValue.toString());
        SPUtil.setBoolean(this,"sw_title",newValue);
        Toast.makeText(this, "标题匹配："+newValue.toString(), Toast.LENGTH_SHORT).show();
    }
    public void onSwitchMsgClick(View v){
        Button btn_switch_msg = (Button) findViewById(R.id.btn_switch_msg);
        Boolean newValue = !SPUtil.getBoolean(this,"sw_msg");
        btn_switch_msg.setText(newValue.toString());
        SPUtil.setBoolean(this,"sw_msg",newValue);
        Toast.makeText(this, "消息匹配："+newValue.toString(), Toast.LENGTH_SHORT).show();
    }
    public void onSwitchVibClick(View v){
        Button btn_switch_vib = (Button) findViewById(R.id.btn_switch_vib);
        Boolean newValue = !SPUtil.getBoolean(this,"sw_vib");
        btn_switch_vib.setText(newValue.toString());
        SPUtil.setBoolean(this,"sw_vib",newValue);
        Toast.makeText(this, "震动："+newValue.toString(), Toast.LENGTH_SHORT).show();
    }
    public void onSwitchFSClick(View v){
        Button btn_switch_fs = (Button) findViewById(R.id.btn_switch_fs);
        Boolean newValue = !SPUtil.getBoolean(this,"sw_fullscreen");
        btn_switch_fs.setText(newValue.toString());
        SPUtil.setBoolean(this,"sw_fullscreen",newValue);
        Toast.makeText(this, "全屏展示："+newValue.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onCheckNPClick(View v) {
        if (!isNotificationListenerEnabled(this)) {
            Toast.makeText(this, "无读取通知权限\n请授权\"ZZ通知监听服务\"", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "权限检查通过\n如需关闭服务，请取消授权\"ZZ通知监听服务\"", Toast.LENGTH_SHORT).show();
        }
        openSettings("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
    }

    public void onCheckBPClick(View v) {
        Intent intent = new Intent();
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!isIgnoringBatteryOptimizations()) {
            Toast.makeText(this, "未加入电池优化白名单", Toast.LENGTH_SHORT).show();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            Toast.makeText(this, "已加入电池优化白名单", Toast.LENGTH_SHORT).show();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }
//        startActivity(intent);
        startActivityForResult(intent,0);
    }

    public void onPreviewClick(View v) {
        //Toast.makeText(this, "预览功能暂时停用", Toast.LENGTH_SHORT).show();
        createNotificationChannel("test","测试消息", NotificationManager.IMPORTANCE_MIN);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("测试通知")
                .setAutoCancel( true )
                .setPriority( Notification.PRIORITY_MAX )
                .setWhen( System.currentTimeMillis() )
                .setDefaults( Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND )
                .setContentText("Hi, I'm Zhangzhe\n" +
                        "Beijing, China\n" +
                        "\n" +
                        "大一狗 00后 双鱼座\n" +
                        "买了一加的米粉\n" +
                        "社交能力基本没有\n" +
                        "嗯 就这样");
        Intent resultIntent = new Intent(this,OldMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }


    private void openSettings(String setting) {
//        startActivity(new Intent(setting));
        startActivityForResult(new Intent(setting),0);
    }

    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }
    public boolean isIgnoringBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(getPackageName());
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }else{
            Log.e("测试通知","Android版本低于26，无需创建通知渠道");
        }
    }
}
