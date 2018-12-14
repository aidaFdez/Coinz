package com.example.aafo.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendFriends extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friends);

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Send coins to friends");

        if(getFirstTime(SendFriends.this)){
            showHelpDialog();
        }

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void goToSend(View view){
        startActivity(new Intent(SendFriends.this, SendCoins.class));
    }

    public void receiveCoins(View view){
        String TAG = "receiveCoins";
        Log.d(TAG, "Receiving coins from Firebase");
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String email = user.getEmail();

        assert email != null;
        database.collection(email).get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "The data was downloaded successfully");
            if (!queryDocumentSnapshots.isEmpty()){
                //Getting all the needed lists of coins
                HashMap<String, String[]> coinsFriendsOverall = MainActivity.getCoinsFriends(SendFriends.this);
                List<DocumentSnapshot> coins = queryDocumentSnapshots.getDocuments();
                HashMap<String, Integer> newCoins = new HashMap<>();
                newCoins.put("Quid", 0);
                newCoins.put("Shilling", 0);
                newCoins.put("Dollar", 0);
                newCoins.put("Penny", 0);
                for(DocumentSnapshot document:coins){
                    //Get the name of the document, which is the coin ID
                    String id = document.getId();
                    //Get the data from the document
                    Map<String, Object> values = document.getData();
                    assert values != null;
                    Object[] currency = values.keySet().toArray();
                    String value = currency[0].toString();
                    String curr = values.get(currency[0]).toString();
                    //Add the new coin to the proper variables
                    MainActivity.addCoinsFriends(SendFriends.this, id, curr, value);
                    Log.d(TAG,"Saving coin with id"+ id +" currency "+ curr+ " Value " + value);
                    coinsFriendsOverall.put(id, new String[]{value, curr});
                    MainActivity.setCoinsOverallFriends(SendFriends.this, coinsFriendsOverall);

                    //Set the appropriate variable depending on the currency
                    switch (curr) {
                        case "QUID": {
                            int added = newCoins.get("Quid") + 1;
                            newCoins.put("Quid", added);
                            break;
                        }
                        case "DOLR": {
                            int added = newCoins.get("Dollar") + 1;
                            newCoins.put("Dollar", added);
                            break;
                        }
                        case "PENY": {
                            int added = newCoins.get("Penny") + 1;
                            newCoins.put("Penny", added);
                            break;
                        }
                        case "SHIL": {
                            int added = newCoins.get("Shilling") + 1;
                            newCoins.put("Shilling", added);
                            break;
                        }
                    }
                    //Delete the coin from the database
                    database.collection(email).document(id).delete();

                }
                MainActivity.setCoinsFriends(SendFriends.this);

                //Write in a string the coins that have been received by the user
                StringBuilder toShow = new StringBuilder();
                int received = 0;
                for(String currency : newCoins.keySet()){
                    toShow.append(currency).append(": ").append(newCoins.get(currency)).append("\n");
                    received = received+newCoins.get(currency);
                }

                //Build an alert dialog to tell the user what coins they hae received from friends
                AlertDialog.Builder builder  = new AlertDialog.Builder(SendFriends.this);
                setNumCoinsReceived(SendFriends.this, received);
                builder.setMessage(toShow.toString()).setTitle("Coins received");
                AlertDialog dialog = builder.create();
                dialog.show();
                if(Medals.checkGoals(SendFriends.this)){
                    Toast.makeText(SendFriends.this, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(SendFriends.this, "No coins were found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(SendFriends.this, "The data could not be accessed", Toast.LENGTH_SHORT).show());
    }

    public void potOfGold(View view){
        //If this is the first time the user finds the pot of gold, show a dialog saying so
        if(firstTimePotGold(SendFriends.this)){
            AlertDialog.Builder builder  = new AlertDialog.Builder(SendFriends.this);
            builder.setTitle("Pot of gold").setMessage("Yay! You found the pot of gold! It has 50000 coins of gold, and they are all for you!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        //Nothing, just closes
                    });
            MainActivity.setGold(SendFriends.this, MainActivity.getGold(SendFriends.this)+50000);
            AlertDialog dialog = builder.create();
            dialog.show();
            setPotGold(SendFriends.this);
            Medals.checkGoals(SendFriends.this);
        }
    }

    public static boolean firstTimePotGold(Context context){return getPrefs(context).getBoolean("Pot gold", true); }

    private static void setPotGold(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("Pot gold", false);
        editor.commit();
    }

    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setNumCoinsReceived(Context context, int received){
        Log.d("setNumCoinsReceived", "Adding "+received+" to received coins");
        int sentBefore = getNumCoinsReceived(context);
        received = sentBefore+received;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("receivedCoins", received);
        editor.commit();
    }
    public static int getNumCoinsReceived(Context context){
        Log.d("getNumCoinsReceived", "Getting the number of coins that have been received");
        return getPrefs(context).getInt("receivedCoins", 0);
    }

    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time sendFriends", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time sendFriends", false);
        editor.commit();
        return ret;
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showHelpDialog();
    }

    public void showHelpDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Send coins to friends").setMessage(R.string.send_explanation)
                .setPositiveButton("OK", (dialog, which) -> {
                    //Nothing, just closes
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
