package com.example.aafo.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


//I followed the code in https://guides.codepath.com/android/using-the-recyclerview
public class Medals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Achievements");

        if(getFirstTime(Medals.this)){
            showHelpDialog();
        }


        RecyclerView rvGoals = findViewById(R.id.goalsRV);
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
                new Goal("Find the pot of gold", "There is a pot of gold hiding somewhere in the app..."),
                new Goal("Completer", "Complete all of the other goals"),
        };
        Goal.setGoals(context, goals);
    }

    //Check the goals to see if a new one has been achieved.
    public static boolean checkGoals(Context context){
        Goal[] goals = Goal.getGoals(context);
        boolean newAchieved = false;
        StringBuilder ret = new StringBuilder();
        boolean allComplete = true;

        for(Goal goal:goals){
            Log.d("Checking", goal.getName());
            if(!goal.getAchieved()){
                switch (goal.getName()) {
                    case "Collector":
                        if (MainActivity.getPickedCoins(context).size() >= 50) {
                            goal.setAchieved(true);
                            newAchieved = true;
                        }
                        break;
                    case "Friendly person":
                        if (SendCoins.getNumCoinsSent(context) >= 10) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Nice friends":
                        if (SendFriends.getNumCoinsReceived(context) >= 10) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Loved person":
                        if (SendFriends.getNumCoinsReceived(context) >= 50) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Cherished person":
                        if (SendFriends.getNumCoinsReceived(context) >= 100) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Worshiped person":
                        if (SendFriends.getNumCoinsReceived(context) >= 500) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "New bank account":
                        if (Bank.getCoinsCashedTotal(context) >= 1) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Saver":
                        if (MainActivity.getNumCoinsPicked(context) >= 50) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Accumulator":
                        if (MainActivity.getNumCoinsPicked(context) >= 100) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Hoarder":
                        if (MainActivity.getNumCoinsPicked(context) >= 500) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Small fortune":
                        if (MainActivity.getGold(context) >= 1000) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Fortune":
                        if (MainActivity.getGold(context) >= 10000) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Millionaire":
                        if (MainActivity.getGold(context) >= 1000000) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Billionaire":
                        if (MainActivity.getGold(context) >= 1000000000) {
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Informed":
                        if(News.getNumNewsToday(context)>=4){
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Find the pot of gold":
                        if(!SendFriends.firstTimePotGold(context)){
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                        break;
                    case "Completer":
                        if(allComplete){
                            newAchieved = true;
                            goal.setAchieved(true);
                        }
                }

            }
            allComplete = allComplete && goal.getAchieved();
            //String for the log to say which goals are completed and which are not
            ret.append(goal.getName()).append(" ").append(goal.getAchieved()).append("\n");
        }
        Log.d("Goals" , ret.toString());
        Goal.setGoals(context, goals);
        return newAchieved;
    }

    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time achievements", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time achievements", false);
        editor.commit();
        return ret;
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showHelpDialog();
    }

    public void showHelpDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Help").setMessage(R.string.achievements_explanation);
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
