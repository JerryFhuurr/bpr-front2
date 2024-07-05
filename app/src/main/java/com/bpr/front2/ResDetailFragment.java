package com.bpr.front2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bpr.front2.home.user.teacher.uploadPage.FileViewModel;
import com.bpr.front2.home.user.teacher.uploadPage.UploadFileItem;

import java.util.ArrayList;

public class ResDetailFragment extends Fragment {

    private TextView testLabel;
    private FileViewModel fileVideModel;
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
        testLabel = v.findViewById(R.id.detail_test_label);
        fileVideModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(FileViewModel.class);
        fileItems = fileVideModel.getFileItem();
        testLabel.setText(fileItems.get(0).toString());
        return v;
    }
}