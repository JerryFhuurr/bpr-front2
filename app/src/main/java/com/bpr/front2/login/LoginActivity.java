package com.bpr.front2.login;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bpr.front2.MainActivity;
import com.bpr.front2.R;
import com.bpr.front2.handler.ActivityManager;
import com.bpr.front2.login.viewmodel.LoginRepo;
import com.bpr.front2.login.viewmodel.LoginViewmodel;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewmodel mViewModel;
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
        mViewModel = new ViewModelProvider(this).get(LoginViewmodel.class);
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
                AlertDialog.Builder resetPassword = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogReset);
                resetPassword.setTitle(R.string.login_reset_title);
                resetPassword.setMessage(R.string.login_reset_body);
                final EditText reset_email = new EditText(getApplicationContext());
                resetPassword.setView(reset_email);
                resetPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = reset_email.getText().toString();
                        String emailE = emailEncode(email);
                        Log.v("email replace", emailE);

                        //TODO 添加发送HTTP请求以重置密码的代码（等后端完成后添加）
//                        mAuth.sendPasswordResetEmail(email)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Log.d(TAG, "Email sent.");
//                                            Toast.makeText(getContext(), R.string.login_reset_ok, Toast.LENGTH_SHORT).show();
//                                        }
//                                        //add error if the email isn't exist
//                                    }
//                                });

                    }
                });

                resetPassword.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
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
                OkHttpClient client = new OkHttpClient();
                String url = "http://192.168.0.150:8080/user/login?username=" + username + "&password=" + password;

                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        loginShowResponse(responseBody, username);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
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