package com.bpr.front2.home.user.course.resource;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bpr.front2.R;
import com.bpr.front2.home.user.teacher.uploadPage.FileViewModel;
import com.bpr.front2.home.user.teacher.uploadPage.UploadFileItem;

import java.util.ArrayList;

public class ResDetailFragment extends Fragment {
    private FileViewModel fileVideModel;
    private RecyclerView uploadItemVIew;
    private SwipeRefreshLayout commentRefresh;
    private RecyclerView commentRecycler;
    private Button uploadButton;

    ArrayList<UploadFileItem> fileItems = new ArrayList<>();

    public ResDetailFragment() {
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
        View v = inflater.inflate(R.layout.fragment_res_detail, container, false);
        fileVideModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(FileViewModel.class);
        fileItems = fileVideModel.getFileItem();

        uploadItemVIew = v.findViewById(R.id.upload_list_view);
        commentRecycler = v.findViewById(R.id.comment_recycler_view);
        commentRefresh = v.findViewById(R.id.comment_refresh_view);
        uploadButton = v.findViewById(R.id.commit_comment_button);

        //TODO 后续修改按钮的逻辑代码（目前仅作测试用）
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResDetailFragment.this)
                        .navigate(R.id.action_resDetailFragment_to_videoPlayerFragment);
            }
        });

        // TODO 添加评论相关的adapter
        // TODO 添加发布评论的代码
        // TODO 添加拉取资源列表的代码
        return v;
    }

}