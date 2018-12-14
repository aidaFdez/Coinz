package com.example.aafo.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private String jSon;
    private HashMap<String, Integer> coinsToday = new HashMap<>();
    public static HashMap<String, Float> ratesHash = new HashMap<>();
    static {
        //Put some default values in case it is not initialized because of internet connection or similar.
        ratesHash.put("QUID", 0.0f);
        ratesHash.put("SHIL", 0.0f);
        ratesHash.put("PENY", 0.0f);
        ratesHash.put("DOLR", 0.0f);
    }

    private String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Show the map on the screen (without the coinz)
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Log.d(TAG, "The map is showing now");

        //If the last time that the app was opened is the same day, then do not download the coins again
        if(!(getDate().equals(getDatePrefs(MainActivity.this)))){
            Log.d(TAG, "Download the map");
            getTheMap();
            setDatePrefs(MainActivity.this);
            resetVariables(MainActivity.this);
        }else{
            if(getJson(MainActivity.this).equals("Unable to load content. Check your network connection")){
                //If the map was not downloaded for some reason last time
                getTheMap();
            }
            Log.d(TAG, "Map already downloaded");
            jSon = getJson(MainActivity.this);
            //Reset all the daily variables

        }
        setDatePrefs(MainActivity.this);

        //If this is the firs time that the app has been opened, set the goals.
        if(firstTime(MainActivity.this)){
            Log.d(TAG, "First time the app is opened");
            Medals.addGoals(MainActivity.this);
        }


        //Code based on http://www.mapbox.com.s3-website-us-east-1.amazonaws.com/android-sdk/examples/geojson/
        mapView.getMapAsync(mapboxMap -> {
            try {
                //Getting all of the JSon data downloaded
                JSONObject jSonObj = new JSONObject(jSon);
                JSONArray features = jSonObj.getJSONArray("features");

                JSONObject rates = jSonObj.getJSONObject("rates");
                Float rateQuid = Float.parseFloat(rates.getString("QUID"));
                Float ratePeny = Float.parseFloat(rates.getString("PENY"));
                Float rateDolr = Float.parseFloat(rates.getString("DOLR"));
                Float rateShil = Float.parseFloat(rates.getString("SHIL"));

                //Putting the rates in the corresponding hash
                ratesHash.put("QUID", rateQuid);
                ratesHash.put("PENY", ratePeny);
                ratesHash.put("DOLR", rateDolr);
                ratesHash.put("SHIL", rateShil);

                //For each marker, create it and set its clicker and information
                for(int i=0; i<features.length();i++){
                    //Getting all the relevant properties of each feature for creating the marker.
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONArray coords = geometry.getJSONArray("coordinates");
                    JSONObject properties = feature.getJSONObject("properties");
                    String curr = properties.getString("currency");

                    Context context = getApplicationContext();
                    Integer colour = 0;

                    //Set the colour of the marker based on the coin's currency
                    switch (curr) {
                        case "SHIL":
                            colour = getResources().getColor(R.color.SHIL);
                            break;
                        case "QUID":
                            colour = getResources().getColor(R.color.QUID);
                            break;
                        case "PENY":
                            colour = getResources().getColor(R.color.PENY);
                            break;
                        case "DOLR":
                            colour = getResources().getColor(R.color.DOLR);
                            break;
                    }
                    //Creating the icon object for the marker and setting it to the correct colour depending on its currency
                    Icon icon = drawableToIcon(context, R.drawable.loc_icon,colour);

                    String id = properties.getString("id");

                    //Creating the marker with all the information from above
                    MarkerOptions coin = new MarkerOptions();
                    coin.position(new LatLng(coords.getDouble(1), coords.getDouble(0)))
                            .icon(icon)
                            .setSnippet(id);
                    //Save the id of the coin as a coin that has been created that day
                    coinsToday.put(id, i);
                    //If the coin has been picked up already, do not put it in the map
                    if(!(getCoinsOverall(MainActivity.this).containsKey(properties.getString("id"))) &&
                       !(getPickedCoins(MainActivity.this).contains(properties.getString("id")))){
                        //Add the marker of the coin
                        mapboxMap.addMarker(coin);
                    }

                    //Code based on https://www.mapbox.com/android-docs/maps/overview/annotations/
                    mapboxMap.setOnMarkerClickListener(marker -> {
                        //Setting the click on the marker so it says if the user is too far or asks if the user wants to pick it up.
                        try {
                            //Get the features of the coin saved on the snippet of the marker
                            JSONObject props = features.getJSONObject(coinsToday.get(marker.getSnippet()))
                                    .getJSONObject("properties");

                            //Get the position and coordinates of the marker
                            LatLng markerLtLn = marker.getPosition();
                            double markerLt = markerLtLn.getLatitude();
                            double markerLn = markerLtLn.getLongitude();

                            //Alert dialog that shows up in case the user tries to pick up a coin when the location is not available
                            AlertDialog.Builder builderr  = new AlertDialog.Builder(MainActivity.this);
                            builderr.setTitle("Wait").setMessage("Please wait for the location to load.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                //Nothing, just closes
                            });
                            AlertDialog dialogg = builderr.create();
                            //If the location is null, then show the above dialog. Otherwise, go on with comparing the user's distance to the marker
                            if(originLocation == null){
                                dialogg.show();
                            }else{
                                //Get the user's location
                                double userLt = originLocation.getLatitude();
                                double userLn = originLocation.getLongitude();
                                double distance = getDistance(markerLt, userLt, markerLn, userLn, 0.0, 0.0);
                                //If the user is close enough, then ask if they want to pick up the coin.
                                if(distance<25.0){
                                    //Set the alert dialog that will ask the user if they want to pick up the coin
                                    AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Do you want to pick this coin up? \nCurrency: "+props.getString("currency")+" \nValue: "+ props.getString("value") )
                                        .setTitle("Pick up the coin")
                                        .setPositiveButton("Yes", (dialog, which) -> {

                                            //Pick up the coin, so store it in the coins hashmap. Then delete it
                                            try {
                                                String value = props.getString("value");
                                                String currency = props.getString("currency");
                                                //Save the coin with the others
                                                addCoinsOverall(MainActivity.this, marker.getSnippet(), value, currency);
                                                //Update the text to add the new coin to the counter
                                                changeText();
                                                addPickedCoin(MainActivity.this, marker.getSnippet());
                                                mapboxMap.removeMarker(marker);
                                            } catch (JSONException e) {
                                                Log.e("putInPrefs", ""+e);
                                            }
                                            if(Medals.checkGoals(context)){
                                                Toast.makeText(context, "New goal/s achieved!", Toast.LENGTH_SHORT).show();
                                            }
                                            //Add one to the total of picked coins
                                            setCoinsPicked(context, 1);
                                            Log.d(TAG, "Coin picked");
                                        })
                                        .setNegativeButton("No", (dialog, which) -> {
                                            //Nothing here, so it will close the dialog when "No" is clicked
                                        });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }else{
                                    //If the user is too far, show a toast saying so
                                    Toast.makeText(MainActivity.this, "You are too far from the coin.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    });
                }

            } catch (Exception e) {
                Log.e("markers","" + e);
                e.printStackTrace();
            }
        });
        changeText();
    }

    //Method for changing a marker from vector to bitmap and changing its colour
    //Code from https://stackoverflow.com/questions/37805379/mapbox-for-android-changing-color-of-a-markers-icon
    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    public void getTheMap(){
        Log.d(TAG, "getTheMap() has been called");

        //Get the url
        String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + getDate() + "/coinzmap.geojson";

        //Use the downloadFile object for downloading the map
        try {
            jSon = new DownloadFileTask().execute(url).get();
            setJson(this, jSon);
            if(jSon.equals("Unable to load content. Check your network connection")){
                Toast.makeText(this, "Unable to load content. Check your network connection and restart the app.", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    //Method for getting the date of the device
    public static String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dateObject = new Date();
        return dateFormat.format(dateObject);
    }

    //The following methods are extracted from the videos on Learn and the lecture slides

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
    }

    private void enableLocation(){
        //Asks for the user's permission if it has not been given. Then initializes the location
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();
        }
        else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        //Initializes the location
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null){
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        }
        else{
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 16.0));
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast or something explaining
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart(){
        super.onStart();
        if(locationEngine != null){
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin != null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
        changeText();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(locationEngine != null){
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(locationEngine != null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    //Method for going to the menu from the main screen
    public void goToMenu (View view){
        Intent intMenu = new Intent(MainActivity.this, Menu.class);
        startActivity(intMenu);
    }

    //Method for updating the text on the map showing the coins collected and gold
    public void changeText(){
        Context context = MainActivity.this;
        //Get the number of coins
        int shil = getNumShil(context);
        int dolr = getNumDolr(context);
        int penny = getNumPenny(context);
        int quid = getNumQuid(context);
        //Build the string with the information
        String toChange = "Shillings: "+shil+"\nDollar: " +dolr+"\nPenny: "+penny+"\nQuid: "+quid+"\nGold: "+getGold(MainActivity.this);
        final TextView textView = findViewById(R.id.textView);
        //Change the text in the TextView object
        textView.setText(toChange);
    }


    /////////////////////////////////
    //                             //
    // GETTER AND SETTER FUNCTIONS //
    //   AND SHARED PREFERENCES    //
    //                             //
    /////////////////////////////////

    //Method for getting the SharedPreferences file
    private static SharedPreferences getPrefs(Context context){
        String PREF_NAME = "preferences";
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /////////////////////
    /*  Coins related  */
    /*    methods      */
    /////////////////////

    //Method for adding a coin to the list of picked ones
    public static void addPickedCoin(Context context, String picked){
        Gson gson = new Gson();
        //Get the arrayList with the coins that have been picked up already
        ArrayList<String> pickedCoins = getPickedCoins(context);
        //Add the new coin to the other ones
        pickedCoins.add(picked);
        //Get the arrayList back into a String
        String arrayPickedStr = gson.toJson(pickedCoins);
        //Save the coins picked into the SharedPrefs file
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("arrayPicked", arrayPickedStr);
        editor.commit();
    }
    //Get the coins that have been picked
    public static ArrayList<String> getPickedCoins(Context context){
        Gson gson = new Gson();
        //Create an empty arrayList
        ArrayList<String> empty = new ArrayList<>();
        empty.add("");
        //Get the arrayList (in form of String) from the SharedPrefs file, if it does not exist, then the string is empty
        String storedArrayString = getPrefs(context).getString("arrayPicked", "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>(){}.getType();
        //If the string is empty, then return the arrayList with "". Otherwise, return the string transformed into ArrayList
        if(storedArrayString.equals("")){
            return empty;
        }
        return gson.fromJson(storedArrayString, type);
    }

    //Method for getting the gold
    public static Float getGold(Context context){return getPrefs(context).getFloat("Gold", 0.0f);}
    //Method for setting the gold
    public static void setGold(Context context, Float amount){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putFloat("Gold", amount);
        editor.commit();
    }

    //Method for getting the Json map
    public static String getJson(Context context){
        return getPrefs(context).getString("Json", "");
    }
    //Method for setting the Json map
    public static void setJson(Context context, String jSon){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Json", jSon);
        editor.commit();
    }

    //Method for getting the overall of coins saved (all of them, not just the ones of the day)
    //Code from https://stackoverflow.com/questions/7944601/how-to-save-hashmap-to-shared-preferences
    //(This is repeated for all of the getting and setting methods)
    public static HashMap<String, String[]> getCoinsOverall(Context context){
        Gson gson = new Gson();
        HashMap<String, String[]> empty = new HashMap<>();
        empty.put("", new String[]{"", ""});
        String storedHashMapString = getPrefs(context).getString("hashCoins", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String[]>>(){}.getType();
        //If there is no stored hashmap, then return an empty hashmap
        if (storedHashMapString.equals("")){
            return empty;
        }
        //Transform the string into a hashmap and return it
        return gson.fromJson(storedHashMapString, type);
    }
    //Save the coins saved overall in th SharedPrefs
    public static void setCoinsOverall(Context context, HashMap<String, String[]> coinsOverall){
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(coinsOverall);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("hashCoins", hashCoinsStr);
        editor.commit();
    }
    //Add a coin to the overall coins
    public static void addCoinsOverall(Context context, String id, String rate, String currency){
        //Get the coins that are already saved
        HashMap<String, String[]> hashCoins = getCoinsOverall(context);
        //Add the new coin
        hashCoins.put(id, new String[]{rate, currency});
        //Save the new hashmap with the new coin
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(hashCoins);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("hashCoins", hashCoinsStr);
        editor.commit();
    }

    //Get the coins that the user has already cashed in
    public static ArrayList<String> getCoinsUsed(Context context){
        Gson gson = new Gson();
        ArrayList<String> usedCoins = new ArrayList<>();
        String storedUsedCoins = getPrefs(context).getString("usedCoins", "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(storedUsedCoins.equals("")){
            usedCoins.add("");
            return usedCoins;
        }
        usedCoins = gson.fromJson(storedUsedCoins, type);
        return usedCoins;
    }
    //Add a coin to the ones that have been used
    public static void addCoinsUsed(Context context, String id){
        Gson gson = new Gson();
        ArrayList<String> coinsUsed = getCoinsUsed(context);
        coinsUsed.add(id);
        String usedStr = gson.toJson(coinsUsed);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("usedCoins", usedStr);
        editor.commit();
    }

    //Get the last date saved on the sharedPrefs (usually this is equivalent to the date of the last time the app was opened)
    public static String getDatePrefs(Context context){
        return getPrefs(context).getString("Date", "");
    }
    //Set the date of the SharedPrefs to the current one
    public static void setDatePrefs(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Date", getDate());
        editor.commit();
    }

    //Get the number of own coins of each of the currency.
    public static int getNumShil(Context context){ setCoins(context);return coinsShil.size();}
    public static int getNumDolr(Context context){setCoins(context);return coinsDolr.size();}
    public static int getNumPenny(Context context){setCoins(context);return coinsPeny.size();}
    public static int getNumQuid(Context context){setCoins(context);return coinsQuid.size();}

    //Get the number of coins from friends of each of the currencies.
    public static int getNumShilFriends(Context context){setCoinsFriends(context);return coinsShilFriends.size();}
    public static int getNumDolrFriends(Context context){setCoinsFriends(context);return coinsDolrFriends.size();}
    public static int getNumPennyFriends(Context context){setCoinsFriends(context);return coinsPenyFriends.size();}
    public static int getNumQuidFriends(Context context){setCoinsFriends(context);return coinsQuidFriends.size();}

    //Get the coins from friends. Basically the same as getCoins
    public static HashMap<String, String[]> getCoinsFriends(Context context){
        Gson gson = new Gson();
        HashMap<String, String[]> empty = new HashMap<>();
        empty.put("", new String[]{"", ""});
        String storedHashMapString = getPrefs(context).getString("coinsFriends", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String[]>>(){}.getType();
        //If there is no stored hashmap, then return an empty hashmap
        if (storedHashMapString.equals("")){
            return empty;
        }
        //Transform the string into a hashmap and return it
        return gson.fromJson(storedHashMapString, type);
    }
    //Set the coins received from friends. Basically the same as setCoinsOverall
    public static void setCoinsOverallFriends(Context context, HashMap<String, String[]> coinsOverallFriends){
        String TAG = "setCoinsOverallFriends";
        Log.d(TAG, "Setting the overall friends coins");
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(coinsOverallFriends);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("coinsFriends", hashCoinsStr);
        editor.commit();
    }
    //Add a coin to the ones received from friends.
    public static void addCoinsFriends(Context context, String id, String rate, String currency){
        String TAG = "addCoinsFriends";
        Log.d(TAG, "Adding coin " + id);
        Gson gson = new Gson();
        HashMap<String, String[]> hashCoins = getCoinsFriends(context);
        hashCoins.put(id, new String[]{rate, currency});
        String hashCoinsStr = gson.toJson(hashCoins);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("coinsFriends", hashCoinsStr);
        editor.commit();
    }

    //HashMaps for storing the own coins. They map ID to value and currency
    public static HashMap<String, String[]> coinsQuid = new HashMap<>();
    public static HashMap<String, String[]> coinsPeny = new HashMap<>();
    public static HashMap<String, String[]> coinsDolr = new HashMap<>();
    public static HashMap<String, String[]> coinsShil = new HashMap<>();

    //Set the hashmaps for each of the coins.
    public static void setCoins(Context context){
        HashMap<String, String[]> hashCoins = getCoinsOverall(context);
        for(String hash:hashCoins.keySet()){
            String[] content = hashCoins.get(hash);
            switch (content[1]) {
                case "QUID":
                    coinsQuid.put(hash, content);
                    break;
                case "DOLR":
                    coinsDolr.put(hash, content);
                    break;
                case "PENY":
                    coinsPeny.put(hash, content);
                    break;
                case "SHIL":
                    coinsShil.put(hash, content);
                    break;
            }
        }
    }

    //HashMaps for storing the friends coins. They map ID to value and currency
    public static HashMap<String, String[]> coinsQuidFriends = new HashMap<>();
    public static HashMap<String, String[]> coinsPenyFriends = new HashMap<>();
    public static HashMap<String, String[]> coinsDolrFriends = new HashMap<>();
    public static HashMap<String, String[]> coinsShilFriends = new HashMap<>();

    public static void setCoinsFriends(Context context){
        HashMap<String, String[]> hashCoins = getCoinsFriends(context);
        for(String hash:hashCoins.keySet()){
            String[] content = hashCoins.get(hash);
            switch (content[1]) {
                case "QUID":
                    coinsQuidFriends.put(hash, content);
                    break;
                case "DOLR":
                    coinsDolrFriends.put(hash, content);
                    break;
                case "PENY":
                    coinsPenyFriends.put(hash, content);
                    break;
                case "SHIL":
                    coinsShilFriends.put(hash, content);
                    break;
            }
        }
    }



    /////////////////////
    /*  Other methods  */
    /////////////////////

    //Check if it is the first time that the user opens the app (after login in)
    public static boolean firstTime(Context context){
        boolean ret = getPrefs(context).getBoolean("FirstTimeApp", true);
        if (ret){
            //The first time the app is opened, so the next time it won't be. Set to false
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putBoolean("FirstTimeApp", false);
            editor.commit();
        }
        return ret;
    }

    //Reset the variables that need to be reset depending on the date, like coins that have been picked up that day or goals.
    public static void resetVariables(Context context){
        Bank.resetCoinsCashed(context);
        News.resetFirstTimeToday(context);
        News.resetNewsSeen(context);
    }

    //Get the number of coins tha the user has collected in total
    public static int getNumCoinsPicked(Context context){ return getPrefs(context).getInt("coinsPickedTotal", 0);}
    //Set the number of coins the user has collected in total
    public static void setCoinsPicked(Context context, int newPicked){
        int alreadyPicked = getNumCoinsPicked(context);
        alreadyPicked = alreadyPicked+newPicked;
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("coinsPickedTotal", alreadyPicked);
        editor.commit();
    }


    //Method for getting the distance given latitude and longitude
    //Code from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @return Distance in Meters
     */
    public static double getDistance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}