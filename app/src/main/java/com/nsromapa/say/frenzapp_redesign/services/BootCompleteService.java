package com.nsromapa.say.frenzapp_redesign.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;


import es.dmoral.toasty.Toasty;

public class BootCompleteService extends Service {
    private Handler handler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toasty.normal(this, "My Service Stopped").show();
        Log.e("BootCompleteService", "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runThisFunction1();
                handler.postDelayed(this, 300);
            }
        };
        handler.postDelayed(runnable, 700);
    }







    protected void runThisFunction1(){
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isMainActivityActive", false)) {
            Toast.makeText(this, "Main Activity Active", Toast.LENGTH_LONG).show();
            Log.d("BootCompleteService", "onStart");
        }else {
            Toast.makeText(this, "Main Activity Closed", Toast.LENGTH_LONG).show();
        }
    }

}
