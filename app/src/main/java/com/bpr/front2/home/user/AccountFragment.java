package com.bpr.front2.home.user;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountFragment extends Fragment {

    private EditText usernameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText phoneEdit;
    private TextView birthdayText;
    private Button editBirthdayBtn;
    private Button saveBtn;
    private Button changePassBtn;
    private int id;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        getUser(username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        usernameEdit = v.findViewById(R.id.accountName);
        emailEdit = v.findViewById(R.id.emailEdit);
        phoneEdit = v.findViewById(R.id.phoneEdit);
        birthdayText = v.findViewById(R.id.birthdayText);
        editBirthdayBtn = v.findViewById(R.id.account_change_birthday);
        saveBtn = v.findViewById(R.id.update_info);
        changePassBtn = v.findViewById(R.id.change_password);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String phone = phoneEdit.getText().toString().trim();
                String birth = birthdayText.getText().toString().trim();

                saveChangeRequest(username, email, phone, birth);
            }
        });

        editBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AccountFragment.this)
                        .navigate(R.id.action_accountFragment_to_resetPasswordFragment);
            }
        });
        return v;
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // select date
                        String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

                        // save date
                        birthdayText.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    //HTTP
    private void getUser(String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/user/getinfo?username=" + username;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setUserInfo(responseBody);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setUserInfo(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(response);
                    id = object.getInt("userId");
                    String username = object.getString("username");
                    String email = object.getString("email");
                    String phone = object.getString("phone");
                    String birth = object.getString("birth");

                    usernameEdit.setText(username);
                    emailEdit.setText(email);
                    phoneEdit.setText(phone);
                    birthdayText.setText(birth);
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }


    private void saveChangeRequest(String username, String email, String phone
    , String birth) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Map map = new HashMap();
                map.put("userId", String.valueOf(id));
                map.put("username", username);
                map.put("email", email);
                map.put("phone", phone);
                map.put("birth", birth);
                JSONObject jo = new JSONObject(map);
                RequestBody requestBody = RequestBody.create(MediaType.parse(
                        "application/json; charset=utf-8"
                ), jo.toString());
                Log.i(TAG, jo.toString());
                String path = HttpUtils.baseUrl1 + "/user/update/info";
                Request request = new Request.Builder()
                        .url(path)
                        .put(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        saveUserInfo(responseBody, username);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void saveUserInfo(final String response, final String username) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.contains("successfully")) {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", username);
                    editor.apply();
                    Toast.makeText(requireContext(), R.string.account_setOK, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Unexpected code " + response);
                }
            }
        });
    }

}