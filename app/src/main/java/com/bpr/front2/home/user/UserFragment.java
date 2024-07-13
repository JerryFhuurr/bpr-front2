package com.bpr.front2.home.user;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpr.front2.R;

public class UserFragment extends Fragment {

    private TextView userNameText;
    private Button account;
    private Button about;
    private Button history;
    private Button cache;
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

        account = v.findViewById(R.id.user_account);
        about = v.findViewById(R.id.user_about);
        settings = v.findViewById(R.id.user_settings);
        userNameText = v.findViewById(R.id.user_name);
        history = v.findViewById(R.id.history_button);

        userNameText.setText(sharedPreferences.getString("username", ""));

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_accountFragment);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_aboutFragment);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_historyFragment);
            }
        });

        return v;
    }
}