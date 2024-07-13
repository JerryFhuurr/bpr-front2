package com.bpr.front2.home.user.teacher.admin.account;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.CourseAdapter;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
}
