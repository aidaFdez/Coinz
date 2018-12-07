package com.example.aafo.coinz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;


//I followed the code in https://guides.codepath.com/android/using-the-recyclerview
public class Medals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);
        addGoals();

        RecyclerView rvGoals = (RecyclerView) findViewById(R.id.goalsRV);
        Goal[] goals = Goal.getGoals(Medals.this);
        //Create adapter passing the data
        GoalsAdapter adapter = new GoalsAdapter(goals);
        //Attach the adapter to the recycler view
        rvGoals.setAdapter(adapter);
        //Set layout manager to position the items
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addGoals(){
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.add(new Goal("Collector", "Collect 50 coins in one day"));
        goals.add(new Goal("Friendly person", "Send 10 coins to your friends"));
        goals.add(new Goal("New bank account", "Cash in your first coin"));
        goals.add(new Goal("Supportive user", "Play the game seven days in a row"));

        Goal.setGoals(this, goals);
    }

    //Check the goals to see if a new one has been achieved.
    public static boolean checkGoals(Context context){
        Goal[] goals = Goal.getGoals(context);
        boolean newAchieved = false;

        for(Goal goal:goals){
            if(goal.getAchieved()==false){
                if(goal.getName().equals("Collector")){
                    if(MainActivity.getPickedCoins(context).size()>=50){
                        goal.setAchieved(true);
                        newAchieved=true;
                    }
                }
                else if(goal.getName().equals("Friendly person")){
                    if(SendCoins.getNumCoinsSent(context)>=10){
                        newAchieved=true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("New bank account")){
                    if(Bank.getCoinsCashedTotal(context)>=1){
                        newAchieved=true;
                        goal.setAchieved(true);
                    }
                }

            }

        }

        return newAchieved;
    }
}
