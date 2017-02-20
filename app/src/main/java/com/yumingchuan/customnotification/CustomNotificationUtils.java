package com.yumingchuan.customnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/2/20.
 */

public class CustomNotificationUtils {


    private static CustomNotificationUtils customNotificationUtils;

    /**
     * @return
     */
    public static CustomNotificationUtils getInstance() {
        if (customNotificationUtils == null) {//双重校验DCL单例模式
            synchronized (CustomNotificationUtils.class) {//同步代码块
                if (customNotificationUtils == null) {
                    customNotificationUtils = new CustomNotificationUtils();
                }
            }
        }
        return customNotificationUtils;
    }


    CustomNotificationUtils() {

    }


    /**
     * 设置自定义通知的样式
     */
    public void sendCustomNotification(Context mContext) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        builder.setSmallIcon(R.mipmap.ic_launcher);//一定要设置

        RemoteViews remoteViews;
        if (isDarkNotificationBar(mContext)) {
            remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification_white);
        } else {
            remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification_dark);
        }


        builder.setContent(remoteViews);
        Intent intentSchedule = new Intent(mContext, OneActivity.class);
        PendingIntent pendingIntentSchedule = PendingIntent.getActivity(mContext, 1, intentSchedule, 0);
        builder.setContentIntent(pendingIntentSchedule);
        remoteViews.setOnClickPendingIntent(R.id.tv_schedule, pendingIntentSchedule);


        Intent intentInbox = new Intent(mContext, TwoActivity.class);
        PendingIntent pendingIntentInbox = PendingIntent.getActivity(mContext, 2, intentInbox, 0);
        builder.setContentIntent(pendingIntentInbox);
        remoteViews.setOnClickPendingIntent(R.id.tv_inbox, pendingIntentInbox);

        Intent intentAddSchedule = new Intent(mContext, ThreeActivity.class);
        PendingIntent pendingIntentAddSchedule = PendingIntent.getActivity(mContext, 3, intentAddSchedule, 0);
        builder.setContentIntent(pendingIntentAddSchedule);
        remoteViews.setOnClickPendingIntent(R.id.tv_addSchedule, pendingIntentAddSchedule);


        Intent intentNote = new Intent(mContext, FourActivity.class);
        PendingIntent pendingIntentNote = PendingIntent.getActivity(mContext, 4, intentNote, 0);
        builder.setContentIntent(pendingIntentNote);
        remoteViews.setOnClickPendingIntent(R.id.tv_note, pendingIntentNote);

        builder.setOngoing(true);//常驻通知
        builder.setPriority(2);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(100, builder.build());


    }


    /**
     * @return 判断当前通知的背景色，是偏向于白色还是黑色
     */
    private boolean isDarkNotificationBar(Context mContext) {
        return !isColorSimilar(Color.BLACK, getNotificationColor(mContext));
    }

    private int getNotificationColor(Context mContext) {
        if (mContext instanceof AppCompatActivity) {
            return getNotificationColorCompat(mContext);
        } else {
            return getNotificationColorCompatInternal(mContext);
        }
    }

    private String DUMMY_TITLE = "DUMMY_TITLE";
    private int titleColor;
    private static final double COLOR_THRESHOLD = 180.0;

    private int getNotificationColorCompatInternal(Context mContext) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(DUMMY_TITLE);
        Notification notification = builder.build();

        ViewGroup notificationRoot = (ViewGroup) notification.contentView.apply(mContext, new FrameLayout(mContext));


        TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);

        if (title == null) {//如果ROM厂商更改了默认的id
            //找到text为"DUMMY_TITLE"的textview并获取颜色

            iteratorView(notificationRoot, new Filter() {//遍历notificationRoot
                @Override
                public void filter(View view) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        if (DUMMY_TITLE.equals(textView.getText().toString())) {
                            titleColor = textView.getCurrentTextColor();
                        }
                    }
                }
            });
            return titleColor;
        } else {
            return title.getCurrentTextColor();
        }
    }

    private int getNotificationColorCompat(Context mContext) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        Notification notification = builder.build();

        int layoutId = notification.contentView.getLayoutId();

        ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId, null);

        TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);

        if (title == null) {
            //想办法从notificationroot中找到title对应的textview

            //先拿到所有的TextView

            final List<TextView> textViews = new ArrayList<TextView>();
            iteratorView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView) {
                        textViews.add((TextView) view);
                    }
                }
            });

            ///可以 认为字号最大的TextView就是title

            float minTextSize = Integer.MIN_VALUE;
            int index = 0;

            for (int i = 0, j = textViews.size(); i < j; i++) {
                float currentSize = textViews.get(i).getTextSize();
                if (currentSize > minTextSize) {
                    minTextSize = currentSize;
                    index = i;
                }
            }
            return textViews.get(index).getCurrentTextColor();

        } else {
            return title.getCurrentTextColor();
        }
    }


    private void iteratorView(View view, Filter filter) {
        if (view == null || filter == null) {
            return;
        }
        filter.filter(view);

        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            for (int i = 0, j = container.getChildCount(); i < j; i++) {
                View child = container.getChildAt(i);
                iteratorView(child, filter);
            }
        }
    }


    private interface Filter {
        void filter(View view);
    }

    public static boolean isColorSimilar(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < COLOR_THRESHOLD) {
            return true;
        }
        return false;
    }
}
