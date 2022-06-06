package com.example.surrogateshopper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    private FusedLocationProviderClient client;
    double longitude;
    double latitude;
    Geocoder geocoder;
    List<Address> addresses ;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    String fullAddress;
    String username;
    int x;
    String type;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        username = getIntent().getExtras().getString("username");

        CheckBox v =(CheckBox)findViewById(R.id.Volunteer);
        CheckBox ar =(CheckBox)findViewById(R.id.AtRisk);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });
        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });
    }

    public void doRegister(View v) {
        EditText Username = (EditText)findViewById(R.id.Username);
        EditText Password = (EditText)findViewById(R.id.Password);

        final String username = Username.getText().toString();
        String password = Password.getText().toString();

        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/~s2430972/users.php";

        RequestBody formBody = new FormBody.Builder()
                .add("Username",username)
                .add("Password",password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final TextView reg = (TextView)findViewById(R.id.isRegistered);
                    final String resp = response.body().string();
                    if(resp.trim().equalsIgnoreCase("False")){
                        Register.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reg.setVisibility(View.VISIBLE);
                                reg.setText("Existing Account");
                            }
                        });
                    }
                    else if(resp.trim().equalsIgnoreCase("True")){
                        Register.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(Register.this, Register.class);
                                i.putExtra("username",username);
                                startActivity(i);
                            }
                        });

                    }
                }
            }
        });
    }

    public void GetLocation(){
        TextView add = (TextView)findViewById(R.id.Location);
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            System.out.println(latitude);
            System.out.println(longitude);
            addresses =  geocoder.getFromLocation(latitude, longitude,1);
            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();

            fullAddress = address + ", " + area + ", " + city;
            add.setText("");
            add.setText(fullAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                Register.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                Register.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = lat;
                longitude = longi;
                GetLocation();
            } else {
                Toast.makeText(this, "Can't fine you :(", Toast.LENGTH_SHORT).show();
            }
        }
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
                    .add("username", username)
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
                                intent.putExtra("username", username);
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
                    .add("email", username)
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
                                intent.putExtra("email", username);
                                intent.putExtra("type",type);
                                startActivity(intent);
                            }
                        });
                    } else if (result.trim().equalsIgnoreCase("Failed")) {
                        System.out.println("Failed to insert");
                    }
                }
            });
        }
    }

}

