package com.bpr.front2.home.user;

import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpr.front2.R;
import com.bpr.front2.handler.GeneralUtils;

public class UserFragment extends Fragment {

    private TextView userNameText, welcomeBackText;
    private Button account;
    private Button history;
    private Button settings;
    private ImageView iconView;
    private SharedPreferences sharedPreferences;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("user", MODE_PRIVATE);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        iconView = v.findViewById(R.id.user_userIcon);

        welcomeBackText = v.findViewById(R.id.welcome_back_label);
        account = v.findViewById(R.id.user_account);
        settings = v.findViewById(R.id.user_settings);
        userNameText = v.findViewById(R.id.user_name);
        history = v.findViewById(R.id.history_button);

        userNameText.setText(sharedPreferences.getString("username", ""));

        String time = GeneralUtils.checkTime();
        Log.i(TAG, "time:" + time);
        if ("morning".equals(time)) {
            welcomeBackText.setText("Good morning !");
        } else if ("afternoon".equals(time)) {
            welcomeBackText.setText("Good afternoon !");
        } else if ("evening".equals(time)) {
            welcomeBackText.setText("Good evening !");
        } else if ("night".equals(time)) {
            welcomeBackText.setText("Good night !");
        } else {
            welcomeBackText.setText("Hello !");
        }


        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_accountFragment);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_historyFragment);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_settingsFragment);
            }
        });

        return v;
    }
}