package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {


    EditText username, password;
    String Username, Password;
    String type;
    String fullAddress;
    String url;
    int x;
    Button regbutton;
    private TextView AddressText;
    private Button LocationButton;
    private LocationRequest locationRequest;
    final String registerURL = "https://lamp.ms.wits.ac.za/~s2430972/registerusers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);
        regbutton = (Button) findViewById(R.id.buttonReg);

        AddressText = findViewById(R.id.addressText);
        LocationButton = findViewById(R.id.locationButton);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name= username.getText().toString();
                String Password= password.getText().toString();

                new registerUser().execute(Name, Password);
                getCurrentLocation();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(Register.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(Register.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Register.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Register.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }


    public class registerUser extends AsyncTask<String,Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String Name= strings[0];
            String Password=strings[1];
            String finalurl= registerURL +"?user_name="+Name+
                    "&user_password="+Password;

            OkHttpClient okHttpClient=new OkHttpClient();
            Request request = new Request.Builder()
                    .url(finalurl)
                    .get()
                    .build();


            //checking server response and inserting data

            Response response= null;

            try {
                response= okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    String result= response.body().string();
                    showToast(result);

                    if(result.equalsIgnoreCase("User registered successfully")){
                        showToast("Successful Registration.Please Login");
                        Intent i= new Intent(Register.this, HomePage.class);
                        startActivity(i);
                        finish();
                    }

                    else if (result.equalsIgnoreCase("User already exists")){
                        showToast("User Already Exist");

                    }
                    else{
                        showToast("Registration Failed. Try Again");
                    }
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }

    public void showToast(final String Text){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Register.this, Text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void confirm(View v){
        CheckBox vol =(CheckBox)findViewById(R.id.Volunteer);
        CheckBox atrisk =(CheckBox)findViewById(R.id.AtRisk);

        if(vol.isChecked() && atrisk.isChecked()){
            Toast.makeText(this, "Only choose one",Toast.LENGTH_LONG).show();

        }
        else if(vol.isChecked()==false && atrisk.isChecked()==false){
            Toast.makeText(this, "Select user type",Toast.LENGTH_LONG).show();
        }
        else if(vol.isChecked()){
            x = 1;
            type ="Volunteer";
            url ="https://lamp.ms.wits.ac.za/~s2430972/volunteers.php";
            OkHttpClient client = new OkHttpClient();
            RequestBody Formbody = new FormBody.Builder()
                    .add("username", Username)
                    .add("type", type)
                    .add("address", fullAddress)
                    .build();

            Request request = new Request.Builder().url(url).post(Formbody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String result = response.body().string();
                    System.out.println(result);
                    if (result.trim().equalsIgnoreCase("Updated")) {
                        Register.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Register.this, HomePage.class);
                                intent.putExtra("username", Username);
                                intent.putExtra("type",type);
                                startActivity(intent);
                            }
                        });
                    } else if (result.trim().equalsIgnoreCase("Failed")) {
                        System.out.println("Cannot insert");
                    }
                }
            });
        }
        else{
            x =0;
            type = "At Risk";
            url ="https://lamp.ms.wits.ac.za/~s2430972/atrisk.php";
            OkHttpClient client = new OkHttpClient();
            RequestBody Formbody = new FormBody.Builder()
                    .add("username", Username)
                    .add("type", type)
                    .add("address", fullAddress)
                    .build();

            Request request = new Request.Builder().url(url).post(Formbody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String result = response.body().string();
                    System.out.println(result);
                    if (result.trim().equalsIgnoreCase("Updated")) {
                        Register.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Register.this, HomePage.class);
                                intent.putExtra("username", Username);
                                intent.putExtra("type",type);
                                startActivity(intent);
                            }
                        });
                    } else if (result.trim().equalsIgnoreCase("Failed")) {
                        System.out.println("insert failed");
                    }
                }
            });
        }
    }




}
