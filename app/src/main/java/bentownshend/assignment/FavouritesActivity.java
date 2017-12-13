package bentownshend.assignment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    List<FavouriteInfo> favourites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // TODO Load favourites
        // TODO add favourites to list view
        // TODO on click, load info activity

        File directory = getFilesDir();
        File[] files = directory.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("test", String.valueOf(files.length));
                if (files[i].getName().startsWith("atm-")) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(files[i]));
                        List<String> lines = new ArrayList<>();
                        String line;

                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                            Log.d("test", line);
                        }
                        br.close();

                        favourites.add(new FavouriteInfo(lines));

                        FavouriteAdapter adapter = new FavouriteAdapter(this, favourites);
                        ListView list = (ListView)findViewById(R.id.favouritesList);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tvLatitude = (TextView) view.findViewById(R.id.tvLatitude);
                                TextView tvLongitude = (TextView) view.findViewById(R.id.tvLongitude);
                                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                                TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);

                                Intent info = new Intent(FavouritesActivity.this, InformationActivity.class);
                                info.putExtra("latitude", Double.parseDouble((String) tvLatitude.getText()));
                                info.putExtra("longitude", Double.parseDouble((String) tvLongitude.getText()));
                                info.putExtra("name", tvName.getText());
                                info.putExtra("address", tvAddress.getText());
                                startActivity(info);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class FavouriteInfo {
        String latitude;
        String longitude;
        String name;
        String address;

        FavouriteInfo(List<String> lines) {
            Log.d("test", String.valueOf(lines.size()));

            if (lines.size() == 4) {
                this.latitude = lines.get(0);
                this.longitude = lines.get(1);
                this.name = lines.get(2);
                this.address = lines.get(3);
            }
        }
    }

    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    class FavouriteAdapter extends ArrayAdapter<FavouriteInfo> {
        public FavouriteAdapter(Context context, List<FavouriteInfo> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FavouriteInfo f = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favourite, parent, false);
            }

            TextView tvLatitude = (TextView) convertView.findViewById(R.id.tvLatitude);
            TextView tvLongitude = (TextView) convertView.findViewById(R.id.tvLongitude);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);

            tvLatitude.setText(f.latitude);
            tvLongitude.setText(f.longitude);
            tvName.setText(f.name);
            tvAddress.setText(f.address);

            return convertView;
        }
    }
}
