package com.example.aafo.coinz;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;

import java.util.Scanner;
import java.util.logging.Logger;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private static Logger logger = Logger.getLogger("DownloadFileTask");

    //Code based on https://stackoverflow.com/questions/23351904/getting-cannot-resolve-method-error-when-trying-to-implement-getsharedpreferen
    /*private SharedPreferences sharedPrefs;
    private static String PREF_NAME = "preferences";

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setJson(Context context, String map){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Json", map);
        editor.commit();
    }*/
    //Finished the code from the previous link

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
        logger.finer("readStream() has been called");
        //Code from https://stackoverflow.com/questions/309424/how-to-read-convert-an-inputstream-into-a-string-in-java
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String toRet = s.hasNext() ? s.next() : "";
        //TODO Guardar en la sharedprefs el mapa
        //setJson(this, toRet);
        return toRet;
        //return toReturn;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        DownloadCompleteRunner.downloadComplete(result);
    }
}
