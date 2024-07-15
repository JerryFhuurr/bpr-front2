package com.bpr.front2.home.user;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetPasswordFragment extends Fragment {
    private EditText passOEdit;
    private EditText passNEdit;
    private EditText passREdit;
    private TextView errorLabel;
    private Button applyButton;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        passOEdit = v.findViewById(R.id.password_origin);
        passNEdit = v.findViewById(R.id.password_new);
        passREdit = v.findViewById(R.id.password_new_repeat);
        errorLabel = v.findViewById(R.id.error_label);
        applyButton = v.findViewById(R.id.apply_button);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordO = passOEdit.getText().toString().trim();
                String passwordN = passNEdit.getText().toString().trim();
                String passwordR = passREdit.getText().toString().trim();
                //Log.i(TAG, passwordN + " " + passwordR);
                if (!passwordN.equals(passwordR)) {
                    errorLabel.setText(R.string.login_reset_error1);
                } else {
                    verifyChangePass(passwordO, passwordN);
                }
            }
        });
        return v;
    }


    // HTTP
    private void verifyChangePass(String passwordO, String passwordN) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                String url = HttpUtils.baseUrl1 + "/user/update/password";
                FormBody.Builder requestBuild = new FormBody.Builder();
                RequestBody requestBody = requestBuild
                        .add("username", username)
                        .add("oldPassword", passwordO)
                        .add("newPassword", passwordN)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .put(requestBody)
                        .build();
                Log.i(TAG, request.toString());
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        changePassword(responseBody);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    Looper.prepare();
                    Toast.makeText(getContext(), "Internet error, please check your connection"
                            , Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void changePassword(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.contains("successful")) {
                    Toast.makeText(getContext(), R.string.account_setPasOK, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Unexpected code " + response);
                }
            }
        });
    }
}