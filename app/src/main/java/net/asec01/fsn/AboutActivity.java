package net.asec01.fsn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_donate_wechat;
    Button btn_open_wechat;
    Button btn_wechat_back;
    Button btn_donate_alipay;
    CardView cv_wechat_qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btn_donate_alipay = findViewById(R.id.btn_donate_alipay);
        btn_donate_wechat = findViewById(R.id.btn_donate_wechat);
        btn_open_wechat = findViewById(R.id.btn_open_wechat);
        btn_wechat_back = findViewById(R.id.btn_wechat_back);
        cv_wechat_qr = findViewById(R.id.cv_wechat_qr);
        btn_donate_alipay.setOnClickListener(this);
        btn_donate_wechat.setOnClickListener(this);
        btn_open_wechat.setOnClickListener(this);
        btn_wechat_back.setOnClickListener(this);
        try {
            InputStream is = getAssets().open("update.txt");
            int lenght = is.available();
            byte[]  buffer = new byte[lenght];
            is.read(buffer);
            String result = new String(buffer, "utf8");
            TextView tv_update_log = findViewById(R.id.tv_update_log);
            tv_update_log.setText(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            TextView tv_version = findViewById(R.id.tv_version);
            tv_version.setText(pi.versionName + " (" + pi.versionCode +")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_donate_alipay:
                openAliPay2Pay("https://qr.alipay.com/tsx04291gzykkrclaq9rmc6");
                break;
            case R.id.btn_donate_wechat:
                cv_wechat_qr.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_open_wechat:
                startWechatScan();
                break;
            case R.id.btn_wechat_back:
                cv_wechat_qr.setVisibility(View.GONE);
                break;
        }
    }

    private void openAliPay2Pay(String qrCode) {
        if (openAlipayPayPage(this, qrCode)) {
//            Toast.makeText(this, "跳转成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "跳转失败", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean openAlipayPayPage(Context context, String qrcode) {
        try {
            qrcode = URLEncoder.encode(qrcode, "utf-8");
        } catch (Exception e) {
        }
        try {
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            openUri(context, alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private static void openUri(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        context.startActivity(intent);
    }

    private void startWechatScan() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
        intent.setFlags( FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP );
        intent.setAction("android.intent.action.VIEW");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "跳转失败", Toast.LENGTH_SHORT).show();
        }
    }
}
