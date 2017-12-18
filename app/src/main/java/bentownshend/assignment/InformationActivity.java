package bentownshend.assignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class InformationActivity extends AppCompatActivity {

    TextView atmName, atmAddress, atmWheelchair, atmBalance, atmPin;
    Button atmFavourite;
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Bundle extras = getIntent().getExtras();

        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");
        String name = extras.getString("name");
        String address = extras.getString("address");
        String wheelchair = extras.getString("wheelchair");
        String balance = extras.getString("balance");
        String pin = extras.getString("pin");

        DisplayData(name, address, wheelchair, balance, pin);
    }

    private void DisplayData(String name, String address, String wheelchair, String balance, String pin) {
        atmName = (TextView)findViewById(R.id.atmName);
        atmAddress = (TextView)findViewById(R.id.atmAddress);
        atmWheelchair = (TextView)findViewById(R.id.atmWheelchair);
        atmBalance = (TextView)findViewById(R.id.atmBalance);
        atmPin = (TextView)findViewById(R.id.atmPin);
        atmFavourite = (Button)findViewById(R.id.favouriteButton);

        atmName.setText(name);
        atmAddress.setText(address);
        atmWheelchair.setText(wheelchair);
        atmBalance.setText(balance);
        atmPin.setText(pin);

        File file = getFileStreamPath("atm-" + name.replace(" ", ""));
        if (file == null || !file.exists()) atmFavourite.setText("Add to Favourites");
        else if (file.exists()) atmFavourite.setText("Remove Favourite");
    }

    public void favouriteClick(View view) {
        String filename = "atm-" + atmName.getText().toString().replace(" ", "");
        FileOutputStream outputStream;
        File file = getFileStreamPath(filename);

        if (file == null || !file.exists()) {
            try {
                outputStream = openFileOutput(filename, MODE_PRIVATE);
                outputStream.write((latitude + "\n").getBytes());
                outputStream.write((longitude + "\n").getBytes());
                outputStream.write((atmName.getText() + "\n").getBytes());
                outputStream.write((atmAddress.getText() + "\n").getBytes());
                outputStream.write((atmWheelchair.getText() + "\n").getBytes());
                outputStream.write((atmBalance.getText() + "\n").getBytes());
                outputStream.write((atmPin.getText() + "\n").getBytes());
                outputStream.close();

                atmFavourite.setText("Remove Favourite");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(InformationActivity.this, "Could not add to favourites!", Toast.LENGTH_SHORT).show();
            }
        } else if (file.exists()) {
            file.delete();
            atmFavourite.setText("Add to Favourites");
        }
    }

    public void directionsClick(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(InformationActivity.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            String locationProvider = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                if (lastKnownLocation != null) {
                    String url = "https://www.google.com/maps/dir/?api=1&";
                    String parameters = "travelmode=walking&origin=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() + "&destination=" + latitude + "," + longitude;
                    Uri uri = Uri.parse(url + parameters);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(InformationActivity.this, "Could not access your location!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(InformationActivity.this, "Could not access your location!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InformationActivity.this, "Network connection not detected!", Toast.LENGTH_SHORT).show();
        }
    }
}
