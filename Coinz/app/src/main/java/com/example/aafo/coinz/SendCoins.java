package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Send coins");

        mAuth = FirebaseAuth.getInstance();

        if(getFirstTime(SendCoins.this)){
            showHelpDialog();
        }

        MainActivity.setCoinsFriends(SendCoins.this);
        MainActivity.setCoins(SendCoins.this);
        displayDolr();
        displayPenny();
        displayQuid();
        displayShil();
        displayChosenDolr();
        displayChosenShil();
        displayChosenPeny();
        displayChosenQuid();

        emailToSend = findViewById(R.id.email_to_send);
    }


    public void sendQuid(View view){
        String emailString = emailToSend.getText().toString().trim();


        //Show a toast if the user has not inputted any email
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }

        //If the user tries to send coins to themselves, do not allow it, show a toast
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String emailUser = user.getEmail();
        if(emailString.equals(emailUser)){
            Toast.makeText(this, "You can not send coins to yourself", Toast.LENGTH_SHORT).show();
            return;
        }
        //If the user has not cashed the 25 of the day, show a toast saying so and exit
        if(Bank.getCoinsCashed(SendCoins.this)<25){
            toastNot25();
            return;
        }

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);

        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numQuid = MainActivity.getNumQuid(SendCoins.this);

        if(numQuid>= quidCount) {
            HashMap<String, String[]> quidHash = MainActivity.coinsQuid;
            List<String> keys = new ArrayList<>(quidHash.keySet());
            ArrayList<Boolean> sent = new ArrayList<>();

            for (int i = 0; i <quidCount; i++) {
                String id = keys.get(i);
                String[] charact = quidHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                int index = i;

                Map<String, String> map = new HashMap<>();
                map.put(charact[0], charact[1]);


                db.collection(emailString).document(id).set(map)
                .addOnSuccessListener(aVoid -> {
                    MainActivity.coinsQuid.remove(keys.get(index));
                    coinsOverall.remove(keys.get(index));
                    MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);
                    displayQuid();
                    sent.add(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT).show();
                    Log.d("Send coin", e.toString());
                    sent.add(false);
                });
            }
            if(sent.contains(false)){
                Toast.makeText(SendCoins.this, "One or more coins were not sent", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SendCoins.this, "Coins sent successfully", Toast.LENGTH_SHORT).show();
            }
            setNumCoinsSent(SendCoins.this, quidCount);
            if(Medals.checkGoals(SendCoins.this)){
                Toast.makeText(SendCoins.this, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendShil(View view){
        ArrayList<Boolean> sent = new ArrayList<>();
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }

        //If the user tries to send coins to themselves, do not allow it, show a toast
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String emailUser = user.getEmail();
        if(emailString.equals(emailUser)){
            Toast.makeText(this, "You can not send coins to yourself", Toast.LENGTH_SHORT).show();
            return;
        }
        //If the user has not cashed the 25 of the day, show a toast saying so and exit
        if(Bank.getCoinsCashed(SendCoins.this)<25){
            toastNot25();
            return;
        }

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);

        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numShil = MainActivity.getNumShil(SendCoins.this);

        if(numShil>= shilCount) {
            HashMap<String, String[]> shilHash = MainActivity.coinsShil;
            List<String> keys = new ArrayList<>(shilHash.keySet());

            for (int i = 0; i <shilCount; i++) {
                String id = keys.get(i);
                String[] charact = shilHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                int index = i;

                Map<String, String> map = new HashMap<>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(aVoid -> {
                            MainActivity.coinsShil.remove(keys.get(index));
                            coinsOverall.remove(keys.get(index));
                            MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);
                            displayShil();
                            sent.add(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                            Log.d("Send coin", e.toString());
                            sent.add(false);
                        });
            }
            if(sent.contains(false)){
                Toast.makeText(SendCoins.this, "One or more coins were not sent", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SendCoins.this, "Coins sent successfully", Toast.LENGTH_SHORT).show();
            }
            setNumCoinsSent(SendCoins.this, shilCount);
            if(Medals.checkGoals(SendCoins.this)){
                Toast.makeText(SendCoins.this, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendDolr(View view){
        ArrayList<Boolean> sent = new ArrayList<>();
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }

        //If the user tries to send coins to themselves, do not allow it, show a toast
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String emailUser = user.getEmail();
        if(emailString.equals(emailUser)){
            Toast.makeText(this, "You can not send coins to yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);

        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numDolr = MainActivity.getNumDolr(SendCoins.this);

        if(numDolr>= dolrCount) {
            HashMap<String, String[]> dolrHash = MainActivity.coinsQuid;
            List<String> keys = new ArrayList<>(dolrHash.keySet());

            for (int i = 0; i <dolrCount; i++) {
                String id = keys.get(i);
                String[] charact = dolrHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                int index = i;

                Map<String, String> map = new HashMap<>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(aVoid -> {
                            MainActivity.coinsDolr.remove(keys.get(index));
                            coinsOverall.remove(keys.get(index));
                            MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);
                            displayDolr();
                            sent.add(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                            Log.d("Send coin", e.toString());
                            sent.add(false);
                        });
            }
            if(sent.contains(false)){
                Toast.makeText(SendCoins.this, "One or more coins were not sent", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SendCoins.this, "Coins sent successfully", Toast.LENGTH_SHORT).show();
            }
            setNumCoinsSent(SendCoins.this, dolrCount);
            if(Medals.checkGoals(SendCoins.this)){
                Toast.makeText(SendCoins.this, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            notEnough(SendCoins.this);
        }
    }

    public void sendPeny(View view){
        ArrayList<Boolean> sent = new ArrayList<>();
        String emailString = emailToSend.getText().toString().trim();
        if(emailString.isEmpty()){
            Toast.makeText(SendCoins.this, "Please provide an email", Toast.LENGTH_SHORT).show();
            return;
        }

        //If the user tries to send coins to themselves, do not allow it, show a toast
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String emailUser = user.getEmail();
        if(emailString.equals(emailUser)){
            Toast.makeText(this, "You can not send coins to yourself", Toast.LENGTH_SHORT).show();
            return;
        }
        //If the user has not cashed the 25 of the day, show a toast saying so and exit
        if(Bank.getCoinsCashed(SendCoins.this)<25){
            toastNot25();
            return;
        }

        //Making sure that the coins are updated
        MainActivity.setCoins(SendCoins.this);

        //Getting the coins that the user has
        HashMap<String, String[]> coinsOverall = MainActivity.getCoinsOverall(SendCoins.this);
        int numPenny = MainActivity.getNumPenny(SendCoins.this);

        if(numPenny>= penyCount) {
            HashMap<String, String[]> penyHash = MainActivity.coinsPeny;
            List<String> keys = new ArrayList<>(penyHash.keySet());

            for (int i = 0; i <penyCount; i++) {
                String id = keys.get(i);
                String[] charact = penyHash.get(id);
                MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);

                int index = i;

                Map<String, String> map = new HashMap<>();
                map.put(charact[0], charact[1]);

                db.collection(emailString).document(id).set(map)
                        .addOnSuccessListener(aVoid -> {
                            MainActivity.coinsPeny.remove(keys.get(index));
                            coinsOverall.remove(keys.get(index));
                            MainActivity.setCoinsOverall(SendCoins.this, coinsOverall);
                            //Toast.makeText(SendCoins.this, "Coin "+index+" was sent successfully", Toast.LENGTH_SHORT).show();
                            displayPenny();
                            sent.add(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SendCoins.this, "The coins were not sent", Toast.LENGTH_SHORT);
                            Log.d("Send coin", e.toString());
                            sent.add(false);
                        });
            }
            if(sent.contains(false)){
                Toast.makeText(SendCoins.this, "One or more coins were not sent", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SendCoins.this, "Coins sent successfully", Toast.LENGTH_SHORT).show();
            }
            setNumCoinsSent(SendCoins.this, penyCount);
            if(Medals.checkGoals(SendCoins.this)){
                Toast.makeText(SendCoins.this, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
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
                .setPositiveButton("Close", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayQuid(){
        int quid = MainActivity.getNumQuid(SendCoins.this);
        String toChange = "Quid collected: " + quid;
        final TextView textView = findViewById(R.id.quid_send);
        textView.setText(toChange);
    }
    public void displayPenny(){
        int penny = MainActivity.getNumPenny(SendCoins.this);
        String toChange = "Pennies collected: " + penny;
        final TextView textView = findViewById(R.id.penny_send);
        textView.setText(toChange);
    }
    public void displayDolr(){
        int dolr = MainActivity.getNumDolr(SendCoins.this);
        String toChange = "Dollars collected: " + dolr;
        final TextView textView = findViewById(R.id.dollar_send);
        textView.setText(toChange);
    }
    public void displayShil(){
        int shil = MainActivity.getNumShil(SendCoins.this);
        String toChange = "Shillings collected: " + shil;
        final TextView textView = findViewById(R.id.shilling_send);
        textView.setText(toChange);
    }

    public void displayChosenShil(){
        final TextView textView = findViewById(R.id.num_shillin_send);
        textView.setText(Integer.toString(shilCount));
    }
    public void displayChosenDolr(){
        final TextView textView = findViewById(R.id.num_dollar_send);
        textView.setText(Integer.toString(dolrCount));
    }
    public void displayChosenQuid(){
        final TextView textView = findViewById(R.id.num_quid_send);
        textView.setText(Integer.toString(quidCount));
    }
    public void displayChosenPeny(){
        final TextView textView = findViewById(R.id.num_penny_send);
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

    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setNumCoinsSent(Context context, int sent){
        int sentBefore = getNumCoinsSent(context);
        sent = sentBefore+sent;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("sentCoins", sent);
        editor.commit();
    }
    public static int getNumCoinsSent(Context context){
        return getPrefs(context).getInt("sentCoins", 0);
    }

    private static boolean getFirstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("First time sendCoins", true);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("First time sendCoins", false);
        editor.commit();
        return ret;
    }

    //Show an alert dialog with the same information as the first time the user opened the activity
    public void showHelp(View view){
        showHelpDialog();
    }

    public void showHelpDialog(){
        android.support.v7.app.AlertDialog.Builder builder  = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Sending coins").setMessage(R.string.sendCoins_explanation)
                .setPositiveButton("OK", (dialog, which) -> {
                    //Nothing, just closes
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toastNot25(){
        Toast.makeText(this, "You have not cashed 25 coins yet", Toast.LENGTH_SHORT).show();
    }
}
