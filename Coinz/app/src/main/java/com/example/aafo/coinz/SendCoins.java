package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference(mAuth.getUid());
        HashMap<String, String[]> coinsToSend = new HashMap<String, String[]>();

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numQuid = MainActivity.getNumQuid(SendCoins.this);

        if(numQuid>= quidCount) {
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            List<String> keys = new ArrayList(quidHash.keySet());

            for (int i = 0; i <quidCount; i++) {
                //float rate = MainActivity.ratesHash.get(currency);
                //float value = Float.parseFloat(coinHash.get(keys.get(i))[0]);
                //goldToAdd = goldToAdd + (rate * value);
                coinsToSend.put(keys.get(i), quidHash.get(keys.get(i)));
                coinsOverall.remove(keys.get(i));
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);
                MainActivity.coinsQuid.remove(keys.get(i));
                MainActivity.subQuid(SendCoins.this, 1);

                db.collection("coins").document("quids").set(coinsToSend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SendCoins.this, "The coins were sent successfully", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendShil(View view){

    }

    public void sendDolr(View view){

    }

    public void sendPeny(View view){

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