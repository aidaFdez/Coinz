package com.example.aafo.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SendFriends extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friends);


        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //FirebaseUser user = mAuth.getCurrentUser();
        //Toast.makeText(SendFriends.this, user.getEmail(), Toast.LENGTH_SHORT).show();

    }

    public void goToSend(View view){
        startActivity(new Intent(SendFriends.this, SendCoins.class));
    }

    public void receiveCoins(View view){
        String TAG = "receiveCoins";
        Log.d(TAG, "Receiving coins from Firebase");
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();

        database.collection(email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "The data was downloaded successfully");
                if (!queryDocumentSnapshots.isEmpty()){
                    HashMap<String, String[]> coinsFriendsOverall = MainActivity.getCoinsFriends(SendFriends.this);
                    List<DocumentSnapshot> coins = queryDocumentSnapshots.getDocuments();
                    HashMap<String, Integer> newCoins = new HashMap<String, Integer>();
                    newCoins.put("Quid", 0);
                    newCoins.put("Shilling", 0);
                    newCoins.put("Dollar", 0);
                    newCoins.put("Penny", 0);
                    for(DocumentSnapshot document:coins){
                        //Get the name of the document, which is the coin ID
                        String id = document.getId();
                        //Get the data from the document
                        Map<String, Object> values = document.getData();
                        Object[] currency = values.keySet().toArray();
                        String value = currency[0].toString();
                        String curr = values.get(currency[0]).toString();
                        //Add the new coin to the proper variables
                        MainActivity.addCoinsFriends(SendFriends.this, id, curr, value);
                        Log.d(TAG,"Saving coin with id"+ id +" currency "+ curr+ " Value " + value);
                        coinsFriendsOverall.put(id, new String[]{value, curr});
                        MainActivity.setCoinsOverallFriends(SendFriends.this, coinsFriendsOverall);

                        //Set the appropriate variable depending on the currency
                        if(curr.equals("QUID")){
                            MainActivity.addQuidFriends(SendFriends.this, 1);
                            int added = newCoins.get("Quid") +1;
                            newCoins.put("Quid", added);
                        }
                        else if(curr.equals("DOLR")){
                            MainActivity.addDolrFriends(SendFriends.this, 1);
                            int added = newCoins.get("Dollar") +1;
                            newCoins.put("Dollar", added);
                        }
                        else if(curr.equals("PENY")){
                            MainActivity.addPennyFriends(SendFriends.this, 1);
                            int added = newCoins.get("Penny") +1;
                            newCoins.put("Penny", added);
                        }
                        else if(curr.equals("SHIL")){
                            MainActivity.addShilFriends(SendFriends.this, 1);
                            int added = newCoins.get("Shilling") +1;
                            newCoins.put("Shilling", added);
                        }
                        //Delete the coin from the database
                        database.collection(email).document(id).delete();

                    }
                    MainActivity.setCoinsFriends(SendFriends.this);

                    //Write in a string the coins that have been received by the user
                    String toShow = "";
                    for(String currency : newCoins.keySet()){
                        toShow = toShow + currency + ": " + newCoins.get(currency) + "\n";
                    }

                    //Build an alert dialog to tell the user what coins they hae received from friends
                    AlertDialog.Builder builder  = new AlertDialog.Builder(SendFriends.this);
                    builder.setMessage(toShow).setTitle("Coins received");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    Toast.makeText(SendFriends.this, "No coins were found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SendFriends.this, "The data could not be accessed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
