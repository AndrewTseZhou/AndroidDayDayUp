package com.andrewtse.testdemo.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.andrewtse.testdemo.LaunchActivity;
import com.andrewtse.testdemo.R;

public class PendingIntentActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "channelId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_intent);
        createNotificationChannel(this);

        findViewById(R.id.btn_send_system_notification).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifySystemMsg();
            }
        });

        findViewById(R.id.btn_send_custom_notification).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyCustomMsg();
            }
        });

        findViewById(R.id.btn_send_fold_notification).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyFoldMsg();
            }
        });

        findViewById(R.id.btn_send_fly_notification).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyFlyMsg();
            }
        });
    }

    private void notifySystemMsg() {
        int count = 0;
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
//        intent.setClass(this, LaunchActivity.class);
        //这里放一个count用来区分每一个通知
        intent.putExtra("intent", "intent--->" + count);//这里设置一个数据,带过去
        Uri uri = Uri.parse("http:///baidu.com");//我这里还可以给一个uri参数,点击notification可以打开百度首页,但是intent.setClass就不可以要了
        intent.setData(uri);//设置uri到intent中
        intent.setAction(Intent.ACTION_VIEW);//设置为展示uri的内容,系统会自动给出可以打开uri的应用,需要你选择

        //参数1:context 上下文对象
        //参数2:发送者私有的请求码(Private request code for the sender)
        //参数3:intent 意图对象
        //参数4:必须为FLAG_ONE_SHOT,FLAG_NO_CREATE,FLAG_CANCEL_CURRENT,FLAG_UPDATE_CURRENT,中的一个
        PendingIntent pi = PendingIntent.getActivity(this, count, intent, PendingIntent.FLAG_CANCEL_CURRENT);//用户点击该notification后才启动SecondActivity类

//        intent.putExtra("intent", "other intent--->" + count);
//        Uri uri = Uri.parse("http:///baidu.com");//我这里还可以给一个uri参数,点击notification可以打开百度首页,但是intent.setClass就不可以要了
//        intent.setData(uri);//设置uri到intent中
//        intent.setAction(Intent.ACTION_VIEW);//设置为展示uri的内容,系统会自动给出可以打开uri的应用,需要你选择
//        //用bundle来传参
//        Bundle bundle = new Bundle();
//        bundle.putString("key", "bundle string--->" + count);
//        intent.putExtra("bundle", bundle);//这里把bundle放到intent中,可以在SecondActivity获取出来
//
//        //参数1,2,3,4和前面的一样
//        //最后一个参数是:指定目标activity如何创建的额外参数
//        pi = PendingIntent.getActivity(this, count, intent, PendingIntent.FLAG_ONE_SHOT, bundle);//API 16以上
//        pi = PendingIntent.getService(this, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setContentText("通知内容" + count)
                .setContentTitle("通知标题" + count)
                .setTicker("this is msg's hint count--->" + count)
                .setNumber(count);
        Notification notification = builder.build();//这个需要在API 16以上才可以用,这里向下兼容到API 14(4.0)
        count++;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;//这里指定这个通知点击之后或者可以被清除按钮清除。 FLAG_NO_CLEAR 通知不能取消
        //参数1:此notification的识别好
        nm.notify(101, notification);
    }

    @RequiresApi(api = VERSION_CODES.N)
    private void notifyCustomMsg() {
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, LaunchActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
//        remoteViews.setTextViewText(, );
//        remoteViews.setImageViewResource(, );
//        remoteViews.setOnClickPendingIntent(, pi);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setCustomContentView(remoteViews)
                .setContentIntent(pi);
        Notification notification = builder.build();//这个需要在API 16以上才可以用,这里向下兼容到API 14(4.0)
        nm.notify(102, notification);
    }

    private void notifyFoldMsg() {
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification_fold);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("折叠式通知");
//               .setCustomBigContentView(remoteViews);//require API21
        Notification notification = builder.build();
        notification.bigContentView = remoteViews;
        nm.notify(1, notification);
    }

    private void notifyFlyMsg() {
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("悬挂式通知")
                .setFullScreenIntent(pi, true)
                .setAutoCancel(true);
        Notification notification = builder.build();
        nm.notify(2, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) {
            throw new IllegalStateException("Fail to get NotificationManager when create notification channel.");
        }
        NotificationChannel channel = nm.getNotificationChannel(CHANNEL_ID);
        if (channel != null) {
            return;
        }
        channel = new NotificationChannel(CHANNEL_ID, "通知栏消息", NotificationManager.IMPORTANCE_HIGH);
        nm.createNotificationChannel(channel);
    }
}
