package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
                            + "\nQuid rate: " + ratesHash.get("QUID").toString()
                            + "\nGold stored: "+MainActivity.getGold(Bank.this);
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
        //Making sure that the coins are updated
        MainActivity.setCoins(Bank.this);
        double goldToAdd = 0.0;
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(Bank.this);
        //Getting the coins that the user has already cashed
        ArrayList<String> used = MainActivity.getCoinsUsed(Bank.this);
        int numQuid = MainActivity.getNumQuid(Bank.this);
        int numQuidFr = MainActivity.getNumQuidFriends(Bank.this);

        //If there are enough quid to do the change
        if(numQuid+numQuidFr >= quidCount){
            //If the user has cashed the daily 25 and does not have enough coins from friends, display a message and exit
            if(numQuidFr<quidCount && getCoinsCashed(Bank.this)>=25){notEnough(Bank.this);return;}

            //Make sure that the coins are up to date
            MainActivity.setCoins(Bank.this);
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            ArrayList<String> keys = new ArrayList(quidHash.keySet());
            //If the number of coins to change can be covered by the user's coins
            if(numQuid >= quidCount && getCoinsCashed(Bank.this)<25){
                cashFromOwn(Bank.this, quidCount, used, keys, coinsOverall, quidHash, "QUID");

                addGold(Bank.this, goldToAdd);
                displayInfo();
                displayQuid();
                return;
            }
            if(numQuidFr>quidCount && getCoinsCashed(Bank.this)>=25){
                /*for (int i =0; i<quidCount; i++) {
                    //If the coin has not been cashed before, cash it
                    if (!(used.contains(keys.get(i)))) {
                        Float rate = MainActivity.ratesHash.get("QUID");
                        goldToAdd = goldToAdd + rate;
                        coinsOverall.remove(keys.get(i));
                        MainActivity.addCoinsUsed(Bank.this, keys.get(i));
                        MainActivity.setCoinsOverall(Bank.this, coinsOverall);
                        MainActivity.subQuid(Bank.this, 1);
                        MainActivity.setCoins(Bank.this);
                        MainActivity.coinsQuid.remove(keys.get(i));
                        addCoinsCashed(Bank.this, 1);
                    } else {
                        alreadyCashed++;
                    }
                }*/
                cashFromOwn(Bank.this, quidCount, used, keys, coinsOverall, quidHash, "QUID");

                //addGold(Bank.this, goldToAdd);
                displayInfo();
                displayQuid();
                return;
            }

            //Add the new gold to the total gold. Have to cast the goldToAdd to a float
            addGold(Bank.this, goldToAdd);
            displayInfo();
            displayQuid();
        }
        //If the user does not have enough coins, display a message.
        else {notEnough(Bank.this);}

    }
    //TODO pasar una string con
    public static void cashFromOwn(Context context, int amount, ArrayList<String> used,ArrayList<String> keys, HashMap<String, String[]> coinsOverall, HashMap<String, String[]> coinHash, String currency ){
        int alreadyCashed = 0;
        double goldToAdd =0;
        for (int i =0; i<amount; i++) {
            //If the coin has not been cashed before, cash it
            if (!(used.contains(keys.get(i)))) {
                float rate = MainActivity.ratesHash.get(currency);
                float value = Float.parseFloat(coinHash.get(keys.get(i))[0]);
                goldToAdd = goldToAdd + (rate*value);
                coinsOverall.remove(keys.get(i));
                MainActivity.addCoinsUsed(context, keys.get(i));
                MainActivity.setCoinsOverall(context, coinsOverall);
                if (currency.equals("QUID")){
                    MainActivity.coinsQuid.remove(keys.get(i));
                    MainActivity.subQuid(context, 1);
                }
                if (currency.equals("DOLR")){
                    MainActivity.coinsDolr.remove(keys.get(i));
                    MainActivity.subDolr(context, 1);
                }
                if(currency.equals("PENY")){
                    MainActivity.coinsPeny.remove(keys.get(i));
                    MainActivity.subPenny(context, 1);
                }
                if(currency.equals("SHIL")){
                    MainActivity.coinsShil.remove(keys.get(i));
                    MainActivity.subShil(context, 1);
                }
                //Update the coins
                MainActivity.setCoins(context);
                addCoinsCashed(context, 1);
            } else {
                alreadyCashed++;
                //Delete the already cashed coin, if this is not done then it could get blocked with the repeated ones.
                coinsOverall.remove(keys.get(i));
                MainActivity.setCoinsOverall(context, coinsOverall);
                if(currency.equals("QUID")){
                    MainActivity.subQuid(context, 1);
                    MainActivity.coinsQuid.remove(keys.get(i));
                }
                if(currency.equals("DOLR")){
                    MainActivity.subDolr(context, 1);
                    MainActivity.subDolr(context, 1);
                }
                if(currency.equals("PENY")){
                    MainActivity.coinsPeny.remove(keys.get(i));
                    MainActivity.subPenny(context, 1);
                }
                if(currency.equals("SHIL")){
                    MainActivity.coinsShil.remove(keys.get(i));
                    MainActivity.subShil(context, 1);
                }
                MainActivity.setCoins(context);
            }
        }
        if(alreadyCashed>0){alreadyCashed(context, alreadyCashed);}

        addGold(context, goldToAdd);
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

    public static void alreadyCashed(Context context, int amount){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(amount + " coin/s had already been cashed before.")
                .setTitle("Already cashed")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void addGold(Context context, double amount){
        double gold = MainActivity.getGold(context);
        float goldToAddF = (float) amount;
        float total = (float) gold + goldToAddF;
        MainActivity.setGold(context, total);
    }

    private SharedPreferences sharedPrefs;
    private static String PREF_NAME = "preferences";

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public static void addCoinsCashed(Context context, int amount){
        int cashed = getPrefs(context).getInt("CashedToday", 0);
        cashed = cashed+amount;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("CashedToday", cashed);
        editor.commit();
    }

    public static int getCoinsCashed (Context context){return getPrefs(context).getInt("CashedToday", 0);}

}
