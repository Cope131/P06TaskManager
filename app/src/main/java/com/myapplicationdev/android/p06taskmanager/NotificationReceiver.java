package com.myapplicationdev.android.p06taskmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

public class NotificationReceiver extends BroadcastReceiver {

    int requestCode = 123;
    int notificationID = 888;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Data
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        String description = bundle.getString("description");
        long id = bundle.getLong("id");
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.sds);

        // Register Notification Channel
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("This is for default notification");
            channel.enableLights(true);
            channel.setSound(sound, attributes);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Notification Destination
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent addIntent = new Intent(context, AddActivity.class);
        PendingIntent pendingAddIntent = PendingIntent.getActivity(context, requestCode, addIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Wear Notification Actions
        NotificationCompat.Action launchAppAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Launch Task Manager", pendingIntent)
                .build();

        RemoteInput ri = new RemoteInput.Builder("status")
                .setLabel("Status Report")
                .setChoices(new String [] {"Completed", "Not yet"})
                .build();
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Reply", pendingIntent)
                .addRemoteInput(ri)
                .build();

        ri = new RemoteInput.Builder("task")
                .setLabel("Add New Task")
                .build();
        NotificationCompat.Action addTaskAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Add New Task", pendingAddIntent)
                .addRemoteInput(ri)
                .build();

        // Add Wear Actions
        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender()
                .addAction(launchAppAction)
                .addAction(replyAction)
                .addAction(addTaskAction);

        // Create Notification
        Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.sentosa);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                    .setContentTitle("Task Manager Reminder")
                    .setContentText(name + "\n" + description)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setLargeIcon(pic)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(pic)
                    .bigLargeIcon(null))
                .setAutoCancel(true)
                .setSound(sound)
                .setVibrate(new long[] { 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .extend(extender);

        // Show Notification
        Notification n = builder.build();
        notificationManager.notify(123, n);
    }
}