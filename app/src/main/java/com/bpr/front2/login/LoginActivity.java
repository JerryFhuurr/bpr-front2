package com.bpr.front2.login;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bpr.front2.MainActivity;
import com.bpr.front2.R;
import com.bpr.front2.handler.ActivityManager;
import com.bpr.front2.handler.HttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private TextView forgetPassword;
    private TextView errorLabel;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }

    private void initView() {
        usernameText = findViewById(R.id.username_login);
        passwordText = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.button_login);
        forgetPassword = findViewById(R.id.login_forget);
        errorLabel = findViewById(R.id.login_error_label);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    setInfo(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.login_nullError, Toast.LENGTH_SHORT).show();
                }

            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder resetPassword = new AlertDialog.Builder(LoginActivity.this);
                resetPassword.setTitle(R.string.login_reset_title);
                resetPassword.setMessage(R.string.login_reset_body);
                resetPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                resetPassword.show();
            }
        });
    }


    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.exit_toast,
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.getInstance().exit();
            System.exit(0);
        }
    }

    private String emailEncode(String email) {
        return email.replace(".", ",");
    }


    // HTTP
    private void setInfo(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build();
                String url = HttpUtils.baseUrl1 + "/user/login?username=" + username + "&password=" + password;

                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.i(TAG, "response:"+response.isSuccessful());
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        loginShowResponse(responseBody, username);
                    } else {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);

                    }
                } catch (IOException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorLabel.setText(R.string.e_login_timeout);
                        }
                    });
                    //throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void loginShowResponse(final String responseBody, final String username) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (responseBody.contains("successful")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", username);
                    editor.apply();
                    goToMainActivity();
                } else {
                    errorLabel.setText(responseBody);
                    Log.d(TAG, "Unexpected code " + responseBody);
                }
            }
        });
    }
}