package bentownshend.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class InformationActivity extends AppCompatActivity {

    JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Bundle extras = getIntent().getExtras();
        String responseString = extras.getString("info");

        try {
            response = new JSONObject(responseString).getJSONObject("location");
//            Log.v("atm", response.toString());

            DisplayData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void DisplayData() throws JSONException {
        TextView atmName = (TextView)findViewById(R.id.atmName);
        TextView atmAddress = (TextView)findViewById(R.id.atmAddress);

        String name = response.getString("ownerBusName");
        String address = response.getJSONObject("address").getString("formattedAddress");

        atmName.setText(name);
        atmAddress.setText(address);

        Log.v("info", name + " " + address);
    }
}
