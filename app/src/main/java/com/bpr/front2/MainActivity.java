package com.bpr.front2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bpr.front2.handler.ActivityManager;
import com.bpr.front2.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user",MODE_PRIVATE);
        String usernameGet = sharedPreferences.getString("username", "");
        if (usernameGet.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.main_login_info, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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

}