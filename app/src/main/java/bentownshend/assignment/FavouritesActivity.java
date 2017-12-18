package bentownshend.assignment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        File directory = getFilesDir();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("atm-")) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        List<String> lines = new ArrayList<>();
                        String line;

                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                        }
                        br.close();

                        favourites.add(new FavouriteInfo(lines));

                        FavouriteAdapter adapter = new FavouriteAdapter(this, favourites);
                        ListView list = (ListView) findViewById(R.id.favouritesList);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tvLatitude = (TextView) view.findViewById(R.id.tvLatitude);
                                TextView tvLongitude = (TextView) view.findViewById(R.id.tvLongitude);
                                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                                TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
                                TextView tvWheelchair = (TextView) view.findViewById(R.id.tvWheelchair);
                                TextView tvBalance = (TextView) view.findViewById(R.id.tvBalance);
                                TextView tvPin = (TextView) view.findViewById(R.id.tvPin);

                                Intent info = new Intent(FavouritesActivity.this, InformationActivity.class);
                                info.putExtra("latitude", Double.parseDouble((String) tvLatitude.getText()));
                                info.putExtra("longitude", Double.parseDouble((String) tvLongitude.getText()));
                                info.putExtra("name", tvName.getText());
                                info.putExtra("address", tvAddress.getText());
                                info.putExtra("wheelchair", tvWheelchair.getText());
                                info.putExtra("balance", tvBalance.getText());
                                info.putExtra("pin", tvPin.getText());
                                startActivity(info);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (favourites.size() <= 0) Toast.makeText(FavouritesActivity.this, "Could not find any favourites!", Toast.LENGTH_SHORT).show();
        }
    }

    class FavouriteInfo {
        String latitude, longitude, name, address, wheelchair, balance, pin;

        FavouriteInfo(List<String> lines) {
            if (lines.size() == 7) {
                this.latitude = lines.get(0);
                this.longitude = lines.get(1);
                this.name = lines.get(2);
                this.address = lines.get(3);
                this.wheelchair = lines.get(4);
                this.balance = lines.get(5);
                this.pin = lines.get(6);
            }
        }
    }

    private class FavouriteAdapter extends ArrayAdapter<FavouriteInfo> {
        FavouriteAdapter(Context context, List<FavouriteInfo> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            FavouriteInfo f = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favourite, parent, false);
            }

            TextView tvLatitude = (TextView) convertView.findViewById(R.id.tvLatitude);
            TextView tvLongitude = (TextView) convertView.findViewById(R.id.tvLongitude);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            TextView tvWheelchair = (TextView) convertView.findViewById(R.id.tvWheelchair);
            TextView tvBalance = (TextView) convertView.findViewById(R.id.tvBalance);
            TextView tvPin = (TextView) convertView.findViewById(R.id.tvPin);

            if (f != null) {
                tvLatitude.setText(f.latitude);
                tvLongitude.setText(f.longitude);
                tvName.setText(f.name);
                tvAddress.setText(f.address);
                tvWheelchair.setText(f.wheelchair);
                tvBalance.setText(f.balance);
                tvPin.setText(f.pin);
            }

            return convertView;
        }
    }
}
