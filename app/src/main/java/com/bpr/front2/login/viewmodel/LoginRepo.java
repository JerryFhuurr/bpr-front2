package com.bpr.front2.login.viewmodel;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRepo {

    private static LoginRepo instance;
    private final Application app;

    private LoginRepo(Application app) {
        this.app = app;
    }

    public static synchronized LoginRepo getInstance(Application app) {
        if (instance == null) {
            instance = new LoginRepo(app);
        }
        return instance;
    }

    public void setInfo(String username, String password) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.0.150:8080/user/login?username=" + username + "&password=" + password;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "Login fail " + e.getLocalizedMessage());
                Toast.makeText(app.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i(TAG, responseBody);
                if (responseBody.contains("successful")) {
                    SharedPreferences sharedPreferences = app.getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", username);
                    Log.i(TAG, "working 1");
                    editor.apply();
                } else {
                    Log.d(TAG, "Unexpected code " + response);
                    throw new IOException("Unexpected code " + response);
                }
                response.body().close();
            }
        });
    }

}
