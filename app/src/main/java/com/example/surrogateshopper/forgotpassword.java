package com.example.surrogateshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class forgotpassword extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

    }

    public void doReset(View v){
        EditText p1 = (EditText)findViewById(R.id.newPassword);
        EditText p2 =(EditText)findViewById(R.id.newPassword2);
        EditText Email = (EditText)findViewById(R.id.Emmail);

        String email = Email.getText().toString();
        String pass1= p1.getText().toString();
        String pass2 = p2.getText().toString();

        if(pass1.trim().equals(pass2)){
            OkHttpClient client = new OkHttpClient();

            RequestBody FormBody =  new FormBody.Builder()
                    .add("email",email)
                    .add("password",pass1)
                    .build();
            String url ="https://lamp.ms.wits.ac.za/home/s1827555/forgotpassword.php";

            Request request = new Request.Builder().url(url).post(FormBody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String fin = response.body().string();
                    System.out.println(fin);
                    if (fin.trim().equalsIgnoreCase("True")){
                        forgotpassword.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Your Password Has Been Reset Successfully", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(forgotpassword.this,Login.class);
                                startActivity(i);
                            }
                        });
                     }
                    else{
                        forgotpassword.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Please Enter Your Correct Email", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"The Passwords do not match. Please try again.",Toast.LENGTH_LONG).show();
        }






    }


}