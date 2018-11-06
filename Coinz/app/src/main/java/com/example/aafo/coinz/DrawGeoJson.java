package com.example.aafo.coinz;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DrawGeoJson extends AsyncTask<String, Void, List<LatLng>> {
    @Override
    protected List<LatLng> doInBackground(String... str) {

        ArrayList<LatLng> points = new ArrayList<>();

        try {
            // Load GeoJSON file
            /*InputStream inputStream = getAssets().open("example.geojson");
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            inputStream.close();*/
            String jsonStr = str[0];

            // Parse JSON
            JSONObject json = new JSONObject(jsonStr);
            JSONArray features = json.getJSONArray("features");
            JSONObject feature = features.getJSONObject(0);
            JSONObject geometry = feature.getJSONObject("geometry");
            if (geometry != null) {
                String type = geometry.getString("type");

                // Our GeoJSON only has one feature: a line string
                if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")) {

            // Get the Coordinates
                    JSONArray coords = geometry.getJSONArray("coordinates");
                    for (int lc = 0; lc < coords.length(); lc++) {
                        JSONArray coord = coords.getJSONArray(lc);
                        LatLng latLng = new LatLng(coord.getDouble(1), coord.getDouble(0));
                        points.add(latLng);
                    }
                }
            }
        } catch (Exception exception) {
        }

        return points;
    }

    @Override
    protected void onPostExecute(List<LatLng> points) {
        super.onPostExecute(points);

        /*if (points.size() > 0) {

// Draw polyline on map
            mapboxMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .color(Color.parseColor("#3bb2d0"))
                    .width(2));
        }
    }*/
}
}