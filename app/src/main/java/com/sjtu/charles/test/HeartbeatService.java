package com.sjtu.charles.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by zhuyifei on 2016/6/12.
 */
public class HeartbeatService extends Service implements Runnable {
    private static final String TAG = "HeartbeatService";
    private static final long TIME_INTERVAL = 1000 * 3;
    private Thread mThread;
    public int count = 0;
    private static String mRestMsg;
    private boolean flag = true;

    @Override
    public void run() {
        while (flag) {
            try {
                if (!TextUtils.isEmpty(mRestMsg)) {
                    //向服务器发送心跳包
                    sendHeartbeatPackage(mRestMsg);
                    count += 1;
                    Thread.sleep(TIME_INTERVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "service onStart");
        //从本地读取服务器的URL，如果没有就用传进来的URL
        if (intent != null) {
            mRestMsg = intent.getExtras().getString("url");
            mThread = new Thread(this);
            mThread.start();
            count = 0;
        }
        super.onStart(intent, startId);
    }

    private void sendHeartbeatPackage(String url) {
        Log.i(TAG,"sendHeartbeatPackage count" + count);
        HttpManager.operation(url, new HttpManager.Ope() {
            @Override
            public void onResponseOk(HttpURLConnection con) {
                try {
                    String content = HttpManager.readContent(con.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,"sendHeartbeatPackage onResponseOk");
            }

            @Override
            public void onResponseFailed(HttpURLConnection con) {
                try {
                    flag = false;
                    String content = HttpManager.readContent(con.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,"sendHeartbeatPackage onResponseFailed");
            }
        });
    }
}