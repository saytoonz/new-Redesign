package com.nsromapa.say.frenzapp_redesign.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class BootCompleteService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toasty.normal(this,"My Service Stopped").show();
        Log.e("BootCompleteService", "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isMainActivityActive", false)) {
            Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
            Log.d("BootCompleteService", "onStart");
        }else {
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }

    }

}
