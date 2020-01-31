package com.nsromapa.say.frenzapp_redesign.asyncs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nsromapa.say.frenzapp_redesign.services.BootCompleteService;

public class CheckAndStartBootService extends AsyncTask<Void, Void, Void> {
    protected Context context;

    public CheckAndStartBootService(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Intent intent = new Intent(context, BootCompleteService.class);
        if(context.startService(intent) == null) {
            context.startService(intent);
        }
        return null;
    }
}
