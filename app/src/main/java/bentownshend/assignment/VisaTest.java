package bentownshend.assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class VisaTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa_test);
    }

    protected void doDataTest(View view) {
        new VisaConnection().execute("");
    }

    // Reference: http://chariotsolutions.com/blog/post/https-with-client-certificates-on/
    // Reference: https://stackoverflow.com/questions/1968416/how-to-do-http-authentication-in-android
    private class VisaConnection extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
//            String url = "https://sandbox.api.visa.com/vdp/helloworld";
            String url = "https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry";

            String clientUser = "E16N805EFQ6CW3ZRFK9O216wu_66iY8r8SvsD6c-UyrGNnR9A";
            String clientPass = "O0diQOzmgRzzx675Q3PnSMeYBf4QfP4FBvsI9";

            String basicAuth = "Basic " + Base64.encodeToString(
                    (clientUser + ":" + clientPass).getBytes(), Base64.NO_WRAP);


            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                InputStream in1 = getResources().openRawResource(R.raw.keycertbundle);
                keyStore.load(in1, clientPass.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                kmf.init(keyStore, clientPass.toCharArray());
                KeyManager[] keyManagers = kmf.getKeyManagers();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagers, null, null);

                HttpURLConnection urlConnection = null;
                try {
                    URL requestedUrl = new URL(url);
                    urlConnection = (HttpURLConnection) requestedUrl.openConnection();
                    if (urlConnection instanceof HttpsURLConnection) {
                        ((HttpsURLConnection) urlConnection)
                                .setSSLSocketFactory(sslContext.getSocketFactory());
                    }

                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
//                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("Authorization", basicAuth);







                    // TODO: Covert geocodes to object
//                    JSONObject bodyObject = new JSONObject();
//                    JSONObject wsRequestHeaderV2Object = new JSONObject();
//                    JSONObject requestDataObject = new JSONObject();
//                    JSONObject locationObject = new JSONObject();
//                    JSONObject optionsObject = new JSONObject();
//                    JSONObject rangeObject = new JSONObject();
//                    JSONObject sortObject = new JSONObject();
//
//                    rangeObject.put("start", 10);
//                    rangeObject.put("end", 20);
//
//                    sortObject.put("primary", "city");
//                    sortObject.put("direction", "asc");
//
//                    locationObject.put("address", JSONObject.NULL);
//                    locationObject.put("placeName", "700 Arch St, Pittsburg, PA 15212");
//                    locationObject.put("geocodes", JSONObject.NULL);
//
//                    optionsObject.put("range", rangeObject);
//                    optionsObject.put("sort", sortObject);
//                    optionsObject.put("useFirstAmbiguous", true);
//
//                    requestDataObject.put("culture", "en-US");
//                    requestDataObject.put("distance", "20");
//                    requestDataObject.put("distanceUnit", "mi");
//                    requestDataObject.put("location", locationObject);
//                    requestDataObject.put("options", optionsObject);
//
//                    wsRequestHeaderV2Object.put("applicationID", "VATMLOC");
//
//                    bodyObject.put("wsRequestHeaderV2", wsRequestHeaderV2Object);
//                    bodyObject.put("requestData", requestDataObject);

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        result = readResponse(urlConnection.getInputStream());

                    } else {
                        result = String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage();
                    }
                } catch(Exception ex) {
                    // TODO
                    result = ex.toString();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException |
                    UnrecoverableKeyException | IOException | KeyManagementException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) findViewById(R.id.textView);
//
//            String text = null;
//
//            if (result != null) {
//                try {
//                    text = result.getString("message");
//                } catch (JSONException e) {
//                    text = result.toString();
//                }
//            } else {
//                text = "No Result.";
//            }
//
//            txt.setText(text);

            txt.setText(result);
        }

        private String readResponse(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = reader.readLine()) != null)
            {
                response.append(inputLine);
            }
            reader.close();

            return response.toString();

        }
    }
}