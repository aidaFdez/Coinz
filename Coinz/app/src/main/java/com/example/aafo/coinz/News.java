package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class News extends AppCompatActivity {

    private NewsStories[] news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //If this is the first time the user opens the activity, show the tutorial
        if(getFirstTime(News.this)){
            showHelpDialog();
        }

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Newsstand");

        //Get the news for the day
        news = NewsStories.getNew();
    }

    public void onSeeingNews(View view){
        //If the user has seen all the news of the day, show a dialog
        if(getNumNewsToday(News.this)>=4){
            AlertDialog.Builder builder = new AlertDialog.Builder(News.this);
            builder.setMessage("You have seen all the news of the day. You can check them again in \"See bought news\"")
                    .setTitle("Sold out of news");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        //If the user is checking the first new of the day, do it for free. If not, check tha they want to buy the news
        if(getFirstTimeToday(News.this)){
            showFirstNew();
        }else{
            showDialogBuy();
        }
    }

    public void showFirstNew(){
        AlertDialog.Builder builder = new AlertDialog.Builder(News.this);
        NewsStories newToShow = news[0];
        builder.setMessage(newToShow.getDescription())
                .setTitle(newToShow.getTitle());
        AlertDialog dialog = builder.create();
        saveNew(News.this, newToShow);
        setNumNewsToday(News.this, getNumNewsToday(News.this)+1);
        dialog.show();
    }

    public void showNextNew(){
        int next = getNumNewsToday(News.this);
        NewsStories newToShow = news[next];
        AlertDialog.Builder builder = new AlertDialog.Builder(News.this);
        builder.setMessage(newToShow.getDescription())
                .setTitle(newToShow.getTitle());
        AlertDialog dialog = builder.create();
        saveNew(News.this, newToShow);
        setNumNewsToday(News.this, getNumNewsToday(News.this)+1);
        dialog.show();
    }

    public void showDialogBuy(){
        //Code from https://developer.android.com/guide/topics/ui/dialogs?hl=en-419#java

        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(News.this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Do you want to spend 500 gold to buy some news?")
                .setTitle("Buy news")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //Just close the dialog
                })
                .setPositiveButton("Purchase", (dialog, which) -> {
                    Float gold = MainActivity.getGold(News.this);
                    if(gold>=500.0f){
                        gold = gold - 500;
                        MainActivity.setGold(News.this, gold);
                        showNextNew();
                    }
                    else{
                        Toast.makeText(this, "You do not have enough gold", Toast.LENGTH_SHORT).show();
                    }
                });
        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //get the number of news the user has seen during the day
    public static int getNumNewsToday(Context context){
        return getPrefs(context).getInt("Num news today", 0);
    }

    public static void setNumNewsToday(Context context, int num){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Num news today", num);
        editor.apply();
    }

    //Check if it is the first time ever the user opens the newsstand
    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time news", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time news", false);
        editor.apply();
        return ret;
    }

    //Check if this is the first time of the day that the newsstand has been opened
    private static boolean getFirstTimeToday(Context context){
        boolean ret = getPrefs(context).getBoolean("First time news today", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time news today", false);
        editor.apply();
        return ret;
    }
    //Reset the firs time of the day
    public static void resetFirstTimeToday(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time news today", true);
        editor.apply();
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showHelpDialog();
    }

    public void showHelpDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Newsstand").setMessage(R.string.news_explanation)
                .setPositiveButton("OK", (dialog, which) -> {
                    //Nothing, just closes
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void saveNew(Context context, NewsStories news){
        //Get the news that were already stored and add the new one
        ArrayList<NewsStories> saved = getNewsSaved(context);
        saved.add(news);
        StringBuilder debug = new StringBuilder();
        for(NewsStories nnew:saved){
            debug.append(nnew.getTitle()).append("\n").append(nnew.getDescription()).append("\n");
        }
        Log.d("Saving news", debug.toString());
        Gson gson = new Gson();
        String newsString = gson.toJson(saved);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("newsSeen", newsString);
        editor.apply();
    }

    public static ArrayList<NewsStories> getNewsSaved(Context context){
        Gson gson = new Gson();
        ArrayList<NewsStories> empty = new ArrayList<>();
        empty.add(new NewsStories("", ""));
        String storedString = getPrefs(context).getString("newsSeen", "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<NewsStories>>(){}.getType();
        //If there is no stored array, then return default one
        if (storedString.equals("")){
            return empty;
        }
        //Transform the string into an array and return it
        return gson.fromJson(storedString, type);
    }

    public static void resetNewsSeen(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("newsSeen", "");
        editor.apply();
    }

    public void goToSeen(View view){
        Intent intSeen = new Intent(News.this, SeeBoughtNews.class);
        startActivity(intSeen);
    }
}
