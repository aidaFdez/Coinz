package com.example.aafo.coinz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.LineLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private String jSon;
    private HashMap<String, Integer> coinsToday = new HashMap<String, Integer>();
    public static HashMap<String, Float> ratesHash = new HashMap<String, Float>();

    private static Logger logger = Logger.getLogger("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Show the map on the screen (without the coinz)
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        logger.finer("The map is showing now");

        //If the last time that the app was opened is the same day, then do not download the coins again
        /*if(!(getDate().equals(getDatePrefs(MainActivity.this)))){
            setDatePrefs(MainActivity.this);

        }*/
        getTheMap();
        setDatePrefs(MainActivity.this);

        //Code based on http://www.mapbox.com.s3-website-us-east-1.amazonaws.com/android-sdk/examples/geojson/
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                //ArrayList<MarkerOptions> optionsList = new ArrayList<>();
                try {
                    //HashMap<String, String[]> pickedCoins = new HashMap<>();
                    JSONObject jSonObj = new JSONObject(jSon);
                    JSONArray features = jSonObj.getJSONArray("features");

                    JSONObject rates = jSonObj.getJSONObject("rates");
                    Float rateQuid = Float.parseFloat(rates.getString("QUID"));
                    Float ratePeny = Float.parseFloat(rates.getString("PENY"));
                    Float rateDolr = Float.parseFloat(rates.getString("DOLR"));
                    Float rateShil = Float.parseFloat(rates.getString("SHIL"));

                    ratesHash.put("QUID", rateQuid);
                    ratesHash.put("PENY", ratePeny);
                    ratesHash.put("DOLR", rateDolr);
                    ratesHash.put("SHIL", rateShil);

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
                        if(curr.equals("SHIL")){
                            colour = getResources().getColor(R.color.SHIL);
                        }
                        if(curr.equals("QUID")){
                            colour = getResources().getColor(R.color.QUID);
                        }
                        if(curr .equals("PENY")){
                            colour = getResources().getColor(R.color.PENY);
                        }
                        if(curr.equals("DOLR")){
                            colour = getResources().getColor(R.color.DOLR);
                        }
                        Icon icon = drawableToIcon(context, R.drawable.loc_icon,colour);

                        String id = properties.getString("id");

                        MarkerOptions coin = new MarkerOptions();
                        coin.position(new LatLng(coords.getDouble(1), coords.getDouble(0)))
                                .icon(icon)
                                .setSnippet(id);
                        coinsToday.put(id, i);
                        //This actually works, the problem is the data stored in the coins. Could have Hm of not picked(?)
                        //If the coin has been picked up already, do not put it in the map
                        if(!(getCoinsOverall(MainActivity.this).containsKey(properties.getString("id"))) &&
                           !(getPickedCoins(MainActivity.this).contains(properties.getString("id")))){
                            mapboxMap.addMarker(coin);
                        }else{
                            //Toast.makeText(MainActivity.this, "Did have it" +i, Toast.LENGTH_LONG).show();
                        }

                        //Code from https://www.mapbox.com/android-docs/maps/overview/annotations/
                        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                /*Toast.makeText(MainActivity.this, "Works", Toast.LENGTH_LONG).show();
                                return true;*/
                                //Create an alert dialog for checking if the user wants to pick up that coin
                                //AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                                try {
                                    JSONObject props = features.getJSONObject(coinsToday.get(marker.getSnippet()))
                                            .getJSONObject("properties");
                                    LatLng markerLtLn = marker.getPosition();
                                    double markerLt = markerLtLn.getLatitude();
                                    double markerLn = markerLtLn.getLongitude();
                                    double userLt = originLocation.getLatitude();
                                    double userLn = originLocation.getLongitude();
                                    double distance = getDistance(markerLt, userLt, markerLn, userLn, 0.0, 0.0);
                                    //if(distance<25.0){
                                        AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                                        //Toast.makeText(MainActivity.this, "Distance less", Toast.LENGTH_LONG).show();
                                        builder.setMessage("Do you want to pick this coin up? \nCurrency: "+props.getString("currency")+" \nValue: "+ props.getString("value") )
                                                .setTitle("Pick up the coin")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        //Pick up the coin, so store it in the coins hashmap. Then delete it
                                                        try {
                                                            //JSONObject props = features.getJSONObject(coinsToday.get(marker.getSnippet()))
                                                            //                  .getJSONObject("properties");
                                                            String value = props.getString("value");
                                                            String currency = props.getString("currency");
                                                            addCoinsOverall(MainActivity.this, marker.getSnippet(), value, currency);
                                                            addCoins(MainActivity.this, currency, 1);
                                                            //addCoinsSw(MainActivity.this, 1, currency);
                                                            changeText();
                                                            addPickedCoin(MainActivity.this, marker.getSnippet());
                                                        } catch (JSONException e) {
                                                            Log.e("putInPrefs", ""+e);
                                                        }
                                                        mapboxMap.removeMarker(marker);
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Nothing here, so it will close the dialog when "No" is clicked
                                                    }
                                                });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    //}else{
                                    //    Toast.makeText(MainActivity.this, "You are too far from the coin.", Toast.LENGTH_LONG).show();
                                    //}

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //AlertDialog dialog = builder.create();
                                //dialog.show();
                                return true;
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("markers","" + e);
                    e.printStackTrace();
                }
            }
        });
        changeText();
    }

    //Code from https://stackoverflow.com/questions/37805379/mapbox-for-android-changing-color-of-a-markers-icon
    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    public void getTheMap(){
        logger.finer("getTheMap() has been called");

        //Context context = getApplicationContext();
        //int duration = Toast.LENGTH_SHORT;


        //Get the url
        String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + getDate() + "/coinzmap.geojson";

        //Toast toast = Toast.makeText(context, url, duration);
        //toast.show();

        //Use the downloadFile object for downloading the map
        try {
            jSon = new DownloadFileTask().execute(url).get();
            setJson(this, jSon);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //Method for getting the date of the device
    public static String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dateObject = new Date();
        String date = dateFormat.format(dateObject);
        return date;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
    }

    private void enableLocation(){
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

    public void goToMenu (View view){
        Intent intMenu = new Intent(MainActivity.this, Menu.class);
        startActivity(intMenu);
    }

    public void changeText(){
        Context context = MainActivity.this;
        int shil = getNumShil(context);
        int dolr = getNumDolr(context);
        int penny = getNumPenny(context);
        int quid = getNumQuid(context);
        String toChange = "Shellins: "+shil+"\nDollar: " +dolr+"\nPenny: "+penny+"\nQuid: "+quid+"\nGold: "+getGold(MainActivity.this);
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(toChange);
    }

    public void addCoins(Context context, String currency, int num){
        if(currency.equals("SHIL")){
            addShil(context, num);
        }
        if(currency.equals("DOLR")){
            addDolr(context, num);
        }
        if(currency.equals("QUID")){
            addQuid(context, num);
        }
        if(currency.equals("PENY")){
            addPenny(context, num);
        }

    }


    ////////////////////////////////
    //                            //
    // GETTER AND SETTER FUNCTIONS//
    //   AND SHARED PREFERENCES   //
    //                            //
    ////////////////////////////////

    //Code based on https://stackoverflow.com/questions/23351904/getting-cannot-resolve-method-error-when-trying-to-implement-getsharedpreferen
    private SharedPreferences sharedPrefs;
    private static String PREF_NAME = "preferences";

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static void addPickedCoin(Context context, String picked){
        Gson gson = new Gson();
        ArrayList<String> pickedCoins = getPickedCoins(context);
        pickedCoins.add(picked);
        String arrayPickedStr = gson.toJson(pickedCoins);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("arrayPicked", arrayPickedStr);
        editor.commit();

    }

    private static ArrayList<String> getPickedCoins(Context context){
        Gson gson = new Gson();
        ArrayList<String> empty = new ArrayList<String>();
        empty.add("");
        String storedArrayString = getPrefs(context).getString("arrayPicked", "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(storedArrayString.equals("")){
            return empty;
        }
        ArrayList<String> array = gson.fromJson(storedArrayString, type);
        return array;
    }

    public static Float getGold(Context context){
        return getPrefs(context).getFloat("Gold", 0.0f);
    }

    public static void setGold(Context context, Float amount){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putFloat("Gold", amount);
        editor.commit();
    }

    public static String getJson(Context context){
        return getPrefs(context).getString("Json", "");
    }

    public static void setJson(Context context, String jSon){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Json", jSon);
        editor.commit();
    }

    //Code from https://stackoverflow.com/questions/7944601/how-to-save-hashmap-to-shared-preferences
    //Gson gson = new Gson();
    public static HashMap<String, String[]> getCoinsOverall(Context context){
        Gson gson = new Gson();
        HashMap<String, String[]> empty = new HashMap<String, String[]>();
        empty.put("", new String[]{"", ""});
        String storedHashMapString = getPrefs(context).getString("hashCoins", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String[]>>(){}.getType();
        //If there is no stored hashmap, then return an empty hashmap
        if (storedHashMapString.equals("")){
            return empty;
        }
        //Transform the string into a hashmap and return it
        HashMap<String, String[]> coinsHash = gson.fromJson(storedHashMapString, type);
        return coinsHash;
    }

    public static void setCoinsOverall(Context context, HashMap<String, String[]> coinsOverall){
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(coinsOverall);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("hashCoins", hashCoinsStr);
        editor.commit();
    }

    public static void addCoinsOverall(Context context, String id, String rate, String currency){
        HashMap<String, String[]> hashCoins = getCoinsOverall(context);
        hashCoins.put(id, new String[]{rate, currency});
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(hashCoins);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("hashCoins", hashCoinsStr);
        editor.commit();
    }

    public static ArrayList<String> getCoinsUsed(Context context){
        Gson gson = new Gson();
        ArrayList<String> usedCoins = new ArrayList<String>();
        String storedUsedCoins = getPrefs(context).getString("usedCoins", "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(storedUsedCoins.equals("")){
            usedCoins.add("");
            return usedCoins;
        }
        usedCoins = gson.fromJson(storedUsedCoins, type);
        return usedCoins;
    }

    public static void addCoinsUsed(Context context, String id){
        Gson gson = new Gson();
        ArrayList<String> coinsUsed = getCoinsUsed(context);
        coinsUsed.add(id);
        String usedStr = gson.toJson(coinsUsed);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("usedCoins", usedStr);
        editor.commit();
    }

    public static String getDatePrefs(Context context){
        return getPrefs(context).getString("Date", "");
    }

    public static void setDatePrefs(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Date", getDate());
        editor.commit();
    }

    public static int getNumShil(Context context){
        return getPrefs(context).getInt("Shil", 0);
    }

    public static int getNumDolr(Context context){
        return getPrefs(context).getInt("Dolr", 0);
    }

    public static int getNumPenny(Context context){
        return getPrefs(context).getInt("Penny", 0);
    }

    public static int getNumQuid(Context context){
        return getPrefs(context).getInt("Quid", 0);
    }

    public static void addCoinsSw(Context context, int num, String currency){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        switch(currency){
            case "QUID":
                int quids = getNumQuid(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("Quid", num+quids);
                //editor.commit();

            case "DOLR":
                int dolrs = getNumDolr(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("Dolr", num+dolrs);
                //editor.commit();

            case "PENY":
                int pennies = getNumPenny(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("Penny", num+pennies);
                //editor.commit();

            case "SHIL":
                int shils = getNumShil(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("Shil", num+shils);
                //editor.commit();
        }
        editor.commit();
    }

    public static void addQuid(Context context, int num){
        int actual = getNumQuid(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Quid", num+actual);
        editor.commit();
    }

    public static void subQuid(Context context, int num){
        int actual = getNumQuid(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Quid", -num+actual);
        editor.commit();
    }

    public static void addDolr(Context context, int num){
        int actual = getNumDolr(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Dolr", num+actual);
        editor.commit();
    }

    public static void subDolr(Context context, int num){
        int actual = getNumDolr(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Dolr", -num+actual);
        editor.commit();
    }

    public static void addPenny(Context context, int num){
        int actual = getNumPenny(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Penny", num+actual);
        editor.commit();
    }

    public static void subPenny(Context context, int num){
        int actual = getNumPenny(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Penny", -num+actual);
        editor.commit();
    }

    public static void addShil(Context context, int num){
        int actual = getNumShil(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Shil", num+actual);
        editor.commit();
    }

    public static void subShil(Context context, int num){
        int actual = getNumShil(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Shil", -num+actual);
        editor.commit();
    }

    public static int getNumShilFriends(Context context){ return getPrefs(context).getInt("ShilFriends", 0);}

    public static int getNumDolrFriends(Context context){ return getPrefs(context).getInt("DolrFriends", 0); }

    public static int getNumQuidFriends(Context context){ return getPrefs(context).getInt("QuidFriends", 0); }

    public static int getNumPennyFriends(Context context){ return getPrefs(context).getInt("PennyFriends", 0); }

    public static void addQuidFriends(Context context, int num){
        int actual = getNumQuidFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("QuidFriends", num+actual);
        editor.commit();
    }

    public static void addDolrFriends(Context context, int num){
        int actual = getNumDolrFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("DolrFriends", num+actual);
        editor.commit();
    }

    public static void addShilFriends(Context context, int num){
        int actual = getNumShilFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("ShilFriends", num+actual);
        editor.commit();
    }

    public static void addPennyFriends(Context context, int num){
        int actual = getNumPennyFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("PennyFriends", num+actual);
        editor.commit();
    }

    public static void subQuidFriends(Context context, int num){
        int actual = getNumQuidFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("QuidFriends", -num+actual);
        editor.commit();
    }

    public static void subDolrFriends(Context context, int num){
        int actual = getNumDolrFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("DolrFriends", -num+actual);
        editor.commit();
    }

    public static void subShilFriends(Context context, int num){
        int actual = getNumShilFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("ShilFriends", -num+actual);
        editor.commit();
    }

    public static void subPennyFriends(Context context, int num){
        int actual = getNumPennyFriends(context);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("PennyFriends", -num+actual);
        editor.commit();
    }

    public static void subCoinsFriends(Context context, int num, String currency){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        switch(currency){
            case "QUID":
                int quids = getNumQuidFriends(context);
                editor.putInt("QuidFriends", -num+quids);
                editor.commit();

            case "DOLR":
                int dlrs = getNumDolrFriends(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("DolrFriends", -num+dlrs);
                editor.commit();

            case "SHIL":
                int shils = getNumShilFriends(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("ShilFriends", -num+shils);
                editor.commit();

            case "PENY":
                int pennies = getNumPennyFriends(context);
                //SharedPreferences.Editor editor = getPrefs(context).edit();
                editor.putInt("PennyFriends", -num+pennies);
                editor.commit();
        }

    }

    public static HashMap<String, String[]> getCoinsFriends(Context context){
        Gson gson = new Gson();
        HashMap<String, String[]> empty = new HashMap<String, String[]>();
        empty.put("", new String[]{"", ""});
        String storedHashMapString = getPrefs(context).getString("coinsFriends", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String[]>>(){}.getType();
        //If there is no stored hashmap, then return an empty hashmap
        if (storedHashMapString.equals("")){
            return empty;
        }
        //Transform the string into a hashmap and return it
        HashMap<String, String[]> coinsFriends = gson.fromJson(storedHashMapString, type);
        return coinsFriends;
    }

    public static void setCoinsOverallFriends(Context context, HashMap<String, String[]> coinsOverallFriends){
        Gson gson = new Gson();
        String hashCoinsStr = gson.toJson(coinsOverallFriends);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("coinsFriends", hashCoinsStr);
        editor.commit();
    }

    public static void addCoinsFriends(Context context, String id, String rate, String currency){
        Gson gson = new Gson();
        HashMap<String, String[]> hashCoins = getCoinsOverall(context);
        hashCoins.put(id, new String[]{rate, currency});
        String hashCoinsStr = gson.toJson(hashCoins);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("coinsFriends", hashCoinsStr);
        editor.commit();
    }

    public static HashMap<String, String[]> coinsQuid = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsPeny = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsDolr = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsShil = new HashMap<String, String[]>();

    //Set the hashmaps for each of the coins.
    public static void setCoins(Context context){
        HashMap<String, String[]> hashCoins = getCoinsOverall(context);
        for(String hash:hashCoins.keySet()){
            String[] content = hashCoins.get(hash);
            if (content[1].equals("QUID")){
                coinsQuid.put(hash, content);
            }
            if (content[1].equals("DOLR")){
                coinsDolr.put(hash, content);
            }
            if (content[1].equals("PENY")){
                coinsPeny.put(hash, content);
            }
            if (content[1].equals("SHIL")){
                coinsShil.put(hash, content);
            }
        }
    }

    public static HashMap<String, String[]> coinsQuidFriends = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsPenyFriends = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsDolrFriends = new HashMap<String, String[]>();
    public static HashMap<String, String[]> coinsShilFriends = new HashMap<String, String[]>();

    public static void setCoinsFriends(Context context){
        HashMap<String, String[]> hashCoins = getCoinsFriends(context);
        for(String hash:hashCoins.keySet()){
            String[] content = hashCoins.get(hash);
            if (content[1].equals("QUID")){
                coinsQuidFriends.put(hash, content);
            }
            if (content[1].equals("DOLR")){
                coinsDolrFriends.put(hash, content);
            }
            if (content[1].equals("PENY")){
                coinsPenyFriends.put(hash, content);
            }
            if (content[1].equals("SHIL")){
                coinsShilFriends.put(hash, content);
            }
        }
    }

    //Code from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
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