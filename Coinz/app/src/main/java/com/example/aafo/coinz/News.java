package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.Random;

public class News extends AppCompatActivity {

    private NewsStories[] news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if(getFirstTime(News.this)){
            showHelpDialog();
        }

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
        builder.setMessage(news[0].getDescription())
                .setTitle(news[0].getTitle());
        AlertDialog dialog = builder.create();
        setNumNewsToday(News.this, getNumNewsToday(News.this)+1);
        dialog.show();
    }

    public void showNextNew(){
        int next = getNumNewsToday(News.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(News.this);
        builder.setMessage(news[next].getDescription())
                .setTitle(news[next].getTitle());
        AlertDialog dialog = builder.create();
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
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just close the dialog
                    }
                })
                .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Float gold = MainActivity.getGold(News.this);
                        if(gold>=500.0f){
                            gold = gold - 500;
                            MainActivity.setGold(News.this, gold);
                            showNextNew();
                        }
                    }
                });
        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNew(){
        int numNew = getNumNewsToday(News.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(News.this);

        builder.setMessage("uyt");
    }


    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getNumNewsToday(Context context){
        return getPrefs(context).getInt("Num news today", 0);
    }

    public static void setNumNewsToday(Context context, int num){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Num news today", num);
        editor.commit();
    }

    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time news", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time news", false);
        editor.commit();
        return ret;
    }

    private static boolean getFirstTimeToday(Context context){
        boolean ret = getPrefs(context).getBoolean("First time news today", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time news today", false);
        editor.commit();
        return ret;
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showHelpDialog();
    }

    public void showHelpDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Help").setMessage(R.string.news_explanation);
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
