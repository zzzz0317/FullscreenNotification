package net.asec01.fsn;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Button btn_notificationListenerSettings;
    Button btn_ignoringBatteryOptimizationsSettings;
    Switch sw_main;
    Switch sw_keyword_title;
    Switch sw_keyword_text;
    Switch sw_keyword_vib;
    Switch sw_keyword_vib_s;
    Switch sw_fullscreen;
    Switch sw_ring;
    EditText et_keyword_title;
    EditText et_keyword_text;
    EditText et_keyword_vib;
    EditText et_vib_time;
    Button btn_save;
    Button btn_test;

    Switch sw_more;
    //More View
    Switch sw_more_title;
    Switch sw_more_text;
    Switch sw_more_vib;
    EditText et_more_title;
    EditText et_more_text;
    EditText et_more_vib;
    Button btn_more_save;

    public static void shareText(Context context, String subject, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);//extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//为Activity新建一个任务栈
        context.startActivity(Intent.createChooser(intent, subject));//R.string.action_share同样是标题
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadStats();
        loadVar();
        viewSwitchTo("main");
        if (isFirstRun()) {
            firstRun();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_status) { // 步骤1：获取FragmentManager
            viewSwitchTo("main");
        } else if (id == R.id.nav_more_settings) {
            viewSwitchTo("more_rule");
        } else if (id == R.id.nav_debug) {

        } else if (id == R.id.nav_share) {
            shareText(this, "推荐应用【全屏消息】", "推荐应用【全屏消息】： https://www.coolapk.com/apk/net.asec01.fsn");
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadVar();
        loadStats();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_notificationListenerSettings:
                if (!isNotificationListenerEnabled(this)) {
                    Toast.makeText(this, "无读取通知权限\n请授权\"ZZ通知监听服务\"", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "权限检查通过\n如需关闭服务，请取消授权\"ZZ通知监听服务\"", Toast.LENGTH_SHORT).show();
                }
                startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 0);
                break;
            case R.id.btn_ignoringBatteryOptimizationsSettings:
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
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_save:
                onSaveClick();
                break;
            case R.id.btn_test:
                onPreviewClick();
                break;
            case R.id.btn_more_save:
                onMoreSaveClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.sw_main:
                SPUtil.setBoolean(this, "sw_main", compoundButton.isChecked());
                break;
            case R.id.sw_keyword_title:
                SPUtil.setBoolean(this, "sw_title", compoundButton.isChecked());
                break;
            case R.id.sw_keyword_text:
                SPUtil.setBoolean(this, "sw_msg", compoundButton.isChecked());
                break;
            case R.id.sw_keyword_vib:
                SPUtil.setBoolean(this, "sw_vib", compoundButton.isChecked());
                break;
            case R.id.sw_keyword_vib_s:
                SPUtil.setBoolean(this, "sw_vib_s", compoundButton.isChecked());
                break;
            case R.id.sw_fullscreen:
                SPUtil.setBoolean(this, "sw_fullscreen", compoundButton.isChecked());
