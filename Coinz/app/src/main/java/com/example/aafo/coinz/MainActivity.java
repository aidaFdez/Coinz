package com.example.aafo.coinz;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private String jSon;

    //Code based on https://stackoverflow.com/questions/23351904/getting-cannot-resolve-method-error-when-trying-to-implement-getsharedpreferen
    private SharedPreferences sharedPrefs;
    private static String PREF_NAME = "preferences";

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

        getTheMap();

        /*Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, jSon.substring(0,300), duration);
        toast.show();*/

        //Code based on http://www.mapbox.com.s3-website-us-east-1.amazonaws.com/android-sdk/examples/geojson/
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                //ArrayList<MarkerOptions> optionsList = new ArrayList<>();
                try {
                    JSONObject jSonObj = new JSONObject(jSon);
                    JSONArray features = jSonObj.getJSONArray("features");
                    for(int i=0; i<features.length();i++){
                        JSONObject feature = features.getJSONObject(i);
                        JSONObject geometry = feature.getJSONObject("geometry");
                        JSONArray coords = geometry.getJSONArray("coordinates");
                        //JSONObject symbol = feature.getJSONObject("marker-symbol");
                        //JSONObject color = feature.getJSONObject("marker-color");

                        //IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.location_shil);
                        Context context = getApplicationContext();
                        Integer colour = 0;
                        //Icon icon = drawableToIcon(context, R.drawable.loc_icon, colour);
                        JSONObject props = feature.getJSONObject("properties");
                        String curr = props.getString("currency");

                        if(curr.equals("SHIL")){
                            Toast toast = Toast.makeText(context, "hey", Toast.LENGTH_LONG);
                            toast.show();
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
                        /*//Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, curr, duration);
                        toast.show();*/
                        Icon icon = drawableToIcon(context, R.drawable.loc_icon,colour);

                        MarkerOptions coin = new MarkerOptions();
                        coin.position(new LatLng(coords.getDouble(1), coords.getDouble(0)))
                                .icon(icon);
                        //optionsList.add(coin);
                        mapboxMap.addMarker(coin);
                    }

                } catch (Exception e) {
                    Log.e("markers","" + e);
                    e.printStackTrace();
                }
            }
        });


        if(jSon != null){
            //System.out.println(jSon);
            /*Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, jSon.substring(0,30), duration);
            toast.show();*/
        }
        else{
            logger.finer("The GeoJson map does not exist");
            /*Context context = getApplicationContext();
            CharSequence text = "The map could not be loaded";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();*/
        }
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

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;


        //Get the url
        String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + getDate() + "/coinzmap.geojson";

        //Toast toast = Toast.makeText(context, url, duration);
        //toast.show();

        //Use the downloadFile object for downloading the map
        try {
            jSon = new DownloadFileTask().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //Method for getting the date of the device
    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dateObject = new Date();
        String date = dateFormat.format(dateObject);
        return date;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();

        //Code for showing the coins. Gotten from https://medium.com/nextome/show-a-geojson-layer-on-google-maps-osm-mapbox-on-android-cd75b8377ba
        //GeoJsonSource source = new GeoJsonSource("geojson", jSon);
        //map.addSource(source);
        //map.addLayer(new LineLayer("geojson", "geojson"));


        //FeatureCollection featureCollection = FeatureCollection.fromJson(jSon);
        //List<Feature> features = featureCollection.getFeatures();

        /*Ver por que no furula
        //List<Feature> features = featureCollection.getFeatures();
        FeatureCollection parsed = (FeatureCollection) GeoJson.parse(jSon);*/

        //COde from http://www.mapbox.com.s3-website-us-east-1.amazonaws.com/android-sdk/examples/geojson/
        /*try {
            //FeatureCollection featureCollection = FeatureCollection.fromJson(jSon);
            JSONObject jsonMap = new JSONObject(jSon);
            JSONArray features = jsonMap.getJSONArray("features");

            for(int i = 0;i<features.length(); i++){
                JSONObject obj = features.getJSONObject(i);
                JSONObject geometry = obj.getJSONObject("geometry");

                //HACERLO CON OTRA CLASE NUEVA COMO LO HACE LA PÁGINA ABIERTA
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        /*ArrayList<LatLng> points= new ArrayList<>();
        try {
            points = (ArrayList<LatLng>) new DrawGeoJson().execute(jSon).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(points != null){
            for (LatLng point : points) {
                map.addMarker(new MarkerOptions().position(point));
            }
        }
        else{
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "No points were found", duration);
            toast.show();
        }*/

        //GeoJsonLayer layer =

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
                location.getLongitude()), 14.0));
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


    ////////////////////////////////
    //                            //
    // GETTER AND SETTER FUNCTIONS//
    //                            //
    ////////////////////////////////

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Integer getGold(Context context){
        return getPrefs(context).getInt("Gold", 0);
    }

    public static void setGold(Context context, Integer amount){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Gold", amount);
    }

    public static String getJson(Context context){
        return getPrefs(context).getString("Json", "");
    }

}