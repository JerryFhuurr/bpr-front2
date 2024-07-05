package com.bpr.front2.home.user.course;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.home.HomeFragment;
import com.bpr.front2.home.user.teacher.uploadPage.FileViewModel;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import java.util.ArrayList;


public class ResListFragment extends Fragment {

    private CourseVideModel courseVideModel;
    private FileViewModel fileViewModel;

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

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_res_list, container, false);
        courseVideModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(CourseVideModel.class);
        fileViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(FileViewModel.class);
        courseNameLabel = v.findViewById(R.id.list_course_name);
        courseItem = courseVideModel.getCourseItem();
        courseNameLabel.setText(courseItem.getCourseName());
        //TODO 后期修改
        courseVideModel.setUploadItems();
        items = courseVideModel.getUploadItems();

        uploadsR = v.findViewById(R.id.uploads_recycle);
        refresh = v.findViewById(R.id.uploads_refresh);
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
                Toast.makeText(getContext(), uploadItem.title, Toast.LENGTH_SHORT).show();
                fileViewModel.setFileItem(uploadItem.title);
                NavHostFragment.findNavController(ResListFragment.this).navigate(R.id.action_resListFragment_to_resDetailFragment);
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
                items = courseVideModel.getUploadItems();
                uploadsR.setAdapter(adapter);

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