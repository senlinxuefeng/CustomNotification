package com.yumingchuan.customnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * 自定义通知接受器
 */
public class CustomNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("sdfsdf",intent.getAction());
        if (!TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals("schedulePage")) {
            Intent intent1 = new Intent();
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setClass(context, OneActivity.class);
            context.startActivity(intent1);
            CustomNotificationUtils.getInstance().collapseStatusBar(context);
        } else {

        }
    }

}
