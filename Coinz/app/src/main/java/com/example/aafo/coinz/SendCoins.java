package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendCoins extends AppCompatActivity {

    private EditText emailToSend;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int shilCount = 0;
    private int dolrCount = 0;
    private int penyCount = 0;
    private int quidCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coins);

        mAuth = FirebaseAuth.getInstance();

        //displayInfo();
        displayDolr();
        displayPenny();
        displayQuid();
        displayShil();
        displayChosenDolr();
        displayChosenShil();
        displayChosenPeny();
        displayChosenQuid();

        emailToSend = (EditText) findViewById(R.id.email_to_send);


    }

    public void sendQuid(View view){
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference ref = firebaseDatabase.getReference(mAuth.getUid());
        //Map<String, String[]> coinsToSend = new HashMap<String, String[]>();

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numQuid = MainActivity.getNumQuid(SendCoins.this);

        if(numQuid>= quidCount) {
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            List<String> keys = new ArrayList(quidHash.keySet());

            for (int i = 0; i <quidCount; i++) {
                String id = keys.get(i);
                String[] charact = quidHash.get(id);
                //coinsToSend.put(keys.get(i), quidHash.get(keys.get(i)));
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                Gson gson = new Gson();
                String arraySendStr = gson.toJson(charact);
                int index = i;

                Map<String, String> map = new HashMap<String, String>();
                map.put(charact[0], charact[1]);


                db.collection(emailString).document(id).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.coinsQuid.remove(keys.get(index));
                        MainActivity.subQuid(SendCoins.this, 1);
                        coinsOverall.remove(keys.get(index));
                        Toast.makeText(SendCoins.this, "Coin "+index+" was sent successfully", Toast.LENGTH_SHORT).show();
                        displayQuid();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                        Log.d("Send coin", e.toString());
                    }
                });
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendShil(View view){
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference ref = firebaseDatabase.getReference(mAuth.getUid());
        //Map<String, String[]> coinsToSend = new HashMap<String, String[]>();

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numShil = MainActivity.getNumShil(SendCoins.this);

        if(numShil>= shilCount) {
            HashMap<String, String[]> shilHash = MainActivity.coinsShil;
            List<String> keys = new ArrayList(shilHash.keySet());

            for (int i = 0; i <shilCount; i++) {
                String id = keys.get(i);
                String[] charact = shilHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                Gson gson = new Gson();
                String arraySendStr = gson.toJson(charact);
                int index = i;

                Map<String, String> map = new HashMap<String, String>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.coinsShil.remove(keys.get(index));
                                MainActivity.subShil(SendCoins.this, 1);
                                coinsOverall.remove(keys.get(index));
                                Toast.makeText(SendCoins.this, "Coin "+index+" was sent successfully", Toast.LENGTH_SHORT).show();
                                displayShil();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                                Log.d("Send coin", e.toString());
                            }
                        });
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendDolr(View view){
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference ref = firebaseDatabase.getReference(mAuth.getUid());
        //Map<String, String[]> coinsToSend = new HashMap<String, String[]>();

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numDolr = MainActivity.getNumDolr(SendCoins.this);

        if(numDolr>= dolrCount) {
            HashMap<String, String[]> dolrHash = MainActivity.coinsQuid;
            List<String> keys = new ArrayList(dolrHash.keySet());

            for (int i = 0; i <dolrCount; i++) {
                String id = keys.get(i);
                String[] charact = dolrHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                Gson gson = new Gson();
                String arraySendStr = gson.toJson(charact);
                int index = i;

                Map<String, String> map = new HashMap<String, String>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.coinsDolr.remove(keys.get(index));
                                MainActivity.subDolr(SendCoins.this, 1);
                                coinsOverall.remove(keys.get(index));
                                Toast.makeText(SendCoins.this, "Coin "+index+" was sent successfully", Toast.LENGTH_SHORT).show();
                                displayDolr();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                                Log.d("Send coin", e.toString());
                            }
                        });
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendPeny(View view){
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference ref = firebaseDatabase.getReference(mAuth.getUid());
        //Map<String, String[]> coinsToSend = new HashMap<String, String[]>();

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numPenny = MainActivity.getNumPenny(SendCoins.this);

        if(numPenny>= penyCount) {
            HashMap<String, String[]> penyHash = MainActivity.coinsPeny;
            List<String> keys = new ArrayList(penyHash.keySet());

            for (int i = 0; i <penyCount; i++) {
                String id = keys.get(i);
                String[] charact = penyHash.get(id);
                //coinsToSend.put(keys.get(i), quidHash.get(keys.get(i)));
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                Gson gson = new Gson();
                String arraySendStr = gson.toJson(charact);
                int index = i;

                Map<String, String> map = new HashMap<String, String>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.coinsPeny.remove(keys.get(index));
                                MainActivity.subPenny(SendCoins.this, 1);
                                coinsOverall.remove(keys.get(index));
                                Toast.makeText(SendCoins.this, "Coin "+index+" was sent successfully", Toast.LENGTH_SHORT).show();
                                displayPenny();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                                Log.d("Send coin", e.toString());
                            }
                        });
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public static void notEnough(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You do not have enough coins to make the change.")
                .setTitle("Lack of coins")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayQuid(){
        int quid = MainActivity.getNumQuid(SendCoins.this);
        String toChange = "Quid collected: " + quid;
        final TextView textView = (TextView) findViewById(R.id.quid_send);
        textView.setText(toChange);
    }
    public void displayPenny(){
        int penny = MainActivity.getNumPenny(SendCoins.this);
        String toChange = "Pennies collected: " + penny;
        final TextView textView = (TextView) findViewById(R.id.penny_send);
        textView.setText(toChange);
    }
    public void displayDolr(){
        int dolr = MainActivity.getNumDolr(SendCoins.this);
        String toChange = "Dollars collected: " + dolr;
        final TextView textView = (TextView) findViewById(R.id.dollar_send);
        textView.setText(toChange);
    }
    public void displayShil(){
        int shil = MainActivity.getNumShil(SendCoins.this);
        String toChange = "Shillings collected: " + shil;
        final TextView textView = (TextView) findViewById(R.id.shilling_send);
        textView.setText(toChange);
    }

    public void displayChosenShil(){
        final TextView textView = (TextView) findViewById(R.id.num_shillin_send);
        textView.setText(Integer.toString(shilCount));
    }
    public void displayChosenDolr(){
        final TextView textView = (TextView) findViewById(R.id.num_dollar_send);
        textView.setText(Integer.toString(dolrCount));
    }
    public void displayChosenQuid(){
        final TextView textView = (TextView) findViewById(R.id.num_quid_send);
        textView.setText(Integer.toString(quidCount));
    }
    public void displayChosenPeny(){
        final TextView textView = (TextView) findViewById(R.id.num_penny_send);
        textView.setText(Integer.toString(penyCount));
    }

    public void addQuid(View view){quidCount++; displayChosenQuid();}
    public void addPeny(View view){penyCount++; displayChosenPeny();}
    public void addShil(View view){shilCount++; displayChosenShil();}
    public void addDolr(View view){dolrCount++; displayChosenDolr();}

    //Subtracting the coins. If the amount of coins is zero, do not go below.
    public void subQuid(View view){
        if(quidCount >0){
            quidCount--;
        }
        displayChosenQuid();
    }
    public void subPeny(View view){
        if(penyCount >0){
            penyCount--;
        }
        displayChosenPeny();
    }
    public void subShil(View view){
        if(shilCount >0){
            shilCount--;
        }
        displayChosenShil();
    }
    public void subDolr(View view){
        if(dolrCount>0){
            dolrCount--;
        }
        displayChosenDolr();
    }
}
