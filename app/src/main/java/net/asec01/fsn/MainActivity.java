package net.asec01.fsn;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadVar();
        if (!isNotificationListenerEnabled(this)) {
            Toast.makeText(this, "无读取通知权限\n请点击\"检查通知权限\"以打开服务", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVar() {
        EditText et_titlekeyword = findViewById(R.id.et_titlekeyword);
        EditText et_msgkeyword = findViewById(R.id.et_msgkeyword);
        et_titlekeyword.setText(SPUtil.getString(this, "titlekeyword"));
        et_msgkeyword.setText(SPUtil.getString(this, "msgkeyword"));
        Button btn_switch_fs = findViewById(R.id.btn_switch_fs);
        btn_switch_fs.setText(SPUtil.getBoolean(this,"fullscreen").toString());
    }

    public void onSaveClick(View v) {
        EditText et_titlekeyword = findViewById(R.id.et_titlekeyword);
        EditText et_msgkeyword = findViewById(R.id.et_msgkeyword);
        String titlekeyword = et_titlekeyword.getText().toString();
        String msgkeyword = et_msgkeyword.getText().toString();
        if (titlekeyword.equals("") || titlekeyword.equals("")) {
            Toast.makeText(this, "保存失败: 禁止空关键词", Toast.LENGTH_SHORT).show();
        }else{
            SPUtil.setString(this, "titlekeyword", titlekeyword);
            SPUtil.setString(this, "msgkeyword", msgkeyword);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSwitchFSClick(View v){
        Button btn_switch_fs = findViewById(R.id.btn_switch_fs);
        Boolean newValue = !SPUtil.getBoolean(this,"fullscreen");
        btn_switch_fs.setText(newValue.toString());
        SPUtil.setBoolean(this,"fullscreen",newValue);
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
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        Intent intent = new Intent();
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Toast.makeText(this, "未加入电池优化白名单", Toast.LENGTH_SHORT).show();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            Toast.makeText(this, "已加入电池优化白名单", Toast.LENGTH_SHORT).show();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }
        startActivity(intent);
    }

    public void onPreviewClick(View v) {
//        newNotification("测试标题", "测试内容测试内容测试内容测试内容测试内容测试内容测试内容测试内容", "net.asec01.fsn");
        Toast.makeText(this, "预览功能暂时停用", Toast.LENGTH_SHORT).show();
    }

    public void newNotification(String title, String msg, String packageName) {
        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putString("packageName", packageName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void openSettings(String setting) {
        startActivity(new Intent(setting));
    }

    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }
}
