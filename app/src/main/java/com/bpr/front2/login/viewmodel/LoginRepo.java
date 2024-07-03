package com.bpr.front2.login.viewmodel;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRepo {

    private static LoginRepo instance;
    private final Application app;
    private CountDownLatch latch; // 添加 CountDownLatch

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
        latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();

                Request request = new Request.Builder()
                        .url("http://39.101.134.253:8080/test/users/testc/post")
                        .post(formBody)
                        .build();

                Response response = null;

                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        SharedPreferences sharedPreferences = app.getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("role", "student");
                        editor.apply();
                        Log.d(TAG, "working");
                    } else {
                        Log.d(TAG, "Unexpected code " + response);
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Unexpected code " + e);
                    throw new RuntimeException(e);
                }
                latch.countDown();
            }
        }).start();
    }

    public void awaitCompletion() throws InterruptedException {
        latch.await(); // 等待异步操作完成
    }
}
