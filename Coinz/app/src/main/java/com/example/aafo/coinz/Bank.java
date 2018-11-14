package com.example.aafo.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class Bank extends AppCompatActivity {

    private int shilCount = 0;
    private int dolrCount = 0;
    private int penyCount = 0;
    private int quidCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        displayInfo();
        displayDolr();
        displayPenny();
        displayQuid();
        displayShil();
        displayChosenDolr();
        displayChosenShil();
        displayChosenPeny();
        displayChosenQuid();

    }

    public void displayInfo(){
        HashMap<String, Float> ratesHash = MainActivity.ratesHash;
        final TextView textView = (TextView) findViewById(R.id.info); //penny, dollar shilling quid
        String toChange = "Penny rate: " + ratesHash.get("PENY").toString()
                            + "\nDollar rate: " + ratesHash.get("DOLR").toString()
                            + "\nShilling rate: " + ratesHash.get("SHIL").toString()
                            + "\nQuid rate: " + ratesHash.get("QUID").toString();
        textView.setText(toChange);
    }

    public void displayQuid(){
        int quid = MainActivity.getNumQuid(Bank.this);
        int quidFriends = MainActivity.getNumQuidFriends(Bank.this);
        String toChange = "Quid collected: " + quid + "\nFrom friends: " + quidFriends;
        final TextView textView = (TextView) findViewById(R.id.quid);
        textView.setText(toChange);
    }

    public void displayPenny(){
        int penny = MainActivity.getNumPenny(Bank.this);
        int penFriends = MainActivity.getNumPennyFriends(Bank.this);
        String toChange = "Pennies collected: " + penny + "\nFrom friends: " + penFriends;
        final TextView textView = (TextView) findViewById(R.id.penny);
        textView.setText(toChange);
    }

    public void displayDolr(){
        int dolr = MainActivity.getNumDolr(Bank.this);
        int dolrFriends = MainActivity.getNumDolrFriends(Bank.this);
        String toChange = "Dollars collected: " + dolr + "\nFrom friends: " + dolrFriends;
        final TextView textView = (TextView) findViewById(R.id.dollar);
        textView.setText(toChange);
    }

    public void displayShil(){
        int shil = MainActivity.getNumShil(Bank.this);
        int shilFriends = MainActivity.getNumShilFriends(Bank.this);
        String toChange = "Shillings collected: " + shil + "\nFrom friends: " + shilFriends;
        final TextView textView = (TextView) findViewById(R.id.shilling);
        textView.setText(toChange);
    }

    public void displayChosenShil(){
        final TextView textView = (TextView) findViewById(R.id.num_shillin);
        textView.setText(Integer.toString(shilCount));
    }

    public void displayChosenDolr(){
        final TextView textView = (TextView) findViewById(R.id.num_dollar);
        textView.setText(Integer.toString(dolrCount));
    }

    public void displayChosenQuid(){
        final TextView textView = (TextView) findViewById(R.id.num_quid);
        textView.setText(Integer.toString(quidCount));
    }

    public void displayChosenPeny(){
        final TextView textView = (TextView) findViewById(R.id.num_penny);
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

    public void cashQuid(View view){
        int numQuid = MainActivity.getNumQuid(Bank.this);
        int numQuidFr = MainActivity.getNumQuidFriends(Bank.this);
        if(numQuid+numQuidFr <= quidCount){
            MainActivity.setCoins(Bank.this);
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            ArrayList keys = new ArrayList(quidHash.keySet());
            //Hacer el for loop fuera, puede ser generico para todos, que devuelva los valores y ya
            //Tener en cuenta el limite de 25 al dia!
        }

    }

}
