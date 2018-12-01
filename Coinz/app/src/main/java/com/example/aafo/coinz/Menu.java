package com.example.aafo.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
        Intent intSend = new Intent(Menu.this, LoginActivity.class);
        startActivity(intSend);
    }
}
