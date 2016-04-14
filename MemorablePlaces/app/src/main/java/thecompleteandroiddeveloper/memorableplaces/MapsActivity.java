package thecompleteandroiddeveloper.memorableplaces;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener {

    private GoogleMap mMap;

    LocationManager mLocationManager;
    String provider;


    int location = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = mLocationManager.getBestProvider(new Criteria(), false);

        Intent i = getIntent();
        //Log.d("location", Integer.toString(i.getIntExtra("locationInfo", -1)));
        location = i.getIntExtra("locationInfo", -1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            e.printStackTrace();
        }


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

        mMap.setOnMapLongClickListener(this);

        if(location != -1 && location != 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.locations.get(location), 12));
            mMap.addMarker(new MarkerOptions()
                    .position(MainActivity.locations.get(location))
                    .title(MainActivity.places.get(location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            try {
                checkPermission(Context.LOCATION_SERVICE, 0, 0);
                mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            } catch (SecurityException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                try {
                    checkPermission(Context.LOCATION_SERVICE, 0, 0);
                    mLocationManager.removeUpdates(this);
                } catch (SecurityException e){
                    e.printStackTrace();
                }
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String label = new Date().toString();

        try {
            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(list != null && list.size() > 0){

                label = list.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.places.add(label);
        MainActivity.adapter.notifyDataSetChanged();
        MainActivity.locations.add(latLng);
        mMap.addMarker(new MarkerOptions()
                .position(MainActivity.locations.get(location))
                .title(MainActivity.places.get(location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(location == -1 || location == 0) {
            try {
                checkPermission(Context.LOCATION_SERVICE, 0, 0);
                mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            checkPermission(Context.LOCATION_SERVICE, 0, 0);
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location userLocation) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
