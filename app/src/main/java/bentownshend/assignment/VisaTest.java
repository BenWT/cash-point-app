package bentownshend.assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        new VisaConnection().execute("");
    }

    // TODO: Covert output to JsonObject
    private class VisaConnection extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject result = null;
            String url = "https://sandbox.api.visa.com/vdp/helloworld";
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
                    // TODO: Change to post
                    // TODO: set parameters
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Authorization", basicAuth);
                    urlConnection.setConnectTimeout(1500);
                    urlConnection.setReadTimeout(1500);

                    if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        InputStream in = urlConnection.getInputStream();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                in, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        in.close();
                        result = new JSONObject(sb.toString());
                    }
                } catch(Exception ex) {
                    // TODO
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
        protected void onPostExecute(JSONObject result) {
            TextView txt = (TextView) findViewById(R.id.textView);
            txt.setText(result.toString());
        }
    }
}