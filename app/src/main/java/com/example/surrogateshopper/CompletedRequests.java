package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompletedRequests extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    LinearLayout l ;
    String email;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_requests);
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
        l = (LinearLayout)findViewById(R.id.comp_req);
        Completed(email);
    }

    public void Completed(String email){
        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s1827555/CompReqNo.php";
        RequestBody formBody = new FormBody.Builder().add("email",email).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String ans = response.body().string();
                System.out.println(ans);
                try {
                    Process(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void Process(String Json) throws JSONException {
        JSONArray jsonArray = new JSONArray(Json);
        for(int i=0;i<jsonArray.length();i++){
           final JSONObject jo = jsonArray.getJSONObject(i);
            final TextView m = new TextView(this);
            m.setId(i+5);
            String reqno = jo.getString("REQUEST_NO");
            String user = jo.getString("REQUEST_USER");
            getUser(user,i+5);
            m.append("Request Number: "+reqno+"\n");
            m.append("Request Date: "+jo.getString("REQUEST_DATE")+"\n");
            getItems(reqno,i+5);
            final int finalI = i;
            CompletedRequests.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    l.addView(m);
                    if(finalI %2==0){
                        m.setBackgroundColor(Color.DKGRAY);
                    }
                    else{
                        m.setBackgroundColor(Color.BLUE);
                    }
                }
            });


        }
    }

    public void getUser(String UserNum, final int id){
        OkHttpClient client1 = new OkHttpClient();
        HttpUrl.Builder UB = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/getRequester.php").newBuilder();
        UB.addQueryParameter("userno",UserNum);
        String url = UB.build().toString();
        Request request1 = new Request.Builder().url(url).build();

        client1.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response1) throws IOException {
                String details = response1.body().string();
                System.out.println(details);
                try {
                    processUser(details,id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getItems(String reqNo, final int id){
        OkHttpClient client1 = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/getItems.php").newBuilder();
        urlBuilder.addQueryParameter("RequestNumber",reqNo);
        String url1 = urlBuilder.build().toString();
        System.out.println(url1);
        Request request1 = new Request.Builder().url(url1).build();

        client1.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response1) throws IOException {
                String items = response1.body().string();
                System.out.println(items);
                try{
                    ProcessItems(items,id);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });


    }

    public void ProcessItems(String Output,int id) throws JSONException {
        JSONArray JA = new JSONArray(Output);
        final TextView p = (TextView)findViewById(id);
        for(int j=0;j<JA.length();j++){
            final JSONObject JO = JA.getJSONObject(j);
            CompletedRequests.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.append("Item Description: "+JO.getString("ITEM_DESC")+" Quantity: "+JO.getString("ITEM_QTY")+"\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void processUser(String json, int ID) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        final TextView k = (TextView)findViewById(ID);
        final JSONObject jsonObject = jsonArray.getJSONObject(0);
        CompletedRequests.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    k.append("First Name: "+jsonObject.getString("USER_FIRSTNAME")+"\n");
                    k.append("Last Name: "+jsonObject.getString("USER_LASTNAME")+"\n");
                    k.append("Cell Number: "+"0"+jsonObject.getString("USER_PHONE")+"\n");
                    k.append("Address: "+jsonObject.getString("USER_ADDRESS")+"\n");
                    //k.append("Items Required:\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
