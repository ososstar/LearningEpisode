package com.android.ososstar.learningepisode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class sslService extends Service {
    public sslService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //allow HTTP SSL connections for older devices
        HttpsTrustManager.allowAllSSL();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return sslService.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
