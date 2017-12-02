package bentownshend.assignment;

import android.accounts.AuthenticatorException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class VisaTest2 extends AppCompatActivity {

    TextView tv;

    String url = "https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry";
    String clientUser = "E16N805EFQ6CW3ZRFK9O216wu_66iY8r8SvsD6c-UyrGNnR9A";
    String clientPass = "O0diQOzmgRzzx675Q3PnSMeYBf4QfP4FBvsI9";

    String basicAuth = "Basic " + Base64.encodeToString(
            (clientUser + ":" + clientPass).getBytes(), Base64.NO_WRAP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa_test2);
        tv = (TextView) findViewById(R.id.textView2);
        try {
            fetchNetworkData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void fetchNetworkData() throws JSONException {
        JSONObject sort = new JSONObject();
        sort.put("primary", "distance");
        sort.put("direction", "asc");

        JSONObject options = new JSONObject();
        options.put("sort", sort);

        JSONObject geocodes = new JSONObject();
        geocodes.put("latitude", 51.511732);
        geocodes.put("longitude", -0.123270);


        JSONObject location = new JSONObject();
        location.put("geocodes", geocodes);

        JSONObject requestData = new JSONObject();
        requestData.put("location", location);
        requestData.put("options", options);

        JSONObject context = new JSONObject();
        context.put("requestData", requestData);

        JsonObjectRequest visaRequest = new JsonObjectRequest(Request.Method.POST, url, context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tv.setText("Response: " + response.toString());
//                Log.v("response", response.toString());

                try {
                    JSONArray responseData = response.getJSONArray("responseData")
                        .getJSONObject(0).getJSONArray("foundATMLocations");

                    for (int i = 0; i < responseData.length(); i++) {
                        Log.v("atm", responseData.getJSONObject(i).toString());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                tv.setText("Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
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
                // httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };
}




