package com.example.aafo.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SendFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friends);
    }

    public void goToSend(View view){
        startActivity(new Intent(SendFriends.this, SendCoins.class));
    }
}
