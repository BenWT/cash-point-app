package bentownshend.assignment;

import android.os.AsyncTask;
import android.util.Base64;

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

/**
 * Created by bentownshend on 01/12/2017.
 */

public class AsyncGetData extends AsyncTask<String, Void, String> {

    private KeyStore keyStore;
    private InputStream in;

    @Override
    protected String doInBackground(String... params) {
        String result = null;

        return result;
    }
    @Override
    protected void onPostExecute(String result) {
        //Enter code for parsing and create list view
    }
}
