package com.bpr.front2.home.user.course.resource;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.CourseItem;
import com.bpr.front2.home.user.course.CourseViewModel;
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


public class ResListFragment extends Fragment {

    private CourseViewModel courseViewModel;
    private UploadItemVideoModel uploadItemVideoModel;
    private EditText searchEdit;
    private TextView courseNameLabel;
    private CourseItem courseItem;
    private RecyclerView uploadsR;
    private SwipeRefreshLayout refresh;
    private UploadsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    ArrayList<UploadItem> items = new ArrayList<>();

    public ResListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(CourseViewModel.class);
        courseItem = courseViewModel.getCourseItem();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_res_list, container, false);

        uploadItemVideoModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(UploadItemVideoModel.class);
        courseNameLabel = v.findViewById(R.id.list_course_name);

        courseNameLabel.setText(courseItem.getCourseName());
        uploadsR = v.findViewById(R.id.uploads_recycle);
        refresh = v.findViewById(R.id.uploads_refresh);
        searchEdit = v.findViewById(R.id.search_edit);

        loadItems();
        return v;
    }

    private void loadItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                String url = HttpUtils.baseUrl1 + "/video/get/list?courseId=" + courseItem.getId();
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setItems(responseBody);
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

    private void setItems(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    items.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        UploadItem uploadItem = new UploadItem();
                        uploadItem.videoId = o.getInt("videoId");
                        uploadItem.videoTitle = o.getString("videoTitle");
                        items.add(uploadItem);
                    }
                    Log.i(TAG, String.valueOf(items.size()));
                    setRecyclerLayout();
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRecyclerLayout() {
        adapter = new UploadsAdapter(items);
        layoutManager = new LinearLayoutManager(getContext());

        uploadsR.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        uploadsR.setLayoutManager(layoutManager);
        uploadsR.setAdapter(adapter);

        //点击事件
        adapter.setOnItemClickListener(new UploadsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UploadItem uploadItem = items.get(position);
                uploadItemVideoModel.setUploadItem(uploadItem);
                NavHostFragment.findNavController(ResListFragment.this)
                        .navigate(R.id.action_resListFragment_to_resDetailFragment);
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

                loadItems();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟加载时间，设置不可见
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        setSearch();
    }

    private void setSearch() {
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // delay 8ms then request
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // no op
                    }
                }, 800);
                if (editable.toString().isEmpty()) {
                    loadItems();
                } else {
                    adapter.searchItem(editable.toString());
                }
            }
        });
    }
}