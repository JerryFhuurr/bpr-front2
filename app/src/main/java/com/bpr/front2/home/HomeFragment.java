package com.bpr.front2.home;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.home.user.course.CourseAdapter;
import com.bpr.front2.home.user.course.CourseItem;
import com.bpr.front2.home.user.course.CourseViewModel;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import java.util.ArrayList;

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
        for (int i = 1; i <= 50; i++) {
            CourseItem item = new CourseItem();
            item.setId(i);
            item.setCourseName("Course " + i); // 设置标题，你可以根据需要修改
            items.add(item);
        }
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
        adapter = new CourseAdapter(items);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", MODE_PRIVATE);
        String usernameGet = sharedPreferences.getString("username", "null");
        usernameLabel.setText(usernameGet);

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

                //向头部插入数据
                //TODO 后端完成后修改为拉取数据
                ArrayList<CourseItem> newDatas = new ArrayList<CourseItem>();
                for (int i = 0; i < 5; i++) {
                    int index = i + 1;
                    CourseItem item = new CourseItem();
                    item.setId(i);
                    item.setCourseName("New Course " + index);
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