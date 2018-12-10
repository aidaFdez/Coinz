package com.example.aafo.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Menu");

        if (getFirstTime(Menu.this)){
            showHepDialog();
        }
    }


    public void goToBank (View view){
        Intent intBank = new Intent(Menu.this, Bank.class);
        startActivity(intBank);
    }

    public void goToMedals (View view){
        Intent intMed = new Intent(Menu.this, Medals.class);
        startActivity(intMed);
    }

    public void goToNews (View view){
        Intent intNews = new Intent(Menu.this, News.class);
        startActivity(intNews);
    }

    public void goToSend (View view){
        Intent intSend = new Intent(Menu.this, SendFriends.class);
        startActivity(intSend);
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHepDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Help").setMessage(R.string.menu_explanation);
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time menu", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time menu", false);
        editor.apply();
        return ret;
    }
}
