package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnBT = findViewById(R.id.bigText);
        Button btnIS = findViewById(R.id.inbox);
        Button btnBP = findViewById(R.id.bigImage);
        Button btnClose = findViewById(R.id.close);

        btnBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification(null,0);
            }
        });
        btnIS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification(null,1);
            }
        });
        btnBP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotificationWithImage();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Please swipe the notification to dismiss", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void showNotificationWithImage(){
        new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... strings) {
                InputStream inputStream;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                } catch (Exception ignored){

                }
                return null;
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                showNotification(bitmap, 2);
            }
        }.execute("https://www.computerhope.com/jargon/a/android.png");
    }

    private void showNotification(Bitmap bitmap, int type){
        int notificationID = new Random().nextInt(100);
        String channelID = "notification_channel_1";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channelID);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        if(type == 0) {
            builder.setContentTitle("New Big Text Notification");
            builder.setContentText("Content for Big Text Notification");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Android is a mobile operating system based on a modified version of the Linux kernel and other open source software, designed primarily for touchscreen mobile devices such as smartphones and tablets. Android is developed by a consortium of developers known as the Open Handset Alliance and commercially sponsored by Google. It was unveiled in November 2007, with the first commercial Android device, the HTC Dream, being launched in September 2008."));
        }
        else if(type == 1){
            builder.setContentTitle("New Inbox Style Notification");
            builder.setContentText("Content for Inbox Style Notification");
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .addLine("1st message text")
                    .addLine("2nd message text")
                    .addLine("3rd message text")
                    .addLine("4th message text")
            );
        }
        else {
            builder.setContentTitle("New Big Image Notification");
            builder.setContentText("Content for Big Image Notification");
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                NotificationChannel notificationChannel = new NotificationChannel(channelID,"Notification Channel 1",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This is the first notification channel");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(notificationID, notification);
        }
    }
}