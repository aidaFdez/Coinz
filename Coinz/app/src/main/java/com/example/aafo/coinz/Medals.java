package com.example.aafo.coinz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;


//I followed the code in https://guides.codepath.com/android/using-the-recyclerview
public class Medals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        RecyclerView rvGoals = (RecyclerView) findViewById(R.id.goalsRV);
        Goal[] goals = Goal.getGoals(Medals.this);
        //Create adapter passing the data
        GoalsAdapter adapter = new GoalsAdapter(goals);
        //Attach the adapter to the recycler view
        rvGoals.setAdapter(adapter);
        //Set layout manager to position the items
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
    }

    public static void addGoals(Context context){
        Log.d("addGoals", "Adding the goals");
        Goal[] goals = new Goal[]{
                new Goal("Collector", "Collect 50 coins in one day"),
                new Goal("Friendly person", "Send 10 coins to your friends"),
                new Goal("Nice friends", "Get 10 coins from your friends"),
                new Goal("Loved person", "Get 50 coins from your friends"),
                new Goal("Cherished person", "Get 100 coins from your friends"),
                new Goal("Worshiped person", "Get 500 coins from your friends"),
                new Goal("New bank account", "Cash in your first coin"),
                new Goal("Saver", "Collect a total of 50 coins"),
                new Goal("Accumulator", "Collect a total of 100 coins"),
                new Goal("Hoarder", "Collect a total of 500 coins"),
                new Goal("Small fortune", "Have your first thousand gold"),
                new Goal("Fortune", "Have 10000 gold in the bank"),
                new Goal("Millionaire", "Have a million gold in the bank"),
                new Goal("Billionaire", "Have a billion gold in the bank"),
                new Goal("Informed", "Check all the news from one day"),
                new Goal("Completer", "Complete all of the other goals"),
                new Goal("Find the pot of gold", "There is a pot of gold hiding somewhere in the app... As you " +
                        "are probably not a goblin, you can't see it, but you can still touch it. Good luck!"),

                new Goal("Supportive user", "Play the game seven days in a row")
        };
        Goal.setGoals(context, goals);
    }

    //Check the goals to see if a new one has been achieved.
    public static boolean checkGoals(Context context){
        Goal[] goals = Goal.getGoals(context);
        boolean newAchieved = false;
        String ret = "";

        for(Goal goal:goals){
            Log.d("Checking", goal.getName());
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
                else if(goal.getName().equals("Nice friends")){
                    if(SendFriends.getNumCoinsReceived(context)>=10){
                        newAchieved=true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Loved person")){
                    if(SendFriends.getNumCoinsReceived(context)>=50){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Cherished person")) {
                    if (SendFriends.getNumCoinsReceived(context) >= 100) {
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Worshiped person")) {
                    if (SendFriends.getNumCoinsReceived(context) >= 500) {
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("New bank account")){
                    if(Bank.getCoinsCashedTotal(context)>=1){
                        newAchieved=true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Saver")){
                    if(MainActivity.getNumCoinsPicked(context)>=50){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Accumulator")){
                    if(MainActivity.getNumCoinsPicked(context)>=100){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Hoarder")){
                    if(MainActivity.getNumCoinsPicked(context)>=500){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Small fortune")){
                    if(MainActivity.getGold(context)>=1000){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Fortune")){
                    if(MainActivity.getGold(context)>=10000){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Millionaire")){
                    if(MainActivity.getGold(context)>=1000000){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }
                else if(goal.getName().equals("Billionaire")){
                    if(MainActivity.getGold(context)>=1000000000){
                        newAchieved = true;
                        goal.setAchieved(true);
                    }
                }

            }
            ret = ret+goal.getName() + " " + goal.getAchieved()+"\n";
        }
        Log.d("Goals" , ret);
        Goal.setGoals(context, goals);
        return newAchieved;
    }
}
