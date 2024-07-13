package com.bpr.front2.home.user.history;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.comment.Comment;
import com.bpr.front2.home.user.comment.CommentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryFragment extends Fragment {

    private SwipeRefreshLayout refresh;
    private RecyclerView listView;

    private HistoryAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayList<History> histories = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        refresh = v.findViewById(R.id.history_list_refresh);
        listView = v.findViewById(R.id.history_list);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        int watcherId = sharedPreferences.getInt("userId", 0);
        getHistoryList(watcherId);

        return v;
    }

    private void getHistoryList(int watcherId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/history/get?watcherId=" + watcherId;
                Request request = new Request.Builder().url(url).get().build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setHistoryList(responseBody, watcherId);
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setHistoryList(final String response, final int watcherId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    histories.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        History history = new History();
                        history.setUserId(o.getInt("userId"));
                        history.setCourseId(o.getInt("courseId"));
                        history.setVideoId(o.getInt("videoId"));
                        history.setRoleId(o.getInt("roleId"));
                        history.setWatcherId(o.getInt("watcherId"));
                        history.setWatchTime(o.getLong("watchTime"));
                        history.setVideoTitle(o.getString("videoTitle"));
                        history.setUpName(o.getString("upName"));
                        history.setHId(o.getInt("hid"));
                        histories.add(history);
                    }
                    Log.i(TAG, String.valueOf(histories.size()));
                    setRecyclerLayout(watcherId);
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRecyclerLayout(int watcherId) {
        adapter = new HistoryAdapter(histories);
        layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);

        // 下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新动画开始后 回调此方法

                //设置可见
                refresh.setRefreshing(true);

                getHistoryList(watcherId);
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