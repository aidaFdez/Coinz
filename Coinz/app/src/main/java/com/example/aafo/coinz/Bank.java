package com.example.aafo.coinz;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


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

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Bank");

        displayInfo();
        displayDolr();
        displayPenny();
        displayQuid();
        displayShil();
        displayChosenDolr();
        displayChosenShil();
        displayChosenPeny();
        displayChosenQuid();

        if(getFirstTime(Bank.this)){
            showGuide();
        }

    }

    //Displaying all the data on the screen
    public void displayInfo(){
        HashMap<String, Float> ratesHash = MainActivity.ratesHash;
        final TextView textView = findViewById(R.id.info); //penny, dollar shilling quid
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
        final TextView textView = findViewById(R.id.quid);
        textView.setText(toChange);
    }
    public void displayPenny(){
        int penny = MainActivity.getNumPenny(Bank.this);
        int penFriends = MainActivity.getNumPennyFriends(Bank.this);
        String toChange = "Pennies collected: " + penny + "\nFrom friends: " + penFriends;
        final TextView textView = findViewById(R.id.penny);
        textView.setText(toChange);
    }
    public void displayDolr(){
        int dolr = MainActivity.getNumDolr(Bank.this);
        int dolrFriends = MainActivity.getNumDolrFriends(Bank.this);
        String toChange = "Dollars collected: " + dolr + "\nFrom friends: " + dolrFriends;
        final TextView textView = findViewById(R.id.dollar);
        textView.setText(toChange);
    }
    public void displayShil(){
        int shil = MainActivity.getNumShil(Bank.this);
        int shilFriends = MainActivity.getNumShilFriends(Bank.this);
        String toChange = "Shillings collected: " + shil + "\nFrom friends: " + shilFriends;
        final TextView textView = findViewById(R.id.shilling);
        textView.setText(toChange);
    }

    @SuppressLint("SetTextI18n")
    public void displayChosenShil(){
        final TextView textView = findViewById(R.id.num_shillin);
        textView.setText(Integer.toString(shilCount));
    }
    @SuppressLint("SetTextI18n")
    public void displayChosenDolr(){
        final TextView textView = findViewById(R.id.num_dollar);
        textView.setText(Integer.toString(dolrCount));
    }
    @SuppressLint("SetTextI18n")
    public void displayChosenQuid(){
        final TextView textView = findViewById(R.id.num_quid);
        textView.setText(Integer.toString(quidCount));
    }
    @SuppressLint("SetTextI18n")
    public void displayChosenPeny(){
        final TextView textView = findViewById(R.id.num_penny);
        textView.setText(Integer.toString(penyCount));
    }

    //Adding the coins.
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
        String TAG = "Cash Quid";
        Log.d(TAG, "Started the cashing of quid");
        //Making sure that the coins are updated
        MainActivity.setCoins(Bank.this);
        MainActivity.setCoinsFriends(Bank.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(Bank.this);
        HashMap<String, String[]> coinsFriends = MainActivity.getCoinsFriends(Bank.this);
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
            MainActivity.setCoinsFriends(Bank.this);
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            ArrayList<String> keys = new ArrayList<>(quidHash.keySet());

            HashMap<String, String[]> quidHashFriends = MainActivity.coinsQuidFriends;
            Log.d(TAG, "Size quidFriends: " + quidHash.size());
            ArrayList<String> keysFriends = new ArrayList<>(quidHashFriends.keySet());

            //Cash the coins if the 25 limit has not been reached.
             if(quidCount<=(25-getCoinsCashed(Bank.this)) || numQuidFr >= quidCount){
                Log.d(TAG, "Cashing quid from both types of coins");
                //Get the number of quid that can be cashed. If the user has 5 quid but only 4 coins are left, then take 4 from own and 1 from friends
                int quidToCash = Math.min(Math.min(25-getCoinsCashed(Bank.this), numQuid), quidCount);
                Log.d(TAG, "CoinsCashed: "+getCoinsCashed(Bank.this)+" numQuid: "+" quidCount: "+quidCount);
                int quidFromFr = quidCount-quidToCash;
                Log.d(TAG, "quidCount: " +quidCount+" quidToCash: "+quidToCash+ " quidFromFr: "+quidFromFr);
                cashFromOwn(Bank.this, quidToCash, used, keys, coinsOverall, quidHash, "QUID");
                cashFromFriends(Bank.this, quidFromFr, used, keysFriends, coinsFriends, quidHash, "QUID");
            }
            else {notEnough(Bank.this);}

            displayInfo();
            displayQuid();
        }
        //If the user does not have enough coins, display a message.
        else {notEnough(Bank.this);}
    }

    public void cashDolr(View view){
        String TAG = "Cash Dolr";
        Log.d(TAG, "Starting the cash of dolr");
        //Making sure that the coins are updated
        MainActivity.setCoins(Bank.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(Bank.this);
        HashMap<String, String[]> coinsFriends = MainActivity.getCoinsFriends(Bank.this);
        //Getting the coins that the user has already cashed
        ArrayList<String> used = MainActivity.getCoinsUsed(Bank.this);
        int numDolr = MainActivity.getNumDolr(Bank.this);
        int numDolrFr = MainActivity.getNumDolrFriends(Bank.this);

        //If there are enough quid to do the change
        if(numDolr+numDolrFr >= dolrCount){
            //If the user has cashed the daily 25 and does not have enough coins from friends, display a message and exit
            if(numDolrFr<dolrCount && getCoinsCashed(Bank.this)>=25){notEnough(Bank.this);return;}

            //Make sure that the coins are up to date
            MainActivity.setCoins(Bank.this);
            MainActivity.setCoinsFriends(Bank.this);
            HashMap<String, String[]> dolrHash = MainActivity.coinsDolr;
            ArrayList<String> keys = new ArrayList<>(dolrHash.keySet());

            HashMap<String, String[]> dolrHashFriends = MainActivity.coinsDolrFriends;
            Log.d(TAG, "Size dolrFriends: " + dolrHash.size());
            ArrayList<String> keysFriends = new ArrayList<>(dolrHashFriends.keySet());

            if(dolrCount<=(25-getCoinsCashed(Bank.this)) ||numDolrFr >= dolrCount){
                Log.d(TAG, "Cashing dolr from own and friends'");
                //Get the number of quid that can be cashed. If the user has 5 quid but only 4 coins are left, then take 4
                int dolrToCash = Math.min(Math.min(25-getCoinsCashed(Bank.this), numDolr), dolrCount);
                int dolrFromFr = dolrCount-dolrToCash;
                cashFromOwn(Bank.this, dolrToCash, used, keys, coinsOverall, dolrHash, "DOLR");
                cashFromFriends(Bank.this, dolrFromFr, used, keysFriends, coinsFriends, dolrHashFriends, "DOLR");
            }
            else{notEnough(Bank.this);}

            displayInfo();
            displayDolr();
        }
        //If the user does not have enough coins, display a message.
        else {notEnough(Bank.this);}
    }

    public void cashPeny(View view){
        String  TAG = "Cash Peny";
        Log.d(TAG, "Starting cashing of peny");
        //Making sure that the coins are updated
        MainActivity.setCoins(Bank.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(Bank.this);
        HashMap<String, String[]> coinsFriends = MainActivity.getCoinsFriends(Bank.this);
        //Getting the coins that the user has already cashed
        ArrayList<String> used = MainActivity.getCoinsUsed(Bank.this);
        int numPeny = MainActivity.getNumPenny(Bank.this);
        int numPenyFr = MainActivity.getNumPennyFriends(Bank.this);

        //If there are enough quid to do the change
        if(numPeny+numPenyFr >= penyCount){
            //If the user has cashed the daily 25 and does not have enough coins from friends, display a message and exit
            if(numPenyFr<penyCount && getCoinsCashed(Bank.this)>=25){notEnough(Bank.this);return;}

            //Make sure that the coins are up to date
            MainActivity.setCoins(Bank.this);
            MainActivity.setCoinsFriends(Bank.this);
            HashMap<String, String[]> penyHash = MainActivity.coinsPeny;
            ArrayList<String> keys = new ArrayList<>(penyHash.keySet());

            HashMap<String, String[]> penyHashFriends = MainActivity.coinsPenyFriends;
            Log.d(TAG, "Size penyFriends: " + penyHash.size());
            ArrayList<String> keysFriends = new ArrayList<>(penyHashFriends.keySet());

            if(penyCount<=(25-getCoinsCashed(Bank.this)) || numPenyFr >= penyCount){
                Log.d(TAG, "Cashing peny from both");
                //Get the number of quid that can be cashed. If the user has 5 quid but only 4 coins are left, then take 4
                int penyToCash = Math.min(Math.min(25-getCoinsCashed(Bank.this), numPeny), penyCount);
                int penyFromFr = penyCount-penyToCash;
                cashFromOwn(Bank.this, penyToCash, used, keys, coinsOverall, penyHash, "PENY");
                cashFromFriends(Bank.this, penyFromFr, used, keysFriends, coinsFriends, penyHashFriends, "PENY");
            }
            else{notEnough(Bank.this);}

            displayInfo();
            displayPenny();
        }
        //If the user does not have enough coins, display a message.
        else {notEnough(Bank.this);}
    }

    public void cashShil(View view){
        String TAG = "Cash Shil";
        Log.d(TAG, "Starting the cash of shil");
        //Making sure that the coins are updated
        MainActivity.setCoins(Bank.this);
        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(Bank.this);
        HashMap<String, String[]> coinsFriends = MainActivity.getCoinsFriends(Bank.this);
        //Getting the coins that the user has already cashed
        ArrayList<String> used = MainActivity.getCoinsUsed(Bank.this);
        int numShil = MainActivity.getNumShil(Bank.this);
        int numShilFr = MainActivity.getNumShilFriends(Bank.this);

        //If there are enough quid to do the change
        if(numShil+numShilFr >= shilCount){
            //If the user has cashed the daily 25 and does not have enough coins from friends, display a message and exit
            if(numShilFr<shilCount && getCoinsCashed(Bank.this)>=25){notEnough(Bank.this);return;}

            //Make sure that the coins are up to date
            MainActivity.setCoins(Bank.this);
            MainActivity.setCoinsFriends(Bank.this);
            HashMap<String, String[]> shilHash = MainActivity.coinsShil;
            ArrayList<String> keys = new ArrayList<>(shilHash.keySet());

            HashMap<String, String[]> shilHashFriends = MainActivity.coinsShilFriends;
            Log.d(TAG, "Size shilFriends: " + shilHash.size());
            ArrayList<String> keysFriends = new ArrayList<>(shilHashFriends.keySet());

            if(shilCount<=(25-getCoinsCashed(Bank.this)) || numShilFr >= shilCount){
                Log.d(TAG, "Cashing shil from own and friends");
                //Get the number of quid that can be cashed. If the user has 5 quid but only 4 coins are left, then take 4
                int shilToCash = Math.min(Math.min(25-getCoinsCashed(Bank.this), numShil), shilCount);
                int shilFromFr = shilCount-shilToCash;
                cashFromOwn(Bank.this, shilToCash, used, keys, coinsOverall, shilHash, "SHIL");
                cashFromFriends(Bank.this, shilFromFr, used, keysFriends, coinsFriends, shilHashFriends, "SHIL");
            }
            else{notEnough(Bank.this);}

            displayInfo();
            displayShil();
        }
        //If the user does not have enough coins, display a message.
        else {notEnough(Bank.this);}
    }

    public static void cashFromFriends(Context context, int amount, ArrayList<String> used, ArrayList<String> keys, HashMap<String, String[]> coinsOverallFriends, HashMap<String, String[]> coinsHash, String currency){
        String TAG = "Cash friends";
        Log.d(TAG, "Cashing from friends");

        int alreadyCashed = 0;
        double goldToAdd = 0.0;
        for (int i =0; i<amount; i++){
            Log.d(TAG, "Size of keys: " + keys.size());
            Log.d(TAG, "Size of used: " + used.size());
            //If the coin has not been cashed before, cash it
            if(!(used.contains(keys.get(i)))){
                Log.d(TAG, "Cashing coin " + (i+1) + "/" + amount);
                float rate = MainActivity.ratesHash.get(currency);
                float value = Float.parseFloat(coinsHash.get(keys.get(i))[0]);
                goldToAdd = goldToAdd + (rate*value);
                coinsOverallFriends.remove(keys.get(i));
                MainActivity.setCoinsOverallFriends(context, coinsOverallFriends);
                MainActivity.addCoinsUsed(context, keys.get(i));

                switch (currency) {
                    case "QUID":
                        MainActivity.coinsQuidFriends.remove(keys.get(i));
                        break;
                    case "DOLR":
                        MainActivity.coinsDolrFriends.remove(keys.get(i));
                        break;
                    case "PENY":
                        MainActivity.coinsPenyFriends.remove(keys.get(i));
                        break;
                    case "SHIL":
                        MainActivity.coinsShilFriends.remove(keys.get(i));
                        break;
                }
                //Update the coins
                MainActivity.setCoinsFriends(context);
            } else {
                alreadyCashed++;
                //Delete the already cashed coin, if this is not done then it could get blocked with the repeated ones.
                coinsOverallFriends.remove(keys.get(i));
                MainActivity.setCoinsOverallFriends(context, coinsOverallFriends);
                switch (currency) {
                    case "QUID":
                        MainActivity.coinsQuidFriends.remove(keys.get(i));
                        break;
                    case "DOLR":
                        MainActivity.coinsDolrFriends.remove(keys.get(i));
                        break;
                    case "PENY":
                        MainActivity.coinsPenyFriends.remove(keys.get(i));
                        break;
                    case "SHIL":
                        MainActivity.coinsShilFriends.remove(keys.get(i));
                        break;
                }
                MainActivity.setCoinsFriends(context);
            }
        }
        if(alreadyCashed>0){alreadyCashed(context, alreadyCashed);}
        setCoinsCashedTotal(context, amount-alreadyCashed);

        addGold(context, goldToAdd);
        if(Medals.checkGoals(context)){
            Toast.makeText(context, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void cashFromOwn(Context context, int amount, ArrayList<String> used,ArrayList<String> keys, HashMap<String, String[]> coinsOverall, HashMap<String, String[]> coinHash, String currency ){
        String TAG = "Cashing own";
        Log.d(TAG, "Cashing from own coins");

        int alreadyCashed = 0;
        double goldToAdd =0;
        for (int i =0; i<amount; i++) {
            Log.d(TAG, "Cashing coin " + (i+1) + "/" + amount);
            //If the coin has not been cashed before, cash it
            if (!(used.contains(keys.get(i)))) {
                float rate = MainActivity.ratesHash.get(currency);
                float value = Float.parseFloat(coinHash.get(keys.get(i))[0]);
                goldToAdd = goldToAdd + (rate*value);
                coinsOverall.remove(keys.get(i));
                MainActivity.addCoinsUsed(context, keys.get(i));
                MainActivity.setCoinsOverall(context, coinsOverall);
                switch (currency) {
                    case "QUID":
                        MainActivity.coinsQuid.remove(keys.get(i));
                        break;
                    case "DOLR":
                        MainActivity.coinsDolr.remove(keys.get(i));
                        break;
                    case "PENY":
                        MainActivity.coinsPeny.remove(keys.get(i));
                        break;
                    case "SHIL":
                        MainActivity.coinsShil.remove(keys.get(i));
                        break;
                }
                //Update the coins
                MainActivity.setCoins(context);
                addCoinsCashed(context, 1);
            } else {
                alreadyCashed++;
                //Delete the already cashed coin, if this is not done then it could get blocked with the repeated ones.
                coinsOverall.remove(keys.get(i));
                MainActivity.setCoinsOverall(context, coinsOverall);
                switch (currency) {
                    case "QUID":
                        MainActivity.coinsQuid.remove(keys.get(i));
                        break;
                    case "DOLR":
                        MainActivity.coinsDolr.remove(keys.get(i));
                        break;
                    case "PENY":
                        MainActivity.coinsPeny.remove(keys.get(i));
                        break;
                    case "SHIL":
                        MainActivity.coinsShil.remove(keys.get(i));
                        break;
                }
                MainActivity.setCoins(context);
            }
        }
        setCoinsCashedTotal(context, amount-alreadyCashed);
        if(alreadyCashed>0){alreadyCashed(context, alreadyCashed);}

        addGold(context, goldToAdd);
        if(Medals.checkGoals(context)){
            Toast.makeText(context, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void notEnough(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You do not have enough coins to make the change.")
                .setTitle("Lack of coins")
                .setPositiveButton("Close", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void alreadyCashed(Context context, int amount){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(amount + " coin/s had already been cashed before.")
                .setTitle("Already cashed")
                .setPositiveButton("Close", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void addGold(Context context, double amount){
        double gold = MainActivity.getGold(context);
        float goldToAddF = (float) amount;
        float total = (float) gold + goldToAddF;
        MainActivity.setGold(context, total);
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showGuide();
    }

    public void showGuide(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Bank").setMessage(R.string.bank_explanation)
                .setPositiveButton("OK", (dialog, which) -> {
                    //Nothing, just closes
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //Methods for keeping track and restarting the coins that have been cashed in the day.
    public static void addCoinsCashed(Context context, int amount){
        int cashed = getPrefs(context).getInt("CashedToday", 0);
        cashed = cashed+amount;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("CashedToday", cashed);
        editor.commit();
    }
    public static int getCoinsCashed (Context context){return getPrefs(context).getInt("CashedToday", 0);}
    public static void resetCoinsCashed(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("CashedToday", 0);
        editor.commit();
    }

    public static void setCoinsCashedTotal(Context context, int amount){
        int cashed = getPrefs(context).getInt("CashedTotal", 0);
        cashed = cashed+amount;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("CashedTotal", cashed);
        editor.commit();
    }
    public static int getCoinsCashedTotal(Context context){return getPrefs(context).getInt("CashedTotal", 0);}

    //Check if it is the first time the bank has been opened
    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time bank", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time bank", false);
        editor.apply();
        return ret;
    }
}
