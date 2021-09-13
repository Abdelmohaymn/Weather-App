package com.example.weather;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.Adapter.DaysAdapter;
import com.example.weather.Adapter.HoursAdapter;
import com.example.weather.model.ForcastObj;
import com.example.weather.model.HourObj;
import com.example.weather.model.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {
    TextView textViewDate, textViewTime, textViewCity, textViewTemp, textViewState, textViewMinMax, textFailed;
    ImageView imageViewState, imageViewSettings, current_location;
    RecyclerView recyclerViewHours, recyclerViewDays;
    WeatherApi weatherApi;
    HoursAdapter hoursAdapter;
    DaysAdapter daysAdapter;
    RelativeLayout layoutProgress;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    FusedLocationProviderClient fusedLocationProviderClient;
    double lat,lon;
    Location location;
    Location location1;
    String currentCity;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTemp = findViewById(R.id.textViewTemperature);
        textViewState = findViewById(R.id.textViewState);
        textViewMinMax = findViewById(R.id.textViewMinMax);
        imageViewState = findViewById(R.id.imageViewState);
        recyclerViewHours = findViewById(R.id.recyclerViewHours);
        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        imageViewSettings = findViewById(R.id.settings);
        progressBar = findViewById(R.id.progressBar);
        layoutProgress = findViewById(R.id.layoutProgress);
        textFailed = findViewById(R.id.textFailed);
        current_location = findViewById(R.id.current_location);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHours.setHasFixedSize(true);
        recyclerViewHours.setLayoutManager(layoutManager);

        /////////////////////////////////////////////////////

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewDays.setHasFixedSize(true);
        recyclerViewDays.setLayoutManager(layoutManager2);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sharedPreferences.getString("lang", "en");
        boolean dark = sharedPreferences.getBoolean("dark", false);


        setDefaultLanguage(this, lang);

        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Handler mHandler = new Handler();

        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                setDefaultLanguage(MainActivity.this, lang);
                textViewTime.setText(timeFormater(System.currentTimeMillis()));
                textViewDate.setText(dateFormater(System.currentTimeMillis()));
                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler.postDelayed(mRunnable,1000);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        ////////////////////////////


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);

        if (networkInfo == null || !networkInfo.isConnected()) {
            progressBar.setVisibility(View.GONE);
            textFailed.setText("No internet connection");
        } else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this
                    , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                String city = sharedPreferences.getString("city", "Cairo");
                if (city.equals(currentCity)){
                    getLocation();
                }else {
                    getWeather(city);
                }

            }else {
                String city = sharedPreferences.getString("city", "Cairo");
                getWeather(city);
            }
        }

        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    ,Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                }

            }
        });


    }

    private void getWeather(String area) {
        String lang = sharedPreferences.getString("lang", "en");
        //

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", area);
        parameters.put("days", "7");
        parameters.put("lang", lang);
        parameters.put("key", "a62e2f24c1974d36853214722210709");



        Call<Response> call = weatherApi.getData(parameters);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    textFailed.setText("Code: " + response.code());
                    return;
                }
                layoutProgress.setVisibility(View.GONE);

                Response weatherData = response.body();

                String minMax = temp(weatherData.getForecast().getForecastday().get(0).getDay().getMintemp_c())
                        + "/" + temp(weatherData.getForecast().getForecastday().get(0).getDay().getMaxtemp_c())
                        + "\u00B0";

                //textViewTime.setText(timeFormater(System.currentTimeMillis()));
                //textViewDate.setText(dateFormater(System.currentTimeMillis()));
                textViewCity.setText(weatherData.getLocation().getName() + "," + weatherData.getLocation().getCountry());
                textViewTemp.setText(temp(weatherData.getCurrent().getTemp_c()) + "\u00B0");
                textViewState.setText(weatherData.getCurrent().getCondition().getText());
                textViewMinMax.setText(minMax);
                Picasso.get().load("https:" + weatherData.getCurrent().getCondition().getIcon()).into(imageViewState);

                sharedPreferences.edit().putString("city",weatherData.getLocation().getName()).apply();
                currentCity =  weatherData.getLocation().getName();

                //int size = weatherData.getForecast().getForecastday().size();

                List<HourObj> hours = weatherData.getForecast().getForecastday().get(0).getHour();
                hoursAdapter = new HoursAdapter(hours);
                recyclerViewHours.setAdapter(hoursAdapter);

                List<ForcastObj> days = weatherData.getForecast().getForecastday();
                days.remove(0);
                daysAdapter = new DaysAdapter(days);
                recyclerViewDays.setAdapter(daysAdapter);

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                textFailed.setText("Code: " + t.getMessage());
            }
        });


    }

    private String dateFormater(long time) {
        Date date = new java.util.Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("E,d MMM");
        //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    private String timeFormater(long time) {
        Date date = new java.util.Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a");
        //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedTime = sdf.format(date);
        return formattedTime;
    }

    private String temp(String t) {
        if (t.contains(".")) {
            String out = "";
            char[] arr = t.toCharArray();
            for (char c : arr) {
                if (c != '.') {
                    out += c;
                } else {
                    return out;
                }
            }
        }
        return t;
    }

    public static void setDefaultLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                     location = task.getResult();
                    if (location != null){
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        getWeather(lat + "," + lon);

                    }else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                location1 = locationResult.getLastLocation();

                                lat = location1.getLatitude();
                                lon = location1.getLongitude();
                                getWeather(lat + "," + lon);
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

                    }
                }
            });

        }else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && grantResults.length > 0 && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {
            getLocation();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}