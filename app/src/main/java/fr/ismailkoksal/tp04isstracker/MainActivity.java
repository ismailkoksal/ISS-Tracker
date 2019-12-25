package fr.ismailkoksal.tp04isstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import fr.ismailkoksal.tp04isstracker.model.IssNow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final String ISS_NOW_URL = "http://api.open-notify.org/iss-now.json";
    final OkHttpClient client = new OkHttpClient();
    final Gson gson = new Gson();
    MapView mapView;
    IMapController mapController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapController = mapView.getController();
        mapController.setZoom(5.0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getISSNow();
            }
        }, 0, 5000);
    }

    private void getISSNow() {
        Request request = new Request.Builder()
                .url(ISS_NOW_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("DATA", "Main : onFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                final IssNow issNow = gson.fromJson(response.body().charStream(), IssNow.class);

                final double latitude = Double.parseDouble(issNow.getIssPosition().getLatitude());
                final double longitude = Double.parseDouble(issNow.getIssPosition().getLongitude());

                affichage(latitude, longitude);
            }
        });
    }

    private void affichage(final double latitude, final double longitude) {
        mapView.post(new Runnable() {
            @Override
            public void run() {
                GeoPoint issLocation = new GeoPoint(latitude, longitude);
                mapController.setCenter(issLocation);
                Marker marker = new Marker(mapView);
                marker.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_space_station));
                marker.setTitle("Latitude : " + latitude + "\nLongitude : " + longitude);
                marker.setPosition(issLocation);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.showInfoWindow();
                mapView.getOverlays().clear();
                mapView.getOverlays().add(marker);
            }
        });
    }
}
