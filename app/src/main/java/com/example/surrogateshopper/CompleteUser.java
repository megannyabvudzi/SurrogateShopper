package com.example.surrogateshopper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompleteUser {

    public void Insert(String requestNumber,String email){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder UB = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1827555/CompleteUser.php").newBuilder();
        UB.addQueryParameter("reqno",requestNumber);
        UB.addQueryParameter("email",email);
        String url = UB.build().toString();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String ans = response.body().string();
                System.out.println(ans);
            }
        });

    }
}
