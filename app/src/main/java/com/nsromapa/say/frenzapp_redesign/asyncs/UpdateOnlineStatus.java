package com.nsromapa.say.frenzapp_redesign.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.STATUS;

public class UpdateOnlineStatus extends AsyncTask<Void, Void, Void> {
    private RequestQueue requestQueue;
    private Context context;
    private String setMyOnlineStatus;
    private String whoseStatus;

    public UpdateOnlineStatus(Context context, String setMyOnlineStatus, String whoseStatus) {
        this.context = context;
        this.setMyOnlineStatus = setMyOnlineStatus;
        this.whoseStatus = whoseStatus;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATUS,
                response -> {}, error -> { }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid());
                post.put("setMyOnlineStatus", setMyOnlineStatus);
                post.put("whoseStatus", whoseStatus);
                return post;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        requestQueue.add(stringRequest);
        return null;
    }
}
