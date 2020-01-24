package com.nsromapa.say.frenzapp_redesign.models;

import android.media.MediaRecorder;

public enum AudioSource {
    MIC,
    CAMCORDER;

    public int getSource(){
        if (this == AudioSource.CAMCORDER) {
            return MediaRecorder.AudioSource.CAMCORDER;
        }
        return MediaRecorder.AudioSource.MIC;
    }
}