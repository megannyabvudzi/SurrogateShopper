package com.example.surrogateshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId()==R.id.home){
            Intent i = new Intent(getApplicationContext(),HomePage.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.profile){
            Intent i = new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.MakeARequest){
            Intent i = new Intent(getApplicationContext(),MakeARequest.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.AllYourRequests){
            Intent i = new Intent(getApplicationContext(),AllRequests.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.completedRequests){
            Intent i = new Intent(getApplicationContext(),CompletedRequests.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.availableRequests){
            Intent i = new Intent(getApplicationContext(),AllRequests.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.NewMessage){
            Intent i = new Intent(getApplicationContext(),PostMessage.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.AllMessages){
            Intent i = new Intent(getApplicationContext(),AllMessages.class);
            startActivity(i);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
