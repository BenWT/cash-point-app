package bentownshend.assignment;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker currentMarker;
    private Marker[] markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO: Load api data here
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Double curLat = extras.getDouble("curLat");
            Double curLongi = extras.getDouble("curLongi");
            LatLng curPos = new LatLng(curLat, curLongi);

            currentMarker = mMap.addMarker(new MarkerOptions().position(curPos).title("Current Location"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker != currentMarker) {
            // load info on marker
        }

        return false;
    }
}
