package bentownshend.assignment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InformationActivity extends AppCompatActivity {

    JSONObject response;
    TextView atmName;
    TextView atmAddress;
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

        DisplayData(name, address);
    }

    private void DisplayData(String name, String address) {
        atmName = (TextView)findViewById(R.id.atmName);
        atmAddress = (TextView)findViewById(R.id.atmAddress);
        atmFavourite = (Button)findViewById(R.id.favouriteButton);

        atmName.setText(name);
        atmAddress.setText(address);

        File file = getFileStreamPath("atm-" + name.replace(" ", ""));
        if (file == null || !file.exists()) atmFavourite.setText("Add to Favourites");
        else if (file.exists()) atmFavourite.setText("Remove Favourite");
    }

    public void favouriteClick(View view) {
        // TODO add to favourites
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
                outputStream.close();

                atmFavourite.setText("Remove Favourite");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (file.exists()) {
            file.delete();
            // delete file here
//            atmFavourite.setText("Add to Favourites");
        }
    }

    public void directionsClick(View view) {
        // TODO check for location privileges here
        // TODO load directions
    }
}
