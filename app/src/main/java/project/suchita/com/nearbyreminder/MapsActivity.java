package project.suchita.com.nearbyreminder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected List nearestPlaces;
    protected String categoryType;
    // protected LocationListener locationListener;
    private static final int MY_PERMISSIONS_REQUEST_GRANTED = 1;
    private Map<Marker, Place> allMarkersMap;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        categoryType = sharedPref.getString("placeType", "");
        Log.e("Placetype", "" + categoryType);

        if (categoryType == null || categoryType.length() == 0) {

            Intent intent = new Intent(this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        } else {
            setContentView(R.layout.activity_maps);
        }
       allMarkersMap = new HashMap<Marker, Place>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        //check the category type
    }

    public void getPlacesInfo(final Location currentLocation) {


        String urlString = "location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius=" + 1000 + "&types=" + categoryType + "&sensor=" + true + "&key=" + "AIzaSyBcH08vNk2s7qWF43UJGA61iHGPZH0TshU";
        Log.e("API URL ", "URL" + urlString);

        RestClient.get(urlString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.e("Response", "response" + response);
                // Get the rsults fron response object
                try {

                    //1 Clear map view
                    mMap.clear();
                    // 2 now set markes using response  object
                    nearestPlaces = new ArrayList();

                    JSONArray results = response.getJSONArray("results");
                    if (results.length() == 0)
                        return;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (int count = 0; count < results.length() - 1; count++) {

                        Place place = new Place();
                        JSONObject obj = results.optJSONObject(count);
                        if (obj.has("name"))
                            place.setName(obj.getString("name"));
                        if (obj.has("icon"))
                            place.setIconUrl(obj.getString("icon"));
                        if (obj.has("rating"))
                            place.setRatings(obj.getDouble("rating"));
                        if (obj.has("vicinity"))
                            place.setAddress(obj.getString("vicinity"));

                        if (obj.has("geometry")) {
                            JSONObject geometryObj = obj.getJSONObject("geometry");
                            if (geometryObj.has("location")) {
                                JSONObject locationObj = geometryObj.getJSONObject("location");
                                if (locationObj.has("lat"))
                                    place.setLatitude(locationObj.getDouble("lat"));
                                if (locationObj.has("lng"))
                                    place.setLongitude(locationObj.getDouble("lng"));
                            }
                        }
                        nearestPlaces.add(place);

                        // Create position
                        LatLng markerLatLong = new LatLng(place.getLatitude(), place.getLongitude());

                        Location markerLoc = new Location("");
                        markerLoc.setLatitude(markerLatLong.latitude);
                        markerLoc.setLongitude(markerLatLong.longitude);

                        // Create marakes
                        float distance = currentLocation.distanceTo(markerLoc) / 1000;
                        String distanceStr = String.format(Locale.getDefault(), "%.1f", distance)
                                + " km Away";
                        Marker markers = mMap.addMarker(new MarkerOptions()
                                .position(markerLatLong)
                                .title(place.getName())
                                .snippet(distanceStr)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        markers.showInfoWindow();
                        allMarkersMap.put(markers, place);
                        // Add option to bonds builder
                        builder.include(markers.getPosition());

                    }
                    if(builder!= null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GRANTED);

            return;
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override

            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this,AddReminderActivity.class);
                Place place =  allMarkersMap.get(marker);
                intent.putExtra("placeData",  place);
                startActivity(intent);

            }
        });

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null)
            getPlacesInfo(location);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GRANTED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    return;
                }

            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }


    @Override
    public void onLocationChanged(Location location) {
        if (mMap != null)
            getPlacesInfo(location);
        Log.e("Location", "" + location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuitem, menu);
        return true;
    }
}