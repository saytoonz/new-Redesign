package com.nsromapa.say.frenzapp_redesign.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.nsromapa.say.frenzapp_redesign.services.BootCompleteService;

public class BootCompleteBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, BootCompleteService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.i("BootCompleteBroadcast", "started");
    }
}
