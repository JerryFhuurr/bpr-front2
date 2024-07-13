package com.bpr.front2.home.user.teacher.admin;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.comment.Comment;
import com.bpr.front2.home.user.comment.CommentAdapter;
import com.bpr.front2.home.user.teacher.admin.account.Account;
import com.bpr.front2.home.user.teacher.admin.account.AccountAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManageAccountFragment extends Fragment {
    private ImageButton addAccountButton;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RecyclerView accountR;
    private SwipeRefreshLayout refresh;
    private AccountAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Account> accounts = new ArrayList<>();

    public ManageAccountFragment() {
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
        View v = inflater.inflate(R.layout.fragment_manage_account, container, false);

        SharedPreferences s = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        int userId = s.getInt("userId", 0);

        accountR = v.findViewById(R.id.account_recycler);
        refresh = v.findViewById(R.id.account_refresh);

        getAccountList(userId);

        addAccountButton = v.findViewById(R.id.add_account_button);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ManageAccountFragment.this)
                        .navigate(R.id.action_manageAccountFragment_to_createAccountFragment);
            }
        });
        return v;
    }

    private void getAccountList(int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/user/get/all";
                Request request = new Request.Builder().url(url).get().build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(ContentValues.TAG, responseBody);
                        setAccountList(responseBody, id);
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setAccountList(final String response, int currentId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    accounts.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        if (o.getInt("userId") != currentId) {
                            Account account = new Account();
                            account.setUserId(o.getInt("userId"));
                            account.setRoleId(o.getInt("roleId"));
                            account.setUsername(o.getString("username"));
                            accounts.add(account);
                        }

                    }
                    Log.i(ContentValues.TAG, "list size:" + String.valueOf(accounts.size()));
                    setRecyclerLayout(currentId);
                } catch (JSONException e) {
                    Log.w(ContentValues.TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRecyclerLayout(int id) {
        adapter = new AccountAdapter(accounts);
        layoutManager = new LinearLayoutManager(getContext());
        accountR.setLayoutManager(layoutManager);
        accountR.setAdapter(adapter);

        // 下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新动画开始后 回调此方法

                //设置可见
                refresh.setRefreshing(true);

                getAccountList(id);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟加载时间，设置不可见
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }
}