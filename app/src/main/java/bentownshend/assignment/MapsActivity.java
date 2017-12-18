package bentownshend.assignment;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private List<ATM> atms = new ArrayList<>();

    Bundle extras;

    String url = "https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry";
    String clientUser = "E16N805EFQ6CW3ZRFK9O216wu_66iY8r8SvsD6c-UyrGNnR9A";
    String clientPass = "O0diQOzmgRzzx675Q3PnSMeYBf4QfP4FBvsI9";

    String basicAuth = "Basic " + Base64.encodeToString(
            (clientUser + ":" + clientPass).getBytes(), Base64.NO_WRAP);

    private class ATM {
        double latitude, longitude;
        boolean createdMarker = false;
        String id, name, address, response, wheelchair, balance, pin;

        ATM() {}

        void CreateMarker() {
            if (mMap != null && !createdMarker) {
                LatLng curPos = new LatLng(latitude, longitude);
                id = mMap.addMarker(new MarkerOptions().position(curPos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).getId();
                createdMarker = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        extras = getIntent().getExtras();

        try {
            fetchNetworkData(extras.getDouble("curLat"), extras.getDouble("curLong"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMarkerClickListener(this);

        if (extras != null) {
            LatLng curPos = new LatLng(extras.getDouble("curLat"), extras.getDouble("curLong"));
            mMap.addMarker(new MarkerOptions().position(curPos).title("Current Location"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
        }

        for (int i = 0; i < atms.size(); i++) {
            atms.get(i).CreateMarker();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!Objects.equals(marker.getTitle(), "Current Location")) {
            for (int i = 0; i < atms.size(); i++) {
                if (Objects.equals(marker.getId(), atms.get(i).id)) {
                    Intent info = new Intent(this, InformationActivity.class);
                    info.putExtra("latitude", atms.get(i).latitude);
                    info.putExtra("longitude", atms.get(i).longitude);
                    info.putExtra("name", atms.get(i).name);
                    info.putExtra("address", atms.get(i).address);
                    info.putExtra("wheelchair", atms.get(i).wheelchair);
                    info.putExtra("balance", atms.get(i).balance);
                    info.putExtra("pin", atms.get(i).pin);
                    startActivity(info);
                }
            }
        }

        return false;
    }

    protected void fetchNetworkData(double latitude, double longitude) throws JSONException {
        JSONObject context = new JSONObject();
        context.put("requestData", generateVisaQuery(latitude, longitude));

        JsonObjectRequest visaRequest = new JsonObjectRequest(Request.Method.POST, url, context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray responseData = response.getJSONArray("responseData").getJSONObject(0).getJSONArray("foundATMLocations");

                    for (int i = 0; i < responseData.length(); i++) {
                        ATM atm = new ATM();
                        atm.latitude = responseData.getJSONObject(i).getJSONObject("location").getJSONObject("coordinates").getDouble("latitude");
                        atm.longitude = responseData.getJSONObject(i).getJSONObject("location").getJSONObject("coordinates").getDouble("longitude");
                        atm.name = responseData.getJSONObject(i).getJSONObject("location").getString("ownerBusName");
                        atm.address = responseData.getJSONObject(i).getJSONObject("location").getJSONObject("address").getString("formattedAddress");
                        atm.response = responseData.getJSONObject(i).toString();

                        JSONArray properties = responseData.getJSONObject(i).getJSONObject("location").getJSONArray("properties");

                        for (int j = 0; j < properties.length(); j++) {
                            if (Objects.equals(properties.getJSONObject(j).getString("name"), "WHEELCHAIR")) {
                                if (Objects.equals(properties.getJSONObject(j).getString("value"), "Y")) atm.wheelchair = "Wheelchair Accessible";
                                else if (Objects.equals(properties.getJSONObject(j).getString("value"), "N")) atm.wheelchair = "No Wheelchair Access";
                            } else if (Objects.equals(properties.getJSONObject(j).getString("name"), "BALANCE_INQUIRY")) {
                                if (Objects.equals(properties.getJSONObject(j).getString("value"), "Y")) atm.balance = "Balance Inquiry Available";
                                else if (Objects.equals(properties.getJSONObject(j).getString("value"), "N")) atm.balance = "Balance Inquiry Unavailable";
                            } else if (Objects.equals(properties.getJSONObject(j).getString("name"), "PIN_CHANGE")) {
                                if (Objects.equals(properties.getJSONObject(j).getString("value"), "Y")) atm.pin = "Pin Change Available";
                                else if (Objects.equals(properties.getJSONObject(j).getString("value"), "N")) atm.pin = "Pin Change Unavailable";
                            }
                        }

                        atm.CreateMarker();

                        atms.add(atm);
                    }
                } catch (JSONException e) {
                    Toast.makeText(MapsActivity.this, "Problem with response data!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Uh oh, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", basicAuth);
                headers.put("Accept", "application/json");

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this, hurlStack);
        requestQueue.add(visaRequest);
    }

    HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(java.net.URL url)
                throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super
                    .createConnection(url);
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                InputStream in1 = getResources().openRawResource(R.raw.keycertbundle);
                keyStore.load(in1, clientPass.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                kmf.init(keyStore, clientPass.toCharArray());
                KeyManager[] keyManagers = kmf.getKeyManagers();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagers, null, null);

                httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };

    private JSONObject generateVisaQuery(double latitude, double longitude) throws JSONException {
        JSONObject sort = new JSONObject();
        sort.put("primary", "city");
        sort.put("direction", "desc");

        JSONObject range = new JSONObject();
        range.put("start", 10);
        range.put("count", 20);

        JSONObject options = new JSONObject();
        options.put("sort", sort);
        options.put("range", range);

        JSONObject geocodes = new JSONObject();
        geocodes.put("latitude", latitude);
        geocodes.put("longitude", longitude);

        Log.v("location", String.valueOf(latitude));
        Log.v("location", String.valueOf(longitude));

        JSONObject location = new JSONObject();
        location.put("geocodes", geocodes);

        JSONObject requestData = new JSONObject();
        requestData.put("location", location);
        requestData.put("options", options);

        return requestData;
    }
}
