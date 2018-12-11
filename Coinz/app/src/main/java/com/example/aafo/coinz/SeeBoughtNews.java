package com.example.aafo.coinz;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class SeeBoughtNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_bought_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Bought news");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Bought news");

        ArrayList<NewsStories> news = News.getNewsSaved(SeeBoughtNews.this);
        StringBuilder toShow = new StringBuilder();
        if(news.size()==1 && news.get(0).getTitle().equals("")){
            toShow.append("You have not seen any news yet");
        }else{
            for(NewsStories newNew : news){
                Log.d("Setting to string", newNew.getTitle());
                toShow.append(newNew.getTitle()).append("\n \n").append(newNew.getDescription()).append("\n\n\n\n");
            }
        }

        TextView textView = findViewById(R.id.show_news);
        textView.setText(toShow.toString());
    }
}
