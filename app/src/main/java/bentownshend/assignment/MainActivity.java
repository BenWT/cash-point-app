package bentownshend.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scanClick(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
//            Toast.makeText(MainActivity.this, "Already have permission!", Toast.LENGTH_SHORT).show();
            LoadMap();
        }
    }

    public void favouriteClick(View view) {
        Toast.makeText(MainActivity.this, "Should show favourites!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted to access location!", Toast.LENGTH_SHORT).show();
                    LoadMap();
                } else {
                    Toast.makeText(MainActivity.this, "Could not access your location!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void LoadMap() {
        // TODO: get location
        // TODO: open map intent

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        double lat = 0.0, longi = 0.0;

        if (lastKnownLocation != null) {
            lat = lastKnownLocation.getLatitude();
            longi = lastKnownLocation.getLongitude();
        }

        Intent map = new Intent(this, MapsActivity.class);
        map.putExtra("curLat", lat);
        map.putExtra("curLongi", longi);
        startActivity(map);
    }
}
