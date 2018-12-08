package com.example.aafo.coinz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Menu extends AppCompatActivity {

    private Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Menu");
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
}
