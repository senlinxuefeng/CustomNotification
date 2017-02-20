package com.yumingchuan.customnotification;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomNotificationUtils.getInstance().sendCustomNotification(this);
    }

}