//                if(compoundButton.isChecked()) Toast.makeText(this,"on",Toast.LENGTH_SHORT).show();
//                else Toast.makeText(this,"off",Toast.LENGTH_SHORT).show();
                break;
            case R.id.sw_ring:
                SPUtil.setBoolean(this, "sw_ring", compoundButton.isChecked());
                break;
            case R.id.sw_more:
                SPUtil.setBoolean(this, "sw_more", compoundButton.isChecked());
                break;
            case R.id.sw_more_title:
                SPUtil.setBoolean(this, "sw_more_title", compoundButton.isChecked());
                break;
            case R.id.sw_more_text:
                SPUtil.setBoolean(this, "sw_more_text", compoundButton.isChecked());
                break;
            case R.id.sw_more_vib:
                SPUtil.setBoolean(this, "sw_more_vib", compoundButton.isChecked());
                break;
        }
    }

    private void initView() {
        btn_notificationListenerSettings = findViewById(R.id.btn_notificationListenerSettings);
        btn_ignoringBatteryOptimizationsSettings = findViewById(R.id.btn_ignoringBatteryOptimizationsSettings);
        sw_main = findViewById(R.id.sw_main);
        sw_keyword_title = findViewById(R.id.sw_keyword_title);
        sw_keyword_text = findViewById(R.id.sw_keyword_text);
        sw_keyword_vib = findViewById(R.id.sw_keyword_vib);
        sw_keyword_vib_s = findViewById(R.id.sw_keyword_vib_s);
        sw_fullscreen = findViewById(R.id.sw_fullscreen);
        sw_ring = findViewById(R.id.sw_more);
        sw_more = findViewById(R.id.sw_more);
        et_keyword_title = findViewById(R.id.et_keyword_title);
        et_keyword_text = findViewById(R.id.et_keyword_text);
        et_keyword_vib = findViewById(R.id.et_keyword_vib);
        et_vib_time = findViewById(R.id.et_vib_time);
        btn_save = findViewById(R.id.btn_save);
        btn_test = findViewById(R.id.btn_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        btn_notificationListenerSettings.setOnClickListener(this);
        btn_ignoringBatteryOptimizationsSettings.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        sw_main.setOnCheckedChangeListener(this);
        sw_keyword_title.setOnCheckedChangeListener(this);
        sw_keyword_text.setOnCheckedChangeListener(this);
        sw_keyword_vib.setOnCheckedChangeListener(this);
        sw_keyword_vib_s.setOnCheckedChangeListener(this);
        sw_fullscreen.setOnCheckedChangeListener(this);
        sw_ring.setOnCheckedChangeListener(this);
        sw_more.setOnCheckedChangeListener(this);
        initMoreView();
    }

    private void loadVar() {
        sw_main.setChecked(SPUtil.getBoolean(this, "sw_main"));
        sw_keyword_title.setChecked(SPUtil.getBoolean(this, "sw_title"));
        sw_keyword_text.setChecked(SPUtil.getBoolean(this, "sw_msg"));
        sw_keyword_vib.setChecked(SPUtil.getBoolean(this, "sw_vib"));
        sw_keyword_vib_s.setChecked(SPUtil.getBoolean(this, "sw_vib_s"));
        sw_fullscreen.setChecked(SPUtil.getBoolean(this, "sw_fullscreen"));
        sw_ring.setChecked(SPUtil.getBoolean(this, "sw_ring"));
        sw_more.setChecked(SPUtil.getBoolean(this, "sw_more"));
        et_keyword_title.setText(SPUtil.getString(this, "titlekeyword"));
        et_keyword_text.setText(SPUtil.getString(this, "msgkeyword"));
        et_keyword_vib.setText(SPUtil.getString(this, "vibkeyword"));
        et_vib_time.setText(SPUtil.getInt(this, "vib_time").toString());
        loadMoreVar();
    }

    private void loadStats() {
        ImageView iv_notificationListener = findViewById(R.id.iv_notificationListener);
        ImageView iv_ignoringBatteryOptimizations = findViewById(R.id.iv_ignoringBatteryOptimizations);
        TextView tv_notificationListener = findViewById(R.id.tv_notificationListener);
        TextView tv_ignoringBatteryOptimizations = findViewById(R.id.tv_ignoringBatteryOptimizations);
        if (isNotificationListenerEnabled(this)) {
            iv_notificationListener.setImageDrawable(getDrawable(R.drawable.ok));
            tv_notificationListener.setText("服务正常");
        } else {
            iv_notificationListener.setImageDrawable(getDrawable(R.drawable.error));
            tv_notificationListener.setText("未启用");
        }
        if (isIgnoringBatteryOptimizations()) {
            iv_ignoringBatteryOptimizations.setImageDrawable(getDrawable(R.drawable.ok));
            tv_ignoringBatteryOptimizations.setText("已忽略电池优化");
        } else {
            iv_ignoringBatteryOptimizations.setImageDrawable(getDrawable(R.drawable.warning));

            tv_ignoringBatteryOptimizations.setText("未忽略电池优化");
        }
    }

    public void onSaveClick() {
        String titlekeyword = et_keyword_title.getText().toString();
        String msgkeyword = et_keyword_text.getText().toString();
        String vibkeyword = et_keyword_vib.getText().toString();
        Integer vib_time = 200;
        try {
            vib_time = Integer.valueOf(et_vib_time.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "振动时间已重置", Toast.LENGTH_SHORT).show();
        }
        if (titlekeyword.equals("") || titlekeyword.equals("") || vibkeyword.equals("")) {
            Toast.makeText(this, "保存失败: 禁止空关键词", Toast.LENGTH_SHORT).show();
        } else {
            SPUtil.setString(this, "titlekeyword", titlekeyword);
            SPUtil.setString(this, "msgkeyword", msgkeyword);
            SPUtil.setString(this, "vibkeyword", vibkeyword);
            SPUtil.setInt(this, "vib_time", vib_time);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPreviewClick() {
        createNotificationChannel("test", "测试消息", NotificationManager.IMPORTANCE_MIN);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("测试通知")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentText("Hi, I'm Zhangzhe\n" +
                        "Beijing, China\n" +
                        "\n" +
                        "大一狗 00后 双鱼座\n" +
                        "买了一加的米粉\n" +
                        "社交能力基本没有\n" +
                        "嗯 就这样");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
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

    public boolean isFirstRun() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionCode != SPUtil.getInt(this, "version_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void firstRun() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            SPUtil.setInt(this, "version_code", pi.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        } else {
            Log.e("测试通知", "Android版本低于26，无需创建通知渠道");
        }
    }

    private void viewSwitchTo(String v) {
        ScrollView sv_main = findViewById(R.id.sv_main);
        ScrollView sv_more_rule = findViewById(R.id.sv_more_rule);
        switch (v) {
            case "main":
                sv_main.setVisibility(View.VISIBLE);
                sv_more_rule.setVisibility(View.GONE);
                break;
            case "more_rule":
                sv_main.setVisibility(View.GONE);
                sv_more_rule.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initMoreView() {
        sw_more_title = findViewById(R.id.sw_more_title);
        sw_more_text = findViewById(R.id.sw_more_text);
        sw_more_vib = findViewById(R.id.sw_more_vib);
        et_more_title = findViewById(R.id.et_more_title);
        et_more_text = findViewById(R.id.et_more_text);
        et_more_vib = findViewById(R.id.et_more_vib);
        btn_more_save = findViewById(R.id.btn_more_save);
        sw_more_title.setOnCheckedChangeListener(this);
        sw_more_text.setOnCheckedChangeListener(this);
        sw_more_vib.setOnCheckedChangeListener(this);
        btn_more_save.setOnClickListener(this);
    }

    private void loadMoreVar() {
        sw_more_title.setChecked(SPUtil.getBoolean(this, "sw_more_title"));
        sw_more_text.setChecked(SPUtil.getBoolean(this, "sw_more_text"));
        sw_more_vib.setChecked(SPUtil.getBoolean(this, "sw_more_vib"));
        et_more_title.setText(SPUtil.getString(this, "et_more_title"));
        et_more_text.setText(SPUtil.getString(this, "et_more_text"));
        et_more_vib.setText(SPUtil.getString(this, "et_more_vib"));
    }

    private void onMoreSaveClick() {
        String more_title = et_more_title.getText().toString();
        String more_text = et_more_text.getText().toString();
        String more_vib = et_more_vib.getText().toString();

        String str = more_title + "," + more_text + "," + more_vib;
        for (String retval : str.split(",")) {
            System.out.println(retval);
            if (retval.equals("")) {
                Toast.makeText(this, "保存失败: 禁止空关键词", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        SPUtil.setString(this, "et_more_title", more_title);
        SPUtil.setString(this, "et_more_text", more_text);
        SPUtil.setString(this, "et_more_vib", more_vib);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }
}
