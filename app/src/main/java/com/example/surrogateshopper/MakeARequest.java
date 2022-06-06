package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.IOException;
import java.util.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MakeARequest extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    OkHttpClient client = new OkHttpClient();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
   ArrayList<String>items = new ArrayList<>();
   ArrayList<String>quantities = new ArrayList<>();
    String email;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_request);

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
    }

    public void addItem(View v){
        EditText product= (EditText)findViewById(R.id.product);
        EditText quantity =(EditText)findViewById(R.id.quantity);
        //PHP_ITEMS p = new PHP_ITEMS();
        String prod = product.getText().toString();
        String qty = quantity.getText().toString();
        items.add(prod);
        quantities.add(qty);
       // String it+=
       // p.doRequest(prod,qty,id);
        product.setText("");
        quantity.setText("");
    }

    public void completeOrder(View v){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/req.php").newBuilder();
        urlBuilder.addQueryParameter("email",email);
        String url= urlBuilder.toString();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if(!response.isSuccessful()) {
                    MakeARequest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText product = (EditText) findViewById(R.id.product);
                            product.setText("Fuck");
                        }
                    });
                }
                else{
                    final String ans = response.body().string();
                    AddItems(ans);
                    MakeARequest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView r = (TextView)findViewById(R.id.RequestNo);
                            r.setVisibility(View.VISIBLE);
                            r.setText("Your request number is: "+ans+".");
                        }
                    });
                }

            }
        });

        if(items.isEmpty()){
            Toast.makeText(this,"Please enter a product",Toast.LENGTH_SHORT).show();
        }
        else{
                Toast.makeText(this, "Your Request has been created.", Toast.LENGTH_SHORT).show();
        }
        }

        public void AddItems(String id){
            String AllItems ="";
            String AllQty ="";
            for(int i=0;i<items.size();i++){
                AllItems+= items.get(i)+",";
                AllQty+= quantities.get(i)+",";
            }
            System.out.println(AllItems);
            System.out.println(AllQty);
            OkHttpClient client1 = new OkHttpClient();
            HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/items.php").newBuilder();
            urlBuilder1.addQueryParameter("product",AllItems);
            urlBuilder1.addQueryParameter("quantity",AllQty);
            urlBuilder1.addQueryParameter("id",id);
            final String url1 = urlBuilder1.toString();
            Request request1 = new Request.Builder().url(url1).build();
            client1.newCall(request1).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    System.out.println("Didn't execute items");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response1) throws IOException {
                    System.out.println(url1);
                    String out = response1.body().string();
                    System.out.println(out);
                }
            });
            items.clear();
            quantities.clear();

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
