package com.bpr.front2.home.user.teacher;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bpr.front2.R;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import java.util.ArrayList;


public class TeacherFragment extends Fragment {

    private TextView accountErrorLabel;
    private SharedPreferences sharedPreferences;
    private String userRoleGet;
    private RecyclerView uploadsR;
    private SwipeRefreshLayout refresh;
    private Button openAccountButton;
    private UploadsAdapter adapter;
    private LinearLayoutManager layoutManager;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    ArrayList<UploadItem> items = new ArrayList<>();


    public TeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("user", MODE_PRIVATE);

        for (int i = 1; i <= 50; i++) {
            UploadItem item = new UploadItem();
            item.id = i;
            item.title = "Title " + i; // 设置标题，你可以根据需要修改
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
        adapter = new UploadsAdapter(items);
        layoutManager = new LinearLayoutManager(getContext());

        userRoleGet = sharedPreferences.getString("role", "student");
        if (userRoleGet.equals("student")) {
            accountErrorLabel.setVisibility(View.VISIBLE);
            uploadsR.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            openAccountButton.setVisibility(View.GONE);
        } else {
            accountErrorLabel.setVisibility(View.GONE);
        }

        uploadsR.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        uploadsR.setLayoutManager(layoutManager);
        uploadsR.setAdapter(adapter);


        // 下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新动画开始后 回调此方法

                //设置可见
                refresh.setRefreshing(true);

                //向头部插入数据
                //TODO 后端完成后修改为拉取数据
                ArrayList<UploadItem> newDatas = new ArrayList<UploadItem>();
                for (int i = 0; i < 5; i++) {
                    int index = i + 1;
                    UploadItem item = new UploadItem();
                    item.id = i;
                    item.title = "new item" + index;
                    newDatas.add(item);
                }
                adapter.addItem(newDatas);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟加载时间，设置不可见
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return v;
    }
}