package com.example.aafo.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(SendFriends.this, user.getEmail(), Toast.LENGTH_SHORT).show();

    }

    public void goToSend(View view){
        startActivity(new Intent(SendFriends.this, SendCoins.class));
    }

    public void receiveCoins(View view){
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();

        database.collection(email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    HashMap<String, String[]> coinsFriendsOverall = MainActivity.getCoinsFriends(SendFriends.this);
                    List<DocumentSnapshot> coins = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot document:coins){
                        String id = document.getId();
                        Map<String, Object> values = document.getData();
                        Object[] currency = values.keySet().toArray();
                        String value = currency[0].toString();
                        String curr = values.get(currency[0]).toString();
                        MainActivity.addCoinsFriends(SendFriends.this, id, curr, value);
                        Toast.makeText(SendFriends.this, "Currency "+ curr+ " Value " + value, Toast.LENGTH_SHORT).show();
                        coinsFriendsOverall.put(id, new String[]{value, curr});
                        MainActivity.setCoinsOverallFriends(SendFriends.this, coinsFriendsOverall);

                        if(curr.equals("QUID")){
                            MainActivity.addQuidFriends(SendFriends.this, 1);
                        }
                        else if(curr.equals("DOLR")){
                            MainActivity.addDolrFriends(SendFriends.this, 1);
                        }
                        else if(curr.equals("PENY")){
                            MainActivity.addPennyFriends(SendFriends.this, 1);
                        }
                        else if(curr.equals("SHIL")){
                            MainActivity.addShilFriends(SendFriends.this, 1);
                        }

                    }
                    MainActivity.setCoinsFriends(SendFriends.this);
                }
                else{
                    Toast.makeText(SendFriends.this, "No coins were found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
