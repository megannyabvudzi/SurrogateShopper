package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AllMessages extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    String email;
    String type;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        email = getIntent().getExtras().getString("email");
        type = getIntent().getExtras().getString("type");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        Toast.makeText(getApplicationContext(),"Swipe right from the left of the screen to access Navigation Bar.",Toast.LENGTH_LONG).show();
        if(type.trim().equalsIgnoreCase("Volunteer")){
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.MakeARequest).setVisible(false);
            nav_menu.findItem(R.id.NewMessage).setVisible(false);
            nav_menu.findItem(R.id.AllYourRequests).setVisible(false);

        }
        else if(type.trim().equalsIgnoreCase("At Risk")){
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.completedRequests).setVisible(false);
            nav_menu.findItem(R.id.AllMessages).setVisible(false);
            nav_menu.findItem(R.id.availableRequests).setVisible(false);
        }
        linearLayout =(LinearLayout)findViewById(R.id.msgs);
        getMessages(email);
    }

    public void getMessages(String email){
        OkHttpClient client = new OkHttpClient();
        RequestBody RB = new FormBody.Builder().add("email",email).build();
        String url = "https://lamp.ms.wits.ac.za/home/s1827555/getMessages.php";
        Request request= new Request.Builder().url(url).post(RB).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String msg = response.body().string();
                System.out.println(msg);
                try {
                    ProcessMsg(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ProcessMsg (String JSON)throws JSONException {
        JSONArray Ja = new JSONArray(JSON);

        for(int i=0;i<Ja.length();i++){
            final TextView p = new TextView(getApplicationContext());
            JSONObject Jo = Ja.getJSONObject(i);
            p.append("Message: \n");
            p.append(Jo.getString("MESSAGE"));
            p.setTextColor(Color.WHITE);
            AllMessages.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearLayout.addView(p);
                }
            });
        }


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId()==R.id.home){
            Intent i = new Intent(getApplicationContext(),HomePage.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.profile){
            Intent i = new Intent(getApplicationContext(),Profile.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.MakeARequest){
            Intent i = new Intent(getApplicationContext(),MakeARequest.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.AllYourRequests){
            Intent i = new Intent(getApplicationContext(),AllRequests.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.completedRequests){
            Intent i = new Intent(getApplicationContext(),CompletedRequests.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.availableRequests){
            Intent i = new Intent(getApplicationContext(),AvailableRequests.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.NewMessage){
            Intent i = new Intent(getApplicationContext(),PostMessage.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.AllMessages){
            Intent i = new Intent(getApplicationContext(),AllMessages.class);
            i.putExtra("email",email);
            i.putExtra("type",type);
            startActivity(i);
        }
        return true;
    }
}
