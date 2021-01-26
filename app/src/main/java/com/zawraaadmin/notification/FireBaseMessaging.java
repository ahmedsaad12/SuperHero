package com.zawraaadmin.notification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zawraaadmin.R;
import com.zawraaadmin.models.NotModel;
import com.zawraaadmin.preferences.Preferences;
import com.zawraaadmin.tags.Tags;
import com.zawraaadmin.ui.activity_notifications.NotificationActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class FireBaseMessaging extends FirebaseMessagingService {

    Preferences preferences = Preferences.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> map = remoteMessage.getData();

        for (String key : map.keySet()) {
            Log.e("keys", key + "    value " + map.get(key));
        }


        manageNotification(map);


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewNotificationVersion(map);
        } else {
            createOldNotificationVersion(map);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createNewNotificationVersion(Map<String, String> map) {

        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;


        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

        String title = null;
        title = map.get("ar_title");
        String content = map.get("ar_desc");
        Log.e("ldldkdk",current_class);
        NotModel notModel=new NotModel();
            if (current_class.equals("com.zawraaadmin.ui.activity_notifications.NotificationActivity")){
                EventBus.getDefault().post(notModel);


            }
        sendNotification_VersionNew(content, title, sound_Path);

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createOldNotificationVersion(Map<String, String> map) {


        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;




        String title = null;
        title = map.get("ar_title");
        String content = map.get("ar_desc");

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.e("ldldkdk",current_class);
        NotModel notModel=new NotModel();
        if (current_class.equals("com.zawraaadmin.ui.activity_notifications.NotificationActivity")) {
            EventBus.getDefault().post(notModel);
        }
        sendNotification_VersionOld(content, title, sound_Path);
    }


    private void sendNotification_VersionOld(String content, String title, String sound_path) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.drawable.logo);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentText(content);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(12352, builder.build());

        }
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //    Log.e("mange","mang");
                if (manager != null) {

                    builder.setLargeIcon(bitmap);

                    manager.notify(new Random().nextInt(200), builder.build());
                    //  Log.e("mange","mang");

                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Picasso.get().load(R.drawable.logo).into(target);


                    }
                }, 1);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification_VersionNew(String content, String title, String sound_path) {

        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.drawable.logo);
        builder.setAutoCancel(true);

        builder.setContentTitle(title);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        builder.setLargeIcon(bitmap);
        Intent intent = new Intent(this, NotificationActivity.class);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentText(content);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
            manager.notify(12352, builder.build());
        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (manager != null) {
                    builder.setLargeIcon(bitmap);
                    manager.createNotificationChannel(channel);
                    manager.notify(new Random().nextInt(200), builder.build());
                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }



            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Picasso.get().load(R.drawable.logo).into(target);

                    }
                }, 1);

    }


}
