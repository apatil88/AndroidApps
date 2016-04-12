package thecompleteandroiddeveloper.hikerswatch;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private static final String TAG = MainActivity.class.getCanonicalName();
    String provider;

    TextView latitude;
    TextView longitude;
    TextView accuracyTV;
    TextView speedTV;
    TextView bearingTV;
    TextView altitude;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Criteria criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        accuracyTV = (TextView) findViewById(R.id.accuracy);
        speedTV = (TextView) findViewById(R.id.speed);
        bearingTV = (TextView) findViewById(R.id.bearing);
        altitude = (TextView) findViewById(R.id.altitude);
        address = (TextView) findViewById(R.id.address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            checkPermission(Context.LOCATION_SERVICE, 0, 0);
            locationManager.requestLocationUpdates(provider, 1000, 1, this);
        } catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            checkPermission(Context.LOCATION_SERVICE, 0, 0);
            locationManager.removeUpdates(this);
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        Double alt = location.getAltitude();
        Float bearing = location.getBearing();
        Float speed = location.getSpeed();
        Float accuracy = location.getAccuracy();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 1);
            String addressHolder = "";
            if(listAddresses != null && listAddresses.size() > 0){
                Log.d(TAG, "Place Info : " + listAddresses.get(0).toString());

                for(int i = 0; i <= listAddresses.get(0).getMaxAddressLineIndex(); i++){
                    addressHolder += listAddresses.get(0).getAddressLine(i) + "\n";
                }
                address.setText("Address: \n" + addressHolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        latitude.setText("Latitude : " + lat.toString());
        longitude.setText("Longitude : " + lng.toString());
        altitude.setText("Altitude : " + alt.toString() + " m");
        bearingTV.setText("Bearing : " + bearing.toString());
        speedTV.setText("Speed : " + speed.toString() + " m/s");
        accuracyTV.setText("Accuracy : " + accuracy.toString());

        Log.d(TAG, "Latitude : " + lat.toString());
        Log.d(TAG, "Longitude : " + lng.toString());
        Log.d(TAG, "Altitude : " + alt.toString());
        Log.d(TAG, "Bearing : " + bearing.toString());
        Log.d(TAG, "Speed : " + speed.toString());
        Log.d(TAG, "Accuracy :" + accuracy.toString());
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
