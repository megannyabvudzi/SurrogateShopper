package com.example.surrogateshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText password = (EditText) findViewById(R.id.Password);

        final EditText login = (EditText) findViewById(R.id.Username);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("");
            }
        });
    }

    public void doLogin(View v)  {
        final EditText USERNAME = (EditText)findViewById(R.id.Username);
        EditText PASSWORD = (EditText)findViewById(R.id.Password);
        String Password = PASSWORD.getText().toString();
        final String Username = USERNAME.getText().toString();

        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/~s2430972/check_users.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username",Username)
                .add("password",Password)
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
                    final TextView resp =(TextView)findViewById(R.id.LoginR) ;
                    final String LoginSuccess = response.body().string();
                    final String[] t = LoginSuccess.split(",");
                    System.out.println(LoginSuccess);
                    if(LoginSuccess.trim().equalsIgnoreCase("False")){
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resp.setVisibility(View.VISIBLE);
                                resp.setText("Incorrect Username");
                            }
                        });
                    }
                    else if(LoginSuccess.trim().equalsIgnoreCase("False")){
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resp.setVisibility(View.VISIBLE);
                                resp.setText("Incorrect Password");
                            }
                        });
                    }
                    else if(LoginSuccess.trim().equalsIgnoreCase("False")){
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resp.setVisibility(View.VISIBLE);
                                resp.setText("Not registered");
                            }
                        });
                    }
                    else if(t[0].trim().equalsIgnoreCase("True")){
                        final String type = t[1];
                        System.out.println(type + " " + t[0]);
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent k = new Intent(getApplicationContext(), HomePage.class);
                                k.putExtra("username",Username);
                                k.putExtra("type",type);
                                startActivity(k);
                            }
                        });
                    }
                }
            }
        });

    }


    public void goToRegister(View v){
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }

}
