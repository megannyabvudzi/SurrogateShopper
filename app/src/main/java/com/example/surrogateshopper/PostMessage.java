package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostMessage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    String email;
    String type;
    String reqno="";
    EditText req;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
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

        req = (EditText)findViewById(R.id.ReqNo);
        reqno = getIntent().getStringExtra("reqno");
        if(reqno==null){
            reqno = "Enter the Request Number.";
        }
        req.setText(reqno);
    }

    public void SendMessage(View v){
       // reqno = getIntent().getStringExtra("reqno");
        //req = (EditText)findViewById(R.id.ReqNo);
        //req.setText(reqno);
        final EditText e = findViewById(R.id.msg);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setText("");
            }
        });
        System.out.println(reqno);
        String message = e.getText().toString();
        String reqs = req.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody rb = new FormBody.Builder().add("message",message).add("reqno",reqs).build();
        String url = "https://lamp.ms.wits.ac.za/home/s1827555/PostMessage.php";
        Request request = new Request.Builder().url(url).post(rb).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
               final String resp = response.body().string();
                System.out.println(resp);
                PostMessage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resp.trim().equalsIgnoreCase("True")){
                            Toast.makeText(getApplicationContext(),"Message sent.",Toast.LENGTH_SHORT).show();
                            e.setText("");
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed to send Message.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
