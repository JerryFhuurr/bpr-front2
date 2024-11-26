package com.bpr.front2.home.user.teacher;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.resource.ResListFragment;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TeacherFragment extends Fragment {

    private TextView accountErrorLabel;
    private SharedPreferences sharedPreferences;
    private String userRoleGet;
    private RecyclerView uploadsR;
    private SwipeRefreshLayout refresh;
    private Button openAccountButton;
    private Button uploadButton;
    private UploadsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private UploadItemVideoModel uploadItemVideoModel;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    ArrayList<UploadItem> items = new ArrayList<>();


    public TeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 1; i <= 50; i++) {
            UploadItem item = new UploadItem();
            item.resId = i;
            item.resTitle = "Title " + i; // 设置标题，你可以根据需要修改
            items.add(item);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher, container, false);
        accountErrorLabel = v.findViewById(R.id.account_error_label);
        uploadsR = v.findViewById(R.id.uploads_recycle);
        refresh = v.findViewById(R.id.uploads_refresh);
        openAccountButton = v.findViewById(R.id.manage_account_button);
        uploadButton = v.findViewById(R.id.upload_resources_button);
        layoutManager = new LinearLayoutManager(getContext());
        uploadItemVideoModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(UploadItemVideoModel.class);

        sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        userRoleGet = sharedPreferences.getString("role", "student");
        int userId = sharedPreferences.getInt("userId", 0);
        Log.i(TAG, userRoleGet);

        if (userRoleGet.equals("student")) {
            accountErrorLabel.setVisibility(View.VISIBLE);
            uploadsR.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            openAccountButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
        } else {
            accountErrorLabel.setVisibility(View.GONE);
            if (userRoleGet.equals("admin")) {
                openAccountButton.setVisibility(View.VISIBLE);
            } else {
                openAccountButton.setVisibility(View.GONE);
            }
        }

        openAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TeacherFragment.this)
                        .navigate(R.id.action_teacherFragment_to_manageAccountFragment);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TeacherFragment.this)
                        .navigate(R.id.action_teacherFragment_to_uploadResFragment);
            }
        });

        getResList(userId);
        return v;
    }

    private void getResList(int userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                String url = HttpUtils.baseUrl1 + "/res/get/list/user?userId=" + userId;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setResList(responseBody, userId);
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

    private void setResList(final String response, final int userId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        UploadItem item = new UploadItem();
                        item.resTitle = o.getString("resTitle");
                        item.resId = o.getInt("resId");
                        items.add(item);
                    }
                    setRefreshView(userId);
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRefreshView(int userId) {
        adapter = new UploadsAdapter(items);
        uploadsR.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        uploadsR.setLayoutManager(layoutManager);
        uploadsR.setAdapter(adapter);

        //点击事件
        adapter.setOnItemClickListener(new UploadsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UploadItem uploadItem = items.get(position);
                uploadItemVideoModel.setUploadItem(uploadItem);
                NavHostFragment.findNavController(TeacherFragment.this)
                        .navigate(R.id.action_teacherFragment_to_resDetailFragment);
                Toast.makeText(getContext(), uploadItem.resTitle, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        // 下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新动画开始后 回调此方法

                //设置可见
                refresh.setRefreshing(true);

                //向头部插入数据
                getResList(userId);
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