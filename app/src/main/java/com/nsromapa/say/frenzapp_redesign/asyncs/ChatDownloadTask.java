package com.nsromapa.say.frenzapp_redesign.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.app.adprogressbarlib.AdCircleProgress;
import com.nsromapa.say.frenzapp_redesign.models.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.widget.ChatView.updater;

public class ChatDownloadTask extends AsyncTask<String, Integer, String> {
    private AdCircleProgress adCircleProgress;
    private ImageView downloadImageView;
    private Message message;
    private Context context;

    public ChatDownloadTask(Context context,
                            AdCircleProgress adCircleProgress,
                            ImageView downloadImageView,
                            Message message) {
        this.context = context;
        this.adCircleProgress = adCircleProgress;
        this.downloadImageView = downloadImageView;
        this.message = message;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (downloadImageView != null)
            downloadImageView.setVisibility(View.GONE);
        if (adCircleProgress != null)
            adCircleProgress.setVisibility(View.VISIBLE);
    }


    @Override
    protected String doInBackground(String... sUrl_fileName) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String fullPath = Environment.getExternalStorageDirectory().getPath() + sUrl_fileName[1] + sUrl_fileName[2];
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + sUrl_fileName[1]);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(Environment.getExternalStorageDirectory().getPath() + sUrl_fileName[1] + "/.nomedia");
            try {
                if (!file.exists() && (sUrl_fileName[1].contains("/Sent/")
                        || sUrl_fileName[1].contains("/sticker/")
                        || sUrl_fileName[1].contains("/gifs/") ||
                        sUrl_fileName[1].contains("/sounds/")))
                    file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            URL url = new URL(sUrl_fileName[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(fullPath);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            Log.e("ChatDownloadTask", Objects.requireNonNull(e.getMessage()));
            return null;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return fullPath;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        if (adCircleProgress != null)
            adCircleProgress.setAdProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
//        mWakeLock.release();
        if (result == null) {
            if (downloadImageView != null)
                downloadImageView.setVisibility(View.VISIBLE);
            if (adCircleProgress != null)
                adCircleProgress.setVisibility(View.GONE);
            Log.e("ChatDownloadTask", "Download error");
        } else {
            if (message != null) {
                if (result.contains("/FrenzApp/Media/Videos/")) {
                    message.setVideoLocalLocation(result);
                } else if (result.contains("/FrenzApp/Media/Audios/")) {
                    message.setAudioLocalLocation(result);
                } else if (result.contains("/FrenzApp/Media/Images/")
                        || result.contains("/FrenzApp/Media/stickers/")
                        || result.contains("/FrenzApp/Media/gifs/")) {
                    message.setImageLocalLocation(result);
                }
                updater();
            }
            Log.e("ChatDownloadTask", "onPostExecute ------> File downloaded to : " + result);
        }
    }
}