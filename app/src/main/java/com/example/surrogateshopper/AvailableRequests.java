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
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class AvailableRequests extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    String email;
    String type;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_requests);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        email = getIntent().getStringExtra("email");
        type = getIntent().getExtras().getString("type");
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
        layout = (LinearLayout)findViewById(R.id.reqContent);
        AllAvailable();
    }

    public void AllAvailable(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s1827555/getReqNo.php";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                System.out.println(data);
                try{
                    process(data);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        final EditText e = new EditText(this);
        e.setText("Input the Request Number of the request you would like to complete.");
        e.setGravity(Gravity.CENTER_HORIZONTAL);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setText("");
            }
        });
        layout.addView(e);
        Button b = new Button(this);
        b.setText("Complete Request");
        b.setTextColor(Color.BLACK);
        layout.addView(b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String ReqNo =  e.getText().toString();
                CompleteUser CU = new CompleteUser();
                CU.Insert(ReqNo,email);
                Toast.makeText(getApplicationContext(),"You can view the request in Completed Requests.",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void process(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);

        for(int i=0;i<ja.length();i++){
            final TextView m = new TextView(this);
            JSONObject jo = ja.getJSONObject(i);
            String Num = jo.getString("REQUEST_NO");
            String userno =jo.getString("REQUEST_USER");
            m.setId(i+20);
            getRequestUser(userno,i+20);
            m.append("Request Number: "+jo.getString("REQUEST_NO")+"\n");
            m.append("Request Date: "+jo.getString("REQUEST_DATE")+"\n");
           // m.append("Request User: "+jo.getString("REQUEST_USER")+"\n");
            //m.setId(i+20);
            //m.append("Items Required:\n");
            getItems(Num,i+20);
            final int finalI = i;
            AvailableRequests.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout.addView(m);
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

    public void getRequestUser(String UserNumber,final int ID){
        OkHttpClient client2=  new OkHttpClient();
        HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/getRequester.php").newBuilder();
        urlBuilder1.addQueryParameter("userno",UserNumber);
        String url2 = urlBuilder1.build().toString();
        Request request2 = new Request.Builder().url(url2).build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response2) throws IOException {
                String allData= response2.body().string();
                System.out.println(allData);
                try {
                    processUser(allData,ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void processUser(String json, int ID) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
       final TextView k = (TextView)findViewById(ID);
        final JSONObject jsonObject = jsonArray.getJSONObject(0);
        AvailableRequests.this.runOnUiThread(new Runnable() {
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
            AvailableRequests.this.runOnUiThread(new Runnable() {
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
