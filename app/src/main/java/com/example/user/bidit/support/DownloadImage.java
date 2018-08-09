package com.example.user.bidit.support;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImage extends AsyncTask<String, Integer, String> {

    private DownloadAsyncTaskCallbacks mCallbacks;


    public DownloadImage() {
    }

    @Override
    protected void onPreExecute() {
        if (mCallbacks != null) {
            mCallbacks.onPreExecute();
        }
    }
    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();

            String storageDir = strings[1];
            String fileName = "/downloaded_image_for_app.jpg";

            byte[] data = new byte[1024];
            int total = 0;
            File imageFile = new File(storageDir + fileName);

            try (InputStream inputStream = new BufferedInputStream(url.openStream());
                 OutputStream outputStream = new FileOutputStream(imageFile)) {

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress(total * 100 / lengthOfFile);
                    outputStream.write(data, 0, count);
                }
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("-------", e.toString());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mCallbacks != null) {
            mCallbacks.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (mCallbacks != null) {
            mCallbacks.onPostExecute(s);
        }
    }

    public void setmCallbacks(DownloadAsyncTaskCallbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
    }

    public interface DownloadAsyncTaskCallbacks{
        void onPreExecute();
        void onPostExecute(String s);
        void onProgressUpdate(Integer i);
    }
}
