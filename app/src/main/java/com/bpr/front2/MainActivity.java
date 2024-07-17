package com.bpr.front2;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bpr.front2.handler.ActivityManager;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration configuration;
    private static boolean isFirst = true;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTempVideoPath();
        setContentView(R.layout.activity_main);
        checkUser();
        initView();
        setupNavigation();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.main_drawer);
        bottomNavigationView = findViewById(R.id.bottom_navbar);
        toolbar = findViewById(R.id.topBar);

        //SharedPreferences sharedPreferences =
        //        PreferenceManager.getDefaultSharedPreferences(this);
        //appThemeLoad(sharedPreferences, this);

        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        setSupportActionBar(toolbar);

        configuration = new AppBarConfiguration.Builder(R.id.homeFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setBottomNavigationView();
    }

    private void setBottomNavigationView() {
        navController.addOnDestinationChangedListener(((navController1, navDestination, bundle) -> {
            final int id = navDestination.getId();
            if (id == R.id.homeFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
            //else if (id == R.id.myFragment) {
            //    bottomNavigationView.setVisibility(View.VISIBLE);
            //    SoundHandler.playSoundClick();
            else if (id == R.id.teacherFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else if (id == R.id.userFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            }
        }));
    }


    private void checkUser() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        String usernameGet = sharedPreferences.getString("username", "");
        if (usernameGet.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.main_login_info, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            getUserR(usernameGet);
        }
    }

    public void logoutUser(View v) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        checkUser();
    }

    public void exitApp(View v) {
        finish();
        ActivityManager.getInstance().exit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, configuration) || super.onSupportNavigateUp();
    }

    private void appThemeLoad(SharedPreferences sharedPreferences, Activity a) {
        String appTheme = sharedPreferences.getString("theme_type", "follow_system");
        if (appTheme.equals("follow_system")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (appTheme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (appTheme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    //HTTP
    private void getUserR(String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                String url = HttpUtils.baseUrl1 + "/user/getinfo?username=" + username;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setRole(responseBody);
                    }
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setRole(final String responseBody) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject o = new JSONObject(responseBody);
                    String role = o.getString("role");
                    int userId = o.getInt("userId");

                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("role", role);
                    editor.putInt("userId", userId);
                    editor.apply();

                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void createTempVideoPath() {
        String cPackagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo";
        File file = new File(cPackagePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

}