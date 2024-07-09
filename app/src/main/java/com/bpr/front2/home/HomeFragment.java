package com.bpr.front2.home;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.CourseAdapter;
import com.bpr.front2.home.user.course.CourseItem;
import com.bpr.front2.home.user.course.CourseViewModel;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private CourseViewModel courseViewModel;
    private TextView usernameLabel;
    private RecyclerView courseR;
    private SwipeRefreshLayout refresh;
    private CourseAdapter adapter;
    private GridLayoutManager layoutManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    ArrayList<CourseItem> items = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        courseViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(CourseViewModel.class);
        usernameLabel = v.findViewById(R.id.home_username);
        courseR = v.findViewById(R.id.home_course_recycle);
        refresh = v.findViewById(R.id.home_course_refresh);

        int itemsSize = items.size();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", MODE_PRIVATE);
        String usernameGet = sharedPreferences.getString("username", "null");
        usernameLabel.setText(usernameGet);

        getCourseList(usernameGet);

        return v;
    }


    //HTTP
    private void getCourseList(String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/course/get/user?username=" + username;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setCourseList(responseBody, username);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setCourseList(final String response, final String username) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    items.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        CourseItem courseItem = new CourseItem();
                        courseItem.setId(o.getInt("courseId"));
                        courseItem.setCourseName(o.getString("courseName"));
                        items.add(courseItem);
                    }
                    Log.i(TAG, String.valueOf(items.size()));
                    setRecyclerLayout(username);
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRecyclerLayout(String usernameGet) {
        adapter = new CourseAdapter(items);
        layoutManager = new GridLayoutManager(getContext(), 4);
        courseR.setLayoutManager(layoutManager);
        courseR.setAdapter(adapter);

        //点击事件
        adapter.setOnItemClickListener(new UploadsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CourseItem uploadItem = items.get(position);
                Toast.makeText(getContext(), uploadItem.getCourseName(), Toast.LENGTH_SHORT).show();
                courseViewModel.setCourseItem(uploadItem);
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_resListFragment);
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

                getCourseList(usernameGet);
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