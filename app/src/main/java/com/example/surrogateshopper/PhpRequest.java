package com.example.surrogateshopper;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhpRequest {
    public void doRequest(final Activity a , String phpFile, final RequestHandler r){
        OkHttpClient client = new OkHttpClient();
        String mainURL = "https://lamp.ms.wits.ac.za/~s2430972/";
        String url = mainURL + phpFile;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String JSON =  response.body().string();
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        r.processResponse(JSON);
                    }
                });
            }
        });

    }

}
