package com.example.aafo.coinz;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;

import java.util.Scanner;

public class DownloadFileTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls){
        try{
            return loadFileFromNetwork(urls[0]);
        } catch (IOException e){
            return "Unable to load content. Check your network connection";
        }
    }

    private String loadFileFromNetwork( String urlString) throws IOException{
        return readStream(downloadUrl(new URL(urlString)));
    }

    private InputStream downloadUrl(URL url) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @NonNull
    private String readStream(InputStream stream) throws IOException {
            //Read input from stream, build result as a string
        //String toReturn = stream.getText();
        //Code from https://stackoverflow.com/questions/309424/how-to-read-convert-an-inputstream-into-a-string-in-java
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
        //return toReturn;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        DownloadCompleteRunner.downloadComplete(result);
    }
}
