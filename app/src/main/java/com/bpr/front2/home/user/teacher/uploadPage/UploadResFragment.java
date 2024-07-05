package com.bpr.front2.home.user.teacher.uploadPage;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bpr.front2.R;

public class UploadResFragment extends Fragment {

    private EditText fileNameEdit;
    private Spinner courseList;
    private RecyclerView uploadListView;
    private Button chooseButton;
    private Button uploadButton;

    public UploadResFragment() {
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
        View v = inflater.inflate(R.layout.fragment_upload_res, container, false);
        fileNameEdit = v.findViewById(R.id.file_name_edit);
        courseList = v.findViewById(R.id.course_cat);
        uploadListView = v.findViewById(R.id.upload_list_view);
        chooseButton = v.findViewById(R.id.choose_local);
        uploadButton = v.findViewById(R.id.upload);

        //TODO 等后端完成后添加上传的逻辑代码
        return v;
    }
}