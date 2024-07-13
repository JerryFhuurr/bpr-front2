package com.bpr.front2.home.user.teacher.admin.account;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.CourseAdapter;
import com.bpr.front2.home.user.teacher.admin.CreateAccountFragment;
import com.bpr.front2.login.LoginActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    private ArrayList<Account> accounts = new ArrayList<>();

    public AccountAdapter(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.account_item, parent, false);
        return new AccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userId.setText(String.valueOf(accounts.get(position).getUserId()));
        holder.userName.setText(accounts.get(position).getUsername());
        holder.userRole.setText(accounts.get(position).getRole());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder remove = new AlertDialog.Builder(view.getContext());
                remove.setTitle(R.string.account_remove_title);
                remove.setMessage(R.string.account_remove_alert);
                remove.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeAccount(accounts.get(position));
                    }
                });
                remove.show();
                removeAccount(accounts.get(position));
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder resetPassword = new AlertDialog.Builder(view.getContext());
                final View dialogView = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.dialog_with_edittext,null);
                resetPassword.setTitle(R.string.login_reset_title);
                resetPassword.setView(dialogView);
                resetPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                        EditText passInput = (EditText) dialogView.findViewById(R.id.pass_input);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                        TextView passError = dialogView.findViewById(R.id.pass_error);
                        String password = passInput.getText().toString().trim();
                        SharedPreferences s = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                        String role = s.getString("role", "");
                        String username = accounts.get(position).getUsername();

                        if (password.length() < 6) {
                            passError.setText(R.string.register_password_hint);
                        }
                        updatePassword(role, username, password);
                    }
                });
                resetPassword.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userId, userName, userRole;
        Button edit, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.user_id);
            userName = itemView.findViewById(R.id.user_name);
            userRole = itemView.findViewById(R.id.user_role);
            edit = itemView.findViewById(R.id.user_change);
            remove = itemView.findViewById(R.id.user_remove);
        }
    }

    private void removeAccount(Account account) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/user/remove?id=" + account.getUserId();
                Request request = new Request.Builder().url(url).delete().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(ContentValues.TAG, responseBody);
                        if (responseBody.equals("User successfully removed")) {
                            Log.i(TAG, "account remove");
                            accounts.remove(account);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void updatePassword(String role, String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                if (role.equals("admin")) {
                    String url = HttpUtils.baseUrl1 + "/user/update/password/admin";
                    FormBody.Builder requestBuild = new FormBody.Builder();
                    RequestBody requestBody = requestBuild
                            .add("username", username)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .put(requestBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();

                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.i(ContentValues.TAG, responseBody);

                            if (responseBody.contains("successfully")) {
                                Log.i(TAG, "password update");
                            }
                        }
                    } catch (IOException e) {
                        Log.w(ContentValues.TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

}
