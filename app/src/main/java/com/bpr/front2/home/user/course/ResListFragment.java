package com.bpr.front2.home.user.course;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bpr.front2.R;


public class ResListFragment extends Fragment {

    private CourseVideModel courseVideModel;
    private TextView courseNameLabel;
    private CourseItem courseItem;

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
        courseNameLabel = v.findViewById(R.id.list_course_name);

        courseItem = courseVideModel.getCourseItem();
        Log.i(TAG, "course:" + courseItem.getCourseName());
        courseNameLabel.setText(courseItem.getCourseName());

        return v;
    }
}